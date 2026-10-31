package studio.magemonkey.fabled.data.io;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.player.PlayerAccounts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

/**
 * Cache of loaded {@link PlayerAccounts}, keyed by player UUID.
 *
 * <p>The cache is used from the server thread, from async unload tasks and from the auto-save
 * thread. Loading and unloading are compound sequences around file or database I/O, so they are
 * made mutually exclusive per player by lock stripes, while unrelated players still load and save
 * in parallel. A thread never holds more than one stripe: the I/O layer fires Bukkit events, and a
 * listener that asks for another player's data runs without taking a second stripe rather than
 * risking a lock-ordering deadlock.</p>
 *
 * <p>An unload that runs some time after the player quits is tracked by a ticket, so a rejoin in
 * the meantime can cancel it and keep the cached accounts, instead of the unload saving and evicting
 * them from under the new session.</p>
 */
public class PlayerLoader {

    /**
     * Number of lock stripes. Must be a power of two.
     */
    private static final int STRIPE_COUNT = 128;

    private static final Object[] STRIPES = new Object[STRIPE_COUNT];

    static {
        for (int i = 0; i < STRIPE_COUNT; i++) {
            STRIPES[i] = new Object();
        }
    }

    private static final ThreadLocal<Boolean> holdingStripe = ThreadLocal.withInitial(() -> false);

    private static final Map<UUID, PlayerAccounts> cachedPlayers = new ConcurrentHashMap<>();

    /**
     * The ticket of each player's pending unload. Entries only live between a quit and its unload
     * or the next rejoin, whichever comes first.
     */
    private static final Map<UUID, Long> pendingUnloads = new ConcurrentHashMap<>();
    private static final AtomicLong      unloadTickets  = new AtomicLong();

    /**
     * Retrieves the cached accounts for the player, loading them from storage if they are not
     * cached yet.
     *
     * <p>Cache hits are lock-free, so the server thread never waits on another thread's save. A
     * miss takes the player's stripe and checks again before loading, so a player is loaded once
     * and never while an unload of the same player is in progress. A lookup for a player who has
     * just quit can still return the instance their unload is about to evict.</p>
     *
     * @param player player to get the accounts for
     * @return the player's accounts, or null if storage could not produce any
     */
    public static PlayerAccounts getPlayerAccounts(OfflinePlayer player) {
        UUID           id     = player.getUniqueId();
        PlayerAccounts cached = cachedPlayers.get(id);
        if (cached != null) {
            return cached;
        }
        return withStripe(id, () -> {
            PlayerAccounts accounts = cachedPlayers.get(id);
            return accounts != null ? accounts : load(player, id);
        });
    }

    /**
     * Loads the player's accounts from storage, replacing anything currently cached for them.
     *
     * @param player player to load the accounts for
     */
    public static void loadPlayer(OfflinePlayer player) {
        UUID id = player.getUniqueId();
        withStripe(id, () -> load(player, id));
    }

    /**
     * Discards the player's cached accounts and loads them again from storage, losing anything not
     * saved since they were loaded.
     *
     * <p>If an unload of the player is still pending, it runs first, so the progress it was going to
     * save is kept and it cannot evict the reloaded accounts later.</p>
     *
     * @param player player to reload
     */
    public static void reloadPlayer(OfflinePlayer player) {
        UUID id = player.getUniqueId();
        withStripe(id, () -> {
            if (pendingUnloads.remove(id) != null) save(id, true);
            load(player, id);
        });
    }

    /**
     * Saves the player's accounts and drops them from the cache.
     *
     * <p>The built-in backends log and swallow write failures, so the entry is dropped even if the
     * save did not succeed.</p>
     *
     * @param player player to unload
     */
    public static void unloadPlayer(OfflinePlayer player) {
        UUID id = player.getUniqueId();
        withStripe(id, () -> save(id, true));
    }

    /**
     * Registers an unload of the player that will run later, such as from an async task after they
     * quit. Pass the returned ticket to {@link #unloadPlayer(OfflinePlayer, long)}. Registering a
     * new unload supersedes any earlier one that is still pending.
     *
     * @param player player who is going to be unloaded
     * @return the ticket of the unload
     */
    public static long prepareUnload(OfflinePlayer player) {
        long ticket = unloadTickets.incrementAndGet();
        pendingUnloads.put(player.getUniqueId(), ticket);
        return ticket;
    }

