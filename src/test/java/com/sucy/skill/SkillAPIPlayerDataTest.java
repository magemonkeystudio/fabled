package com.sucy.skill;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
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
import studio.magemonkey.fabled.data.io.IOManager;
import studio.magemonkey.fabled.data.io.PlayerLoader;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * The deprecated player data methods of {@link SkillAPI} from
 * <a href="https://github.com/magemonkeystudio/fabled/issues/1897">#1897</a>, which used to call themselves
 * until {@link StackOverflowError}.
 *
 * <p>Runs without a Bukkit server: {@code Fabled.singleton} is swapped reflectively for a mock whose
 * scheduler queues async tasks and whose {@link IOManager} is a fake, hence {@link Isolated}.</p>
 */
@Isolated
class SkillAPIPlayerDataTest {

    private static final UUID ID = new UUID(0L, 1897L);

    private static Fabled previousSingleton;
    private static FakeIO io;

    private static final List<Runnable> asyncTasks = new CopyOnWriteArrayList<>();

    private final Player player = player();

    @BeforeAll
    static void installFakeFabled() throws Exception {
        BukkitScheduler scheduler = mock(BukkitScheduler.class);
        when(scheduler.runTaskAsynchronously(any(Plugin.class), any(Runnable.class))).thenAnswer(inv -> {
            asyncTasks.add(inv.getArgument(1));
            return mock(BukkitTask.class);
        });
        Server server = mock(Server.class);
        when(server.getScheduler()).thenReturn(scheduler);

        Fabled fabled = mock(Fabled.class);
        when(fabled.getServer()).thenReturn(server);
        io = new FakeIO(fabled);
        setField(Fabled.class, fabled, "io", io);

        previousSingleton = (Fabled) getField(Fabled.class, null, "singleton");
        setField(Fabled.class, null, "singleton", fabled);
    }

    @AfterAll
    static void restoreFabled() throws Exception {
        clearLoaderState();
        setField(Fabled.class, null, "singleton", previousSingleton);
    }

    @BeforeEach
    void resetState() throws Exception {
        clearLoaderState();
        asyncTasks.clear();
        io.reset();
    }

    @Test
    void unloadPlayerData_savesAndEvictsTheCachedAccounts() {
        PlayerAccounts accounts = Fabled.getPlayerAccounts(player);

        SkillAPI.unloadPlayerData(player);
        assertEquals(1, asyncTasks.size(), "the unload was not scheduled");
        runAsyncTasks();

        assertEquals(List.of(accounts), io.saves);
        assertNull(cached());
    }

    @Test
    void unloadPlayerData_withNothingCached_doesNothing() {
        SkillAPI.unloadPlayerData(player);
        runAsyncTasks();

        assertTrue(io.loads.isEmpty());
        assertTrue(io.saves.isEmpty());
    }

    @Test
    void reloadPlayerData_discardsTheCachedAccountsAndLoadsFromStorage() {
        PlayerAccounts accounts = Fabled.getPlayerAccounts(player);

        SkillAPI.reloadPlayerData(player);

        assertEquals(2, io.loads.size());
        assertNotSame(accounts, cached());
        assertSame(io.loads.get(1), cached());
        assertTrue(io.saves.isEmpty(), "the discarded accounts were saved");
    }

    @Test
    void reloadPlayerData_withNothingCached_loadsFromStorage() {
        SkillAPI.reloadPlayerData(player);

        assertEquals(1, io.loads.size());
        assertSame(io.loads.get(0), cached());
    }

    /**
     * A reload while the unload of a quit is still pending must not lose the progress the unload was
     * going to save, and the late unload must not evict the reloaded accounts.
     */
    @Test
    void reloadPlayerData_withAPendingUnload_runsTheUnloadFirst() throws Exception {
        PlayerAccounts accounts = Fabled.getPlayerAccounts(player);
        Fabled.unloadPlayerData(player, false);

        SkillAPI.reloadPlayerData(player);
        assertEquals(List.of(accounts), io.saves, "the pending unload's save was lost");
        PlayerAccounts reloaded = cached();
        assertNotNull(reloaded);
        assertNotSame(accounts, reloaded);

        runAsyncTasks();
        assertEquals(List.of(accounts), io.saves, "the stale unload saved again");
        assertSame(reloaded, cached(), "the stale unload evicted the reloaded accounts");
        assertTrue(pendingUnloads().isEmpty());
    }

    /**
     * The reload takes the player's stripe, so it waits for an unload that is already saving instead of
     * loading underneath it, and loads what was saved.
     */
    @Test
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    void reloadPlayerData_whileTheUnloadIsSaving_waitsForIt() throws Exception {
        PlayerAccounts accounts = Fabled.getPlayerAccounts(player);
        Fabled.unloadPlayerData(player, false);

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

        Thread reload = new Thread(() -> SkillAPI.reloadPlayerData(player), "reload");
        reload.setDaemon(true);
        reload.start();
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
        while (reload.isAlive() && reload.getState() != Thread.State.BLOCKED && System.nanoTime() < deadline) {
            Thread.onSpinWait();
        }
        assertEquals(1, io.loads.size(), "the reload loaded while the unload was saving");
        release.countDown();
        unload.join(TimeUnit.SECONDS.toMillis(10));
        reload.join(TimeUnit.SECONDS.toMillis(10));
        assertFalse(unload.isAlive() || reload.isAlive(), "the unload or the reload never finished");

        assertEquals(List.of(accounts), io.saves);
        assertEquals(2, io.loads.size());
        assertSame(io.loads.get(1), cached(), "the unload evicted the reloaded accounts");
    }

    private static Player player() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(ID);
        when(player.getName()).thenReturn("legacy");
        return player;
    }

    private static void runAsyncTasks() {
        while (!asyncTasks.isEmpty()) {
            asyncTasks.remove(0).run();
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
        pendingUnloads().clear();
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

    /** Every load returns a new accounts instance, so the tests can tell copies apart by identity. */
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
