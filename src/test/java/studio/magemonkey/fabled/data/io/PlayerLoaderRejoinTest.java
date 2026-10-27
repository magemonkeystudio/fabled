package studio.magemonkey.fabled.data.io;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.parallel.Isolated;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.player.PlayerAccounts;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.data.Settings;
import studio.magemonkey.fabled.listener.MainListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Regression tests for <a href="https://github.com/magemonkeystudio/fabled/issues/1809">#1809</a>: a
 * player who rejoins before the async unload of their quit has run must keep the accounts their new
 * session is bound to, instead of the unload saving and evicting them.
 *
 * <p>Drives the real join and quit handlers without a Bukkit server. {@code Fabled.singleton} and
 * Bukkit's server are swapped reflectively for mocks whose scheduler queues tasks, so each test decides
 * when the async unload and the delayed SQL load run. Hence {@link Isolated}.</p>
 */
@Isolated
class PlayerLoaderRejoinTest {

    private static final UUID ID        = new UUID(0L, 42L);
    private static final int  SQL_DELAY = 20;

    private static Fabled   previousSingleton;
    private static Server   previousServer;
    private static Settings settings;
    private static FakeIO   io;

    private static final List<Runnable>  asyncTasks   = new CopyOnWriteArrayList<>();
    private static final List<Scheduled> delayedTasks = new CopyOnWriteArrayList<>();
    /** The accounts whose active data the most recent join initialised. */
    private static final AtomicReference<PlayerAccounts> bound = new AtomicReference<>();

    private final MainListener listener = new MainListener();

    @BeforeAll
    static void installFakeServer() throws Exception {
        BukkitScheduler scheduler = mock(BukkitScheduler.class);
        when(scheduler.runTaskAsynchronously(any(Plugin.class), any(Runnable.class))).thenAnswer(inv -> {
            asyncTasks.add(inv.getArgument(1));
            return mock(BukkitTask.class);
        });
        when(scheduler.runTaskLater(any(Plugin.class), any(Runnable.class), anyLong())).thenAnswer(inv -> {
            Scheduled task = new Scheduled(inv.getArgument(1));
            delayedTasks.add(task);
            return task.handle;
        });
        Server server = mock(Server.class);
        when(server.getScheduler()).thenReturn(scheduler);

        Fabled fabled = mock(Fabled.class);
        when(fabled.getServer()).thenReturn(server);
        // GUITool builds a NamespacedKey from the plugin on first use; match the real plugin's
        when(fabled.namespace()).thenReturn("fabled");
        settings = mock(Settings.class);
        when(settings.isWorldEnabled(any(World.class))).thenReturn(true);
        io = new FakeIO(fabled);
        setField(Fabled.class, fabled, "io", io);
        setField(Fabled.class, fabled, "settings", settings);

        previousSingleton = (Fabled) getField(Fabled.class, null, "singleton");
        setField(Fabled.class, null, "singleton", fabled);
        previousServer = (Server) getField(Bukkit.class, null, "server");
        setField(Bukkit.class, null, "server", server);
    }

    @AfterAll
    static void restoreServer() throws Exception {
        clearLoaderState();
        setField(Fabled.class, null, "singleton", previousSingleton);
        setField(Bukkit.class, null, "server", previousServer);
    }

    @BeforeEach
    void resetState() throws Exception {
        clearLoaderState();
        listener.cleanup();
        asyncTasks.clear();
        delayedTasks.clear();
        bound.set(null);
        io.reset();
        useSqlDelay(false);
    }

    /**
     * Ordering 1 from the issue: the rejoin takes the still-cached accounts, then the late unload used
     * to save and evict them, so the next lookup loaded a second copy.
     */
    @Test
    void rejoinBeforeTheUnloadRuns_keepsTheAccountsTheSessionIsBoundTo() {
        Player first = join();
        PlayerAccounts accounts = cached();
        quit(first);

        join();
        runAsyncTasks();

        assertSame(accounts, bound.get());
        assertSame(accounts, cached(), "the stale unload evicted the accounts the rejoined session is bound to");
        assertEquals(1, io.loads.size(), "the player's accounts were loaded a second time");
        assertTrue(io.saves.isEmpty(), "the stale unload still saved");
    }

    /**
     * Ordering 2 from the issue: with a SQL delay, the rejoin's delayed load used to replace the cached
     * accounts with a copy read from storage before they had been saved, and the late unload then
     * evicted that copy.
     */
    @Test
    void rejoinWithSqlDelay_delayedLoadBeforeTheUnload_keepsTheUnsavedAccounts() {
        useSqlDelay(true);
        Player first = join();
        runDelayedTasks();
        PlayerAccounts accounts = cached();
        quit(first);

        Player second = join();
        runDelayedTasks();
        runAsyncTasks();

        assertSame(accounts, bound.get(), "the rejoin was bound to a copy read before the accounts were saved");
        assertSame(accounts, cached(), "the stale unload evicted the accounts the rejoined session is bound to");
        assertEquals(1, io.loads.size(), "the player's accounts were loaded a second time");

        quit(second);
        runAsyncTasks();
        assertEquals(List.of(accounts), io.saves);
        assertNull(cached());
    }

