package studio.magemonkey.fabled.data.io;

import org.bukkit.OfflinePlayer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.player.PlayerAccounts;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Regression tests for <a href="https://github.com/magemonkeystudio/fabled/issues/1795">#1795</a>.
 *
 * <p>{@link PlayerLoader} used to hold its cache in a plain {@code TreeMap} while the main thread
 * loaded players on join and an async thread saved-and-removed them on quit. Interleaving those
 * mutations could corrupt the red-black tree into a cycle, after which a lookup on the main thread
 * span forever and the server stopped ticking.</p>
 *
 * <p>These tests run without a Bukkit server: {@code Fabled.singleton} is replaced with a stand-in
 * whose {@link IOManager} is a fake that reports how long it spends "on disk", and players are
 * lightweight proxies that only answer {@code getUniqueId()}.</p>
 */
class PlayerLoaderConcurrencyTest {

    /** Enough players that different UUIDs land on different lock stripes. */
    private static final int PLAYER_COUNT = 16;

    private static Fabled     fabled;
    private static Fabled     previousSingleton;
    private static FakeIO     io;
    private static List<UUID> uuids;
    /** Stable accounts instance per player, so a wrong lookup is detectable by identity. */
    private static Map<UUID, PlayerAccounts> expected;
    private static Map<UUID, OfflinePlayer>  players;

    @BeforeAll
    static void installFakeFabled() throws Exception {
        uuids = new ArrayList<>();
        expected = new HashMap<>();
        players = new HashMap<>();
        for (int i = 0; i < PLAYER_COUNT; i++) {
            UUID id = UUID.randomUUID();
            uuids.add(id);
            expected.put(id, mock(PlayerAccounts.class));
            players.put(id, offlinePlayer(id));
        }
        uuids = Collections.unmodifiableList(uuids);

        fabled = mock(Fabled.class);
        io = new FakeIO(fabled);

        Field ioField = Fabled.class.getDeclaredField("io");
        ioField.setAccessible(true);
        ioField.set(fabled, io);

        Field singletonField = Fabled.class.getDeclaredField("singleton");
        singletonField.setAccessible(true);
        previousSingleton = (Fabled) singletonField.get(null);
        singletonField.set(null, fabled);
    }

    @AfterAll
    static void restoreFabled() throws Exception {
        clearCache();
        Field singletonField = Fabled.class.getDeclaredField("singleton");
        singletonField.setAccessible(true);
        singletonField.set(null, previousSingleton);
    }

    @BeforeEach
    void resetState() throws Exception {
        clearCache();
        io.reset();
    }

    @SuppressWarnings("unchecked")
    private static void clearCache() throws Exception {
        Field cache = PlayerLoader.class.getDeclaredField("cachedPlayers");
        cache.setAccessible(true);
        ((Map<UUID, PlayerAccounts>) cache.get(null)).clear();
    }

    /**
     * The whole of #1795 in one run.
     *
     * <p>Under the old {@code TreeMap} this either threw from inside the map internals (the reporter
     * saw {@code NullPointerException} in {@code fixAfterDeletion}), threw
     * {@link java.util.ConcurrentModificationException} out of the reader threads, or - once the
     * tree had been corrupted into a cycle - span forever inside {@code getEntry}, which is what
     * froze the server. The worker assertions cover the first two and the timeout covers the spin.
     * The overlap counter separately checks the invariant that keeps a rejoin from reading a data
     * file that the quit is still writing.</p>
     */
    @Test
    @Timeout(value = 120, unit = TimeUnit.SECONDS)
    void concurrentLoadAndUnload_doesNotCorruptTheCache() throws Exception {
        List<Throwable> failures = runStress(8, 8, 4, 1500);

        assertTrue(failures.isEmpty(), () -> "concurrent access failed: " + describe(failures));
        assertEquals(0, io.overlaps.get(),
                "load and save overlapped for a single player - the per-player lock is not holding");
    }

