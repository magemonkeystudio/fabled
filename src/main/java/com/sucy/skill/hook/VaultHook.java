/**
 * SkillAPI
 * com.sucy.skill.hook.VaultHook
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2014 Steven Sucy
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
package com.sucy.skill.hook;

import com.sucy.skill.SkillAPI;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Manages setting permissions through vault
 */
public class VaultHook {

    private static Permission permission;
    private static Economy    economy;
    private static boolean    checked = false;

    /**
     * Initializes the permissions and economy manager
     */
    private static void initialize() {
        try {
            RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
            if (permissionProvider != null) {
                permission = permissionProvider.getProvider();
            }
            RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
            if (economyProvider != null) {
                economy = economyProvider.getProvider();
            }
        } catch (NoClassDefFoundError e) {
            if (permission == null)
                SkillAPI.inst().getLogger().info(
                        "Vault permissions not found. Permissions support as such will not be provided");
            if (economy == null)
                SkillAPI.inst().getLogger().info(
                        "Vault economy not found. Economy support as such will not be provided");
        }
    }

    // Permissions

    /**
     * Checks whether or not the Vault reference is valid with a permissions plugin
     *
     * @return true if valid, false otherwise
     */
    public static boolean isPermissionsValid() {
        if (!checked) {
            initialize();
            checked = true;
        }
        return permission != null;
    }

    /**
     * Adds a permission to the player
     *
     * @param player player to add to
     * @param node   permission node to add
     */
    public static void addPermission(Player player, String node) {
        permission.playerAdd(player, node);
    }

    /**
     * Removes a permission from the player
     *
     * @param player player to remove from
     * @param node   permission node to remove
     */
    public static void removePermission(Player player, String node) {
        permission.playerRemove(player, node);
    }

    /**
     * Checks whether or not the player has the permission
     *
     * @param player player to check for
     * @param node   permission node to remove
     * @return true if the player has it, false otherwise
     */
    public static boolean hasPermission(Player player, String node) {
        return permission.has(player, node);
    }

    // Economy

    /**
     * Checks whether or not the Vault reference is valid with an economy plugin
     *
     * @return true if valid, false otherwise
     */
    public static boolean isEconomyValid() {
        if (!checked) {
            initialize();
            checked = true;
        }
        return economy != null;
    }

    public static double getBalance(Player player) {
        return economy.getBalance(player, player.getWorld().getName());
    }

    public static Boolean hasBalance(Player player, double balance) {
        return !(getBalance(player) < balance);
    }

    public static EconomyResponse withdraw(Player player, double amount) {
        return economy.withdrawPlayer(player, player.getWorld().getName(), amount);
    }

    public static EconomyResponse deposit(Player player, double amount) {
        return economy.depositPlayer(player, player.getWorld().getName(), amount);
    }
}