    @Test
    void rejoinWithSqlDelay_unloadBeforeTheDelayedLoad_keepsTheUnsavedAccounts() {
        useSqlDelay(true);
        Player first = join();
        runDelayedTasks();
        PlayerAccounts accounts = cached();
        quit(first);

        join();
        runAsyncTasks();
        runDelayedTasks();

        assertSame(bound.get(), cached(), "the rejoined session is not bound to the cached accounts");
        assertTrue(cached() == accounts || io.saves.equals(List.of(accounts)),
                "the first session's accounts were neither kept nor saved before the reload");
    }

    /**
     * Two unloads are pending at once. Only the one for the player's last quit may act, since the player
     * rejoined after the first.
     */
    @Test
    void quitRejoinQuit_onlyTheLastUnloadActs() {
        Player first = join();
        PlayerAccounts accounts = cached();
        quit(first);
        Player second = join();
        quit(second);
        assertEquals(2, asyncTasks.size());

        asyncTasks.remove(0).run();
        assertTrue(io.saves.isEmpty(), "the first unload acted although the player rejoined after it");
        assertSame(accounts, cached());

        asyncTasks.remove(0).run();
        assertEquals(List.of(accounts), io.saves);
        assertNull(cached());
    }

    @Test
    void quitRejoinQuitRejoin_keepsTheAccountsTheSessionIsBoundTo() {
        Player first = join();
        PlayerAccounts accounts = cached();
        quit(first);
        quit(join());
        join();
        runAsyncTasks();

        assertSame(accounts, bound.get());
        assertSame(accounts, cached(), "a stale unload evicted the accounts the rejoined session is bound to");
        assertTrue(io.saves.isEmpty(), "a stale unload still saved");
    }

    /**
     * The rejoin must not take the cached accounts while the unload is between saving and evicting
     * them. It waits for the unload instead, then loads what was saved.
     */
    @Test
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    void rejoinWhileTheUnloadIsSaving_bindsToTheReloadedAccounts() throws Exception {
        quit(join());
        PlayerAccounts accounts = cached();

        CountDownLatch saving  = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        io.onSave = () -> {
            saving.countDown();
            await(release);
        };
        Thread unload = new Thread(asyncTasks.remove(0), "async-unload");
        unload.setDaemon(true);
        unload.start();
        assertTrue(saving.await(10, TimeUnit.SECONDS), "the unload never started saving");

        Thread rejoin = new Thread(this::join, "rejoin");
        rejoin.setDaemon(true);
        rejoin.start();
        // Fixed code blocks the rejoin on the player's stripe; let it get there before the save ends
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
        while (rejoin.isAlive() && rejoin.getState() != Thread.State.BLOCKED && System.nanoTime() < deadline) {
            Thread.onSpinWait();
        }
        release.countDown();
        unload.join(TimeUnit.SECONDS.toMillis(10));
        rejoin.join(TimeUnit.SECONDS.toMillis(10));
        assertFalse(unload.isAlive() || rejoin.isAlive(), "the unload or the rejoin never finished");

        assertEquals(List.of(accounts), io.saves);
        assertSame(bound.get(), cached(), "the rejoined session is bound to accounts the unload evicted");
        assertEquals(2, io.loads.size());
    }

    /**
     * {@code MainListener#unload} cancels a load that is still waiting out the SQL delay and skips
     * saving. A rejoin after that must load from storage as before.
     */
    @Test
    void quitDuringTheSqlDelay_cancelsTheLoadAndTheRejoinLoadsAgain() {
        useSqlDelay(true);
        Player first = join();
        Scheduled pending = delayedTasks.get(0);
        quit(first);
        runAsyncTasks();

        assertTrue(pending.cancelled, "the pending load was not cancelled");
        assertTrue(io.saves.isEmpty(), "data was saved although its load had been cancelled");

        join();
        runDelayedTasks();
        PlayerAccounts reloaded = io.loads.get(io.loads.size() - 1);
        assertEquals(2, io.loads.size(), "the rejoin did not load after the SQL delay");
        assertSame(reloaded, bound.get());
        assertSame(reloaded, cached());
    }

    /**
     * Accounts cached by a lookup while the player was elsewhere, for example a placeholder, may be
     * older than what another server has since saved. The SQL delay exists to reload those.
     */
    @Test
    void accountsCachedForAnOfflinePlayer_areStillReloadedAfterTheSqlDelay() {
        useSqlDelay(true);
        Fabled.getData(player());
        PlayerAccounts lookedUp = cached();

        join();
        runDelayedTasks();

        assertEquals(2, io.loads.size());
        assertSame(io.loads.get(1), bound.get());
        assertSame(io.loads.get(1), cached());
        assertNotSame(lookedUp, cached());
    }

