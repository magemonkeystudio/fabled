package studio.magemonkey.fabled.data.io;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.player.PlayerAccounts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache of loaded {@link PlayerAccounts}, keyed by player UUID.
 *
 * <p>This cache is touched by at least three threads:</p>
 * <ul>
 *     <li>the server main thread, when a player joins ({@code MainListener#onJoin} &rarr;
 *     {@code Fabled#getData}) or whenever any code asks for player data;</li>
 *     <li>an async Bukkit thread, when a player quits ({@code Fabled#unloadPlayerData} dispatches
 *     {@link #unloadPlayer(OfflinePlayer)} via {@code runTaskAsynchronously});</li>
 *     <li>Fabled's own {@code MainThread}, which runs {@code SaveTask} when {@code auto-save} is on.</li>
 * </ul>
 *
 * <p>The map itself is therefore concurrent, but a concurrent map is not enough on its own: the
 * sequences performed here are compound (check &rarr; load &rarr; put, and get &rarr; save &rarr;
 * remove) and the save/load steps perform file or database I/O. Those sequences are made mutually
 * exclusive <em>per player</em> by a small array of lock stripes, so a join and a quit for the same
 * player can never interleave, while unrelated players still load and save in parallel. No global
 * lock is ever held across I/O.</p>
 */
public class PlayerLoader {

    /**
     * Number of lock stripes. Must be a power of two.
     *
     * <p>Only one stripe is ever held at a time and only for the duration of a single player's
     * load or save, so the worst case for an unrelated caller that collides on a stripe is waiting
     * out one file write - not the whole save pass, and not a global lock.</p>
     */
    private static final int STRIPE_COUNT = 128;

    private static final Object[] STRIPES = new Object[STRIPE_COUNT];

    static {
        for (int i = 0; i < STRIPE_COUNT; i++) {
            STRIPES[i] = new Object();
        }
    }

    private static final Map<UUID, PlayerAccounts> cachedPlayers = new ConcurrentHashMap<>();

    /**
     * Resolves the lock guarding the cache entry for the given player.
     *
     * @param id player UUID
     * @return the stripe to synchronize on for that player
     */
    private static Object lockFor(UUID id) {
        int hash = id.hashCode();
        // Spread the higher bits down, since only the low bits index the stripe array
        hash ^= (hash >>> 16);
        return STRIPES[hash & (STRIPE_COUNT - 1)];
    }

    /**
     * Retrieves the cached accounts for the player, loading them from storage if they are not
     * cached yet.
     *
     * <p>The lock is taken even when the entry is likely to be present. Reading the map without it
     * would let a fast rejoin hand back an instance that a concurrent unload is about to evict and
     * write to disk, leaving the returned object detached from the cache.</p>
     *
     * @param player player to get the accounts for
     * @return the player's accounts, or null if storage could not produce any
     */
    public static PlayerAccounts getPlayerAccounts(OfflinePlayer player) {
        UUID id = player.getUniqueId();
        synchronized (lockFor(id)) {
            PlayerAccounts accounts = cachedPlayers.get(id);
            if (accounts == null) {
                accounts = load(player, id);
            }
            return accounts;
        }
    }

    /**
     * Loads the player's accounts from storage, replacing anything currently cached for them.
     *
     * @param player player to load the accounts for
     */
    public static void loadPlayer(OfflinePlayer player) {
        UUID id = player.getUniqueId();
        synchronized (lockFor(id)) {
            load(player, id);
        }
    }

    /**
     * Reads the player's accounts from storage and caches them. Must be called while holding the
     * player's stripe.
     *
     * @param player player to load the accounts for
     * @param id     the player's UUID
     * @return the loaded accounts, or null if storage returned nothing
     */
    private static PlayerAccounts load(OfflinePlayer player, UUID id) {
        PlayerAccounts accounts = Fabled.getIO().loadData(player);
        if (accounts == null) {
            // FabledPlayersSQL#loadPlayerAccounts returns null for a player with no known name.
            // ConcurrentHashMap rejects null values, so leave the entry absent rather than throwing
            // from inside the map - the caller sees the same null it saw before.
            cachedPlayers.remove(id);
            return null;
        }
        cachedPlayers.put(id, accounts);
        return accounts;
    }

    /**
     * Saves the player's accounts and drops them from the cache.
     *
     * <p>Saving before removing is deliberate: if the write fails, the entry stays cached and a
     * later save pass can still persist it. Removing first would narrow the window in which another
     * bulk save could pick the same player up, but it would turn a failed write into lost data.</p>
     *
     * @param player player to unload
     */
    public static void unloadPlayer(OfflinePlayer player) {
        UUID id = player.getUniqueId();
        synchronized (lockFor(id)) {
            PlayerAccounts accounts = cachedPlayers.get(id);
            if (accounts != null) {
                Fabled.getIO().saveData(accounts);
                cachedPlayers.remove(id);
            }
        }
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
     * Saves every cached player's accounts, optionally emptying the cache afterwards.
     *
     * <p>Each player is saved while holding only that player's stripe, and the stripe is released
     * before moving on to the next one. That keeps this from racing an unload of the same player
     * onto the same file without ever blocking every player at once.</p>
     *
     * @param clearCache whether to empty the cache once everything is saved
     */
    public static void saveAllPlayerAccounts(boolean clearCache) {
        List<UUID> ids = new ArrayList<>(cachedPlayers.keySet());
        for (UUID id : ids) {
            synchronized (lockFor(id)) {
                // Re-read under the lock; the player may have been unloaded since the snapshot
                PlayerAccounts accounts = cachedPlayers.get(id);
                if (accounts != null) {
                    Fabled.getIO().saveData(accounts);
                }
            }
        }
        if (clearCache) cachedPlayers.clear();
    }

    /**
     * Retrieves a snapshot of every cached player's accounts.
     *
     * <p>This is a copy, not the live cache. Callers iterate the result while players are joining
     * and quitting on other threads, so handing out the backing map exposed them to entries
     * appearing and disappearing mid-iteration. The accounts themselves are shared, not copied.</p>
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
}