    /**
     * Saves the player's accounts and drops them from the cache, unless the unload has been
     * cancelled by {@link #cancelPendingUnload(OfflinePlayer)} or superseded since it was prepared.
     *
     * @param player player to unload
     * @param ticket ticket returned by {@link #prepareUnload(OfflinePlayer)}
     */
    public static void unloadPlayer(OfflinePlayer player, long ticket) {
        UUID id = player.getUniqueId();
        withStripe(id, () -> {
            if (pendingUnloads.remove(id, ticket)) save(id, true);
        });
    }

    /**
     * Cancels the player's pending unload, for a player who rejoins before it has run. This takes
     * the player's stripe, so an unload that is already saving finishes and evicts first, and a
     * later lookup loads the saved data.
     *
     * @param player player who rejoined
     * @return true if an unload was cancelled and the accounts it would have evicted are still cached
     */
    public static boolean cancelPendingUnload(OfflinePlayer player) {
        UUID id = player.getUniqueId();
        return withStripe(id, () -> pendingUnloads.remove(id) != null && cachedPlayers.containsKey(id));
    }

    /**
     * Checks whether accounts are currently cached for the player.
     *
     * @param player player to check for
     * @return true if the player's accounts are cached
     */
    public static boolean hasPlayerAccounts(OfflinePlayer player) {
        return cachedPlayers.containsKey(player.getUniqueId());
    }

    /**
     * Saves every cached player's accounts.
     */
    public static void saveAllPlayerAccounts() {
        saveAllPlayerAccounts(false);
    }

    /**
     * Saves every cached player's accounts, one stripe at a time.
     *
     * <p>When {@code clearCache} is set, each entry is dropped under the stripe that saved it, so a
     * player who joins during the pass stays cached instead of being discarded unsaved.</p>
     *
     * @param clearCache whether to drop each player from the cache once saved
     */
    public static void saveAllPlayerAccounts(boolean clearCache) {
        for (UUID id : new ArrayList<>(cachedPlayers.keySet())) {
            withStripe(id, () -> save(id, clearCache));
        }
    }

    /**
     * Retrieves a snapshot of every cached player's accounts. The map is a copy; the accounts in it
     * are the live instances.
     *
     * @return an unmodifiable snapshot of the cache
     */
    public static Map<UUID, PlayerAccounts> getAllPlayerAccounts() {
        return Collections.unmodifiableMap(new HashMap<>(cachedPlayers));
    }

    /**
     * Loads the accounts of every online player.
     */
    public static void loadAllPlayerAccounts() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            loadPlayer(player);
        }
    }

    static int stripeIndex(UUID id) {
        int hash = id.hashCode();
        // Only the low bits pick the stripe, so fold the high bits into them
        return (hash ^ (hash >>> 16)) & (STRIPE_COUNT - 1);
    }

    /**
     * Runs the action while holding the player's stripe, or without locking if this thread already
     * holds one.
     */
    private static <T> T withStripe(UUID id, Supplier<T> action) {
        if (holdingStripe.get()) {
            return action.get();
        }
        synchronized (STRIPES[stripeIndex(id)]) {
            holdingStripe.set(true);
            try {
                return action.get();
            } finally {
                holdingStripe.remove();
            }
        }
    }

    private static void withStripe(UUID id, Runnable action) {
        withStripe(id, () -> {
            action.run();
            return null;
        });
    }

    private static PlayerAccounts load(OfflinePlayer player, UUID id) {
        PlayerAccounts accounts = Fabled.getIO().loadData(player);
        if (accounts == null) {
            // Storage returns null for some players (e.g. SQL players with no name), and a
            // ConcurrentHashMap cannot hold null, so leave the entry absent
            cachedPlayers.remove(id);
        } else {
            cachedPlayers.put(id, accounts);
        }
        return accounts;
    }

    private static void save(UUID id, boolean drop) {
        PlayerAccounts accounts = cachedPlayers.get(id);
        if (accounts != null) {
            Fabled.getIO().saveData(accounts);
            if (drop) cachedPlayers.remove(id);
        }
    }
}
