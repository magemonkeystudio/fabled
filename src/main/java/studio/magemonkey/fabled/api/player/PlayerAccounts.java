/**
 * Fabled
 * studio.magemonkey.fabled.api.player.PlayerAccounts
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 MageMonkeyStudio
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package studio.magemonkey.fabled.api.player;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.event.PlayerAccountChangeEvent;
import studio.magemonkey.fabled.cast.CastMode;
import studio.magemonkey.fabled.manager.ClassBoardManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents the collection of accounts owned by a single player.
 * Most of the time, this class won't be used by other plugins as
 * you can skip directly to a player's active data using the
 * Fabled.getData methods. This would be if you want
 * to extend functionality for handling the inactive accounts.
 */
public class PlayerAccounts {
    private final Map<Integer, PlayerData> classData = new HashMap<>();
    private final UUID                     player;
    private       int                      active;
    @Getter
    @Setter
    @Accessors(fluent = true)
    private       boolean                  isLoaded  = false;

    /**
     * Initializes a new container for player account data.
     * This shouldn't be used by other plugins as the API
     * provides one for each player already.
     *
     * @param player player to store data for
     */
    public PlayerAccounts(OfflinePlayer player) {
        this.player = player.getUniqueId();

        PlayerData data = new PlayerData(player, true);
        classData.put(1, data);
        active = 1;
    }

    /**
     * Retrieves the active account ID for the player
     *
     * @return active account ID
     */
    public int getActiveId() {
        return active;
    }

    /**
     * Retrieves the active account data for the player
     *
     * @return active account data
     */
    public PlayerData getActiveData() {
        return classData.get(active);
    }

    /**
     * Gets the Bukkit player object for the owner of the data
     *
     * @return Bukkit player object or null if offline/dead
     */
    public Player getPlayer() {
        return Bukkit.getPlayer(player);
    }

    /**
     * Gets the Bukkit offline player object for the owner of the data
     *
     * @return Bukkit offline player object
     */
    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(player);
    }

    /**
     * Gets the name of the owner of the data
     *
     * @return owner's name
     */
    public String getPlayerName() {
        return getOfflinePlayer().getName();
    }

    /**
     * Retrieves the max amount of accounts the owner can use
     *
     * @return available account number
     */
    public int getAccountLimit() {
        return Fabled.getSettings().getMaxAccounts(getPlayer());
    }

    /**
     * Checks whether there is any data for the given account ID. If
     * the player has not switched to the account, there will be no data
     * unless the setting to initialize one account for each class is enabled.
     *
     * @param id account ID
     * @return true if data exists, false otherwise
     */
    public boolean hasData(int id) {
        return classData.containsKey(id);
    }

    /**
     * Gets the account data by ID for the owner
     *
     * @param id account ID
     * @return account data or null if not found
     */
    public PlayerData getData(int id) {
        return classData.get(id);
    }

    /**
     * Gets the account data by ID for the owner. If no data
     * exists under the given ID, new data is created as long
     * as the ID is a positive integer (not necessarily in
     * bounds for the player's allowed accounts).
     *
     * @param id     account ID
     * @param player offline player reference
     * @param init   whether the data is being initialized
     * @return account data or null if invalid id or player
     */
    public PlayerData getData(int id, OfflinePlayer player, boolean init) {
        if (!hasData(id) && id > 0 && player != null) {
            classData.put(id, new PlayerData(player, init));
        }

        return classData.get(id);
    }

    /**
     * Retrieves all the data for the owner. Modifying this map will
     * alter the player's actual data.
     *
     * @return all account data for the player
     */
    public Map<Integer, PlayerData> getAllData() {
        return classData;
    }

    /**
     * Switches the active account for the player by ID. This will not accept
     * IDs outside the player's account limits. If the player is offline or
     * dead, this will not do anything.
     *
     * @param id ID of the account to switch to
     */
    public void setAccount(int id) {
        setAccount(id, true);
    }

    /**
     * Switches the active account for the player by ID. This will not accept
     * IDs outside the player's account limits. If the player is offline or
     * dead, this will not do anything.
     *
     * @param id    ID of the account to switch to
     * @param apply whether to apply the switch
     */
    public void setAccount(int id, boolean apply) {
        Player player = getPlayer();
        if (player == null || id == active || !apply) {
            active = id;
            return;
        }
        if (id <= getAccountLimit() && id > 0 && !classData.containsKey(id)) {
            classData.put(id, new PlayerData(player, false));
        }
        if (classData.containsKey(id)) {
            PlayerAccountChangeEvent event = new PlayerAccountChangeEvent(this, active, id);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }

            if (Fabled.getSettings().isWorldEnabled(player.getWorld())) {
                ClassBoardManager.clear(player);
                getActiveData().stopSkills(player);
                getActiveData().clearAllModifiers();
                active = event.getNewID();
                getActiveData().startPassives(player);
                getActiveData().updateScoreboard();
                if (getActiveData().hasClass() && Fabled.getSettings().isSkillBarEnabled() && !Fabled.getSettings()
                        .getCastMode()
                        .equals(CastMode.COMBAT)) {
                    getActiveData().getSkillBar().setup(player);
                }
                getActiveData().getEquips().update(player);
                getActiveData().updatePlayerStat(player);
                // Do a force health update, as health haven't been altered
                // updatePlayerStat will not do a wasteful health update
                getActiveData().updateHealth(player);
            } else {
                active = event.getNewID();
            }
        }
    }
}