    /**
     * {@code getAllPlayerAccounts} used to return the live cache, so the four call sites that
     * iterate it raced the async unloader. It now returns a snapshot.
     */
    @Test
    void getAllPlayerAccounts_returnsAnIndependentUnmodifiableSnapshot() {
        UUID first  = uuids.get(0);
        UUID second = uuids.get(1);
        PlayerLoader.getPlayerAccounts(players.get(first));

        Map<UUID, PlayerAccounts> snapshot = PlayerLoader.getAllPlayerAccounts();
        assertEquals(1, snapshot.size());

        // Mutating the cache afterwards must not be visible through the snapshot
        PlayerLoader.getPlayerAccounts(players.get(second));
        PlayerLoader.unloadPlayer(players.get(first));

        assertEquals(1, snapshot.size());
        assertTrue(snapshot.containsKey(first));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.remove(first));
    }

    /**
     * A bulk clear must not discard players who joined during the pass.
     *
     * <p>Clearing the whole map at the end of {@code saveAllPlayerAccounts(true)} would drop a
     * player whose stripe had already been visited before they joined. They were never saved, and
     * {@code unloadPlayer} skips players it finds nothing cached for, so their session would be
     * lost outright when they quit.</p>
     */
    @Test
    void saveAllWithClear_doesNotDiscardAPlayerThatJoinedDuringThePass() {
        UUID joiner = uuids.get(0);
        UUID cached = uuids.get(1);
        PlayerLoader.getPlayerAccounts(players.get(cached));

        // Join from inside the pass, at the point where the joining player's stripe may already
        // have been visited
        io.onSave = () -> PlayerLoader.getPlayerAccounts(players.get(joiner));
        PlayerLoader.saveAllPlayerAccounts(true);
        io.onSave = null;

        assertTrue(PlayerLoader.hasPlayerAccounts(players.get(joiner)),
                "a player who joined during the clearing pass was dropped without being saved");
        assertSame(expected.get(joiner), PlayerLoader.getPlayerAccounts(players.get(joiner)));
    }

    /**
     * The I/O layer is not a leaf. {@code IOManager#load} fires Bukkit events - {@code setClass} →
     * {@code updatePlayerStat} → {@code PlayerMaxManaChangeEvent} - and a third-party listener may
     * ask for another player's data from inside one. Two threads doing that for two players in
     * opposite orders would deadlock if each could hold two stripes, so a nested request must not
     * take a second one. Without that guard this test hangs rather than fails.
     */
    @Test
    @Timeout(value = 60, unit = TimeUnit.SECONDS)
    void reentrantLookupForAnotherPlayer_fromInsideTheIoLayer_doesNotDeadlock() throws Exception {
        UUID a = uuids.get(0);
        UUID b = uuids.get(1);

        // Each load reaches back into the loader for the *other* player, as a listener would
        io.onLoad = id -> {
            UUID other = id.equals(a) ? b : a;
            PlayerLoader.getPlayerAccounts(players.get(other));
        };

        List<Throwable> failures = new CopyOnWriteArrayList<>();
        CountDownLatch  start    = new CountDownLatch(1);
        List<Thread>    threads  = List.of(
                worker("nested-a", start, failures, i -> {
                    PlayerLoader.unloadPlayer(players.get(a));
                    assertSame(expected.get(a), PlayerLoader.getPlayerAccounts(players.get(a)));
                }, 400),
                worker("nested-b", start, failures, i -> {
                    PlayerLoader.unloadPlayer(players.get(b));
                    assertSame(expected.get(b), PlayerLoader.getPlayerAccounts(players.get(b)));
                }, 400));

        threads.forEach(Thread::start);
        start.countDown();
        for (Thread thread : threads) {
            thread.join(TimeUnit.SECONDS.toMillis(30));
            if (thread.isAlive()) {
                failures.add(new AssertionError(thread.getName() + " never finished - deadlocked "
                        + "acquiring a second stripe"));
            }
        }
        io.onLoad = null;

        assertTrue(failures.isEmpty(), () -> "nested lookup deadlocked or failed: " + describe(failures));
    }

    /**
     * Hammers the loader from several threads at once and returns everything that went wrong.
     *
     * @param loaders   threads calling {@code getPlayerAccounts}, as a join would
     * @param unloaders threads calling {@code unloadPlayer}, as an async quit would
     * @param readers   threads iterating {@code getAllPlayerAccounts} and running a full save pass
     * @param rounds    iterations per thread
     * @return the failures observed across all threads
     */
    private List<Throwable> runStress(int loaders, int unloaders, int readers, int rounds) throws Exception {
        List<Throwable> failures = new CopyOnWriteArrayList<>();
        List<Thread>    threads  = new ArrayList<>();
        CountDownLatch  start    = new CountDownLatch(1);

        for (int i = 0; i < loaders; i++) {
            threads.add(worker("loader-" + i, start, failures, seed -> {
                UUID           id       = uuids.get(seed % PLAYER_COUNT);
                PlayerAccounts accounts = PlayerLoader.getPlayerAccounts(players.get(id));
                assertSame(expected.get(id), accounts, "wrong accounts returned for " + id);
            }, rounds));
        }
        for (int i = 0; i < unloaders; i++) {
            // Offset so unloaders chase the loaders around the player set instead of tracking them
            int offset = i + 1;
            threads.add(worker("unloader-" + i, start, failures, seed -> {
                UUID id = uuids.get((seed + offset) % PLAYER_COUNT);
                PlayerLoader.unloadPlayer(players.get(id));
            }, rounds));
        }
        for (int i = 0; i < readers; i++) {
            boolean saver = (i % 2 == 0);
            threads.add(worker("reader-" + i, start, failures, seed -> {
                if (saver) {
                    PlayerLoader.saveAllPlayerAccounts(false);
                } else {
                    for (Map.Entry<UUID, PlayerAccounts> entry : PlayerLoader.getAllPlayerAccounts().entrySet()) {
                        assertSame(expected.get(entry.getKey()), entry.getValue(),
                                "snapshot held the wrong accounts for " + entry.getKey());
                    }
                }
            }, rounds));
        }

        threads.forEach(Thread::start);
        start.countDown();
        for (Thread thread : threads) {
            thread.join(TimeUnit.SECONDS.toMillis(90));
            if (thread.isAlive()) {
                failures.add(new AssertionError(thread.getName() + " never finished - "
                        + "the cache is most likely corrupted into a cycle"));
            }
        }
        return failures;
    }

    private Thread worker(String name,
                          CountDownLatch start,
                          List<Throwable> failures,
                          Round round,
                          int rounds) {
        Thread thread = new Thread(() -> {
            try {
                start.await();
                for (int i = 0; i < rounds; i++) {
                    round.run(i);
                }
            } catch (Throwable t) {
                failures.add(t);
            }
        }, name);
        thread.setDaemon(true);
        return thread;
    }

    private static String describe(List<Throwable> failures) {
        StringBuilder sb = new StringBuilder();
        for (Throwable t : failures) {
            sb.append('\n').append(t);
        }
        return sb.toString();
    }

    private interface Round {
        void run(int iteration) throws Exception;
    }

    private static OfflinePlayer offlinePlayer(UUID id) {
        InvocationHandler handler = (proxy, method, args) -> {
            switch (method.getName()) {
                case "getUniqueId":
                    return id;
                case "getName":
                    return id.toString();
                case "hashCode":
                    return id.hashCode();
                case "equals":
                    return proxy == args[0];
                case "toString":
                    return "OfflinePlayer(" + id + ")";
                default:
                    return null;
            }
        };
        return (OfflinePlayer) Proxy.newProxyInstance(PlayerLoaderConcurrencyTest.class.getClassLoader(),
                new Class<?>[]{OfflinePlayer.class},
                handler);
    }

    /**
     * Stands in for the real IO layer. Every call spends a little time "on disk", which is what
     * makes the race in the original code reachable, and records whether two calls were ever inside
     * the layer for the same player at once.
     */
    private static class FakeIO extends IOManager {

        private final Map<UUID, AtomicInteger> inFlight = new ConcurrentHashMap<>();
        private final AtomicInteger            overlaps = new AtomicInteger();

        /** Fired from inside a load, standing in for a Bukkit event with a third-party listener. */
        private volatile Consumer<UUID> onLoad;
        /** Fired from inside a save, standing in for a player joining mid-pass. */
        private volatile Runnable       onSave;
        /** A listener triggered by a load fires its own load; only go one level deep. */
        private final ThreadLocal<Boolean> inHook = ThreadLocal.withInitial(() -> false);

        FakeIO(Fabled api) {
            super(api);
        }

        void reset() {
            inFlight.clear();
            overlaps.set(0);
            onLoad = null;
            onSave = null;
        }

        @Override
        public Map<UUID, PlayerAccounts> loadAll() {
            return new HashMap<>();
        }

        @Override
        public PlayerAccounts loadData(OfflinePlayer player) {
            UUID id = player.getUniqueId();
            enter(id);
            try {
                Consumer<UUID> hook = onLoad;
                if (hook != null && !inHook.get()) {
                    inHook.set(true);
                    try {
                        hook.accept(id);
                    } finally {
                        inHook.set(false);
                    }
                }
                return expected.get(id);
            } finally {
                exit(id);
            }
        }

        @Override
        public void saveData(PlayerAccounts data) {
            UUID id = idOf(data);
            enter(id);
            try {
                Runnable hook = onSave;
                if (hook != null && !inHook.get()) {
                    inHook.set(true);
                    try {
                        hook.run();
                    } finally {
                        inHook.set(false);
                    }
                }
            } finally {
                exit(id);
            }
        }

        private void enter(UUID id) {
            AtomicInteger count = inFlight.computeIfAbsent(id, key -> new AtomicInteger());
            if (count.incrementAndGet() != 1) {
                overlaps.incrementAndGet();
            }
            // Stand in for the file write that widens the window in the real code
            LockSupport.parkNanos(20_000L);
        }

        private void exit(UUID id) {
            inFlight.get(id).decrementAndGet();
        }

        /** The fake never mutates the accounts, so identity is enough to recover the UUID. */
        private UUID idOf(PlayerAccounts data) {
            for (Map.Entry<UUID, PlayerAccounts> entry : expected.entrySet()) {
                if (entry.getValue() == data) return entry.getKey();
            }
            throw new IllegalStateException("saveData called with accounts that were never loaded");
        }
    }
}