    @Test
    void pendingUnloads_doNotOutliveTheirUnloadOrRejoin() throws Exception {
        for (int i = 0; i < 50; i++) {
            Player player = join();
            quit(player);
            if (i % 2 == 0) runAsyncTasks();
        }
        runAsyncTasks();
        assertTrue(pendingUnloads().isEmpty(), "pending unloads left behind: " + pendingUnloads());
        assertNull(cached());

        quit(join());
        join();
        assertTrue(pendingUnloads().isEmpty(), "a rejoin left the cancelled unload behind");
    }

    private Player join() {
        Player          player = player();
        PlayerJoinEvent event  = mock(PlayerJoinEvent.class);
        when(event.getPlayer()).thenReturn(player);
        listener.onJoin(event);
        return player;
    }

    private void quit(Player player) {
        PlayerQuitEvent event = mock(PlayerQuitEvent.class);
        when(event.getPlayer()).thenReturn(player);
        listener.onQuit(event);
    }

    /** Bukkit hands out a new {@link Player} on every login. */
    private static Player player() {
        Player          player    = mock(Player.class);
        PlayerInventory inventory = mock(PlayerInventory.class);
        when(inventory.getContents()).thenReturn(new ItemStack[0]);
        when(player.getUniqueId()).thenReturn(ID);
        when(player.getName()).thenReturn("rejoiner");
        when(player.getWorld()).thenReturn(mock(World.class));
        when(player.getInventory()).thenReturn(inventory);
        return player;
    }

    private static void useSqlDelay(boolean enabled) {
        when(settings.isUseSql()).thenReturn(enabled);
        when(settings.getSqlDelay()).thenReturn(enabled ? SQL_DELAY : 0);
    }

    private static void runAsyncTasks() {
        while (!asyncTasks.isEmpty()) {
            asyncTasks.remove(0).run();
        }
    }

    private static void runDelayedTasks() {
        while (!delayedTasks.isEmpty()) {
            Scheduled task = delayedTasks.remove(0);
            if (!task.cancelled) task.runnable.run();
        }
    }

    /** Reads the cache directly, since a lookup would load on a miss. */
    @SuppressWarnings("unchecked")
    private static PlayerAccounts cached() {
        try {
            return ((Map<UUID, PlayerAccounts>) getField(PlayerLoader.class, null, "cachedPlayers")).get(ID);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<UUID, Long> pendingUnloads() throws ReflectiveOperationException {
        return (Map<UUID, Long>) getField(PlayerLoader.class, null, "pendingUnloads");
    }

    @SuppressWarnings("unchecked")
    private static void clearLoaderState() throws ReflectiveOperationException {
        ((Map<UUID, PlayerAccounts>) getField(PlayerLoader.class, null, "cachedPlayers")).clear();
        try {
            pendingUnloads().clear();
        } catch (NoSuchFieldException ignored) {
            // Not present before #1809
        }
        MainListener.loadingPlayers.clear();
    }

    private static void await(CountDownLatch latch) {
        try {
            latch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static Object getField(Class<?> owner, Object target, String name) throws ReflectiveOperationException {
        Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(target);
    }

    private static void setField(Class<?> owner, Object target, String name, Object value)
            throws ReflectiveOperationException {
        Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static class Scheduled {
        private final    Runnable   runnable;
        private final    BukkitTask handle = mock(BukkitTask.class);
        private volatile boolean    cancelled;

        Scheduled(Runnable runnable) {
            this.runnable = runnable;
            doAnswer(inv -> {
                cancelled = true;
                return null;
            }).when(handle).cancel();
        }
    }

    /**
     * Stands in for the real IO layer. Every load returns a new accounts instance, so the tests can
     * tell copies apart by identity, and records which instance a join initialised.
     */
    private static class FakeIO extends IOManager {

        private final List<PlayerAccounts> loads = new CopyOnWriteArrayList<>();
        private final List<PlayerAccounts> saves = new CopyOnWriteArrayList<>();

        /** Fired from inside a save, while the unload holds the player's stripe. */
        private volatile Runnable onSave;

        FakeIO(Fabled api) {
            super(api);
        }

        void reset() {
            loads.clear();
            saves.clear();
            onSave = null;
        }

        @Override
        public Map<UUID, PlayerAccounts> loadAll() {
            return new HashMap<>();
        }

        @Override
        public PlayerAccounts loadData(OfflinePlayer player) {
            PlayerAccounts accounts = mock(PlayerAccounts.class);
            PlayerData     data     = mock(PlayerData.class);
            when(accounts.getActiveData()).thenReturn(data);
            doAnswer(inv -> {
                bound.set(accounts);
                return null;
            }).when(data).init(any(Player.class));
            loads.add(accounts);
            return accounts;
        }

        @Override
        public void saveData(PlayerAccounts data) {
            Runnable hook = onSave;
            if (hook != null) hook.run();
            saves.add(data);
        }
    }
}
