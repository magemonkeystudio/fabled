/**
 * SkillAPI
 * com.sucy.skill.hook.PluginChecker
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sucy.skill.hook;

import com.sucy.skill.listener.SkillAPIListener;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.PluginManager;

/**
 * Handler for checking whether or not hooked plugins are present
 * and active before using related code.
 */
public class PluginChecker extends SkillAPIListener {
    private static boolean vault;
    private static boolean libsDisguises;
    private static boolean noCheatPlus;
    private static boolean rpgInventory;
    private static boolean papi;
    private static boolean bungee;
    private static boolean mythicMobs;
    private static boolean worldGuard;
    private static boolean parties;

    @Override
    public void init() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        vault = pluginManager.isPluginEnabled("Vault") && VaultHook.isValid();
        libsDisguises = pluginManager.isPluginEnabled("LibsDisguises");
        noCheatPlus = pluginManager.isPluginEnabled("NoCheatPlus");
        rpgInventory = pluginManager.isPluginEnabled("RPGInventory");
        papi = pluginManager.isPluginEnabled("PlaceholderAPI");
        try {
            Class.forName("net.md_5.bungee.Util");
            bungee = true;
        } catch (Exception ex) { bungee = false; }
        mythicMobs = pluginManager.isPluginEnabled("MythicMobs");
        worldGuard = pluginManager.isPluginEnabled("WorldGuard");
        parties = pluginManager.isPluginEnabled("Parties");
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        switch (event.getPlugin().getName()) {
            case "Vault":
                vault = true;
                break;
            case "LibsDisguises":
                libsDisguises = true;
                break;
            case "NoCheatPlus":
                noCheatPlus = true;
                break;
            case "RPGInventory":
                rpgInventory = true;
                break;
            case "PlaceholderAPI":
                papi = true;
                break;
            case "MythicMobs":
                mythicMobs = true;
                break;
            case "WorldGuard":
                worldGuard = true;
                break;
            case "Parties":
                parties = true;
                break;
        }
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        switch (event.getPlugin().getName()) {
            case "Vault":
                vault = false;
                break;
            case "LibsDisguises":
                libsDisguises = false;
                break;
            case "NoCheatPlus":
                noCheatPlus = false;
                break;
            case "RPGInventory":
                rpgInventory = false;
                break;
            case "PlaceholderAPI":
                papi = true;
                break;
            case "MythicMobs":
                mythicMobs = false;
                break;
            case "WorldGuard":
                worldGuard = false;
                break;
            case "Parties":
                parties = false;
                break;
        }
    }

    /**
     * Checks if vault is active on the server
     *
     * @return true if active with permissions plugin, false otherwise
     */
    public static boolean isVaultActive() { return vault; }

    /**
     * Checks whether or not Lib's Disguises is active
     *
     * @return true if active
     */
    public static boolean isDisguiseActive() { return libsDisguises; }

    /**
     * Checks whether or not NoCheatPlus is active on the server
     *
     * @return true if active, false otherwise
     */
    public static boolean isNoCheatActive() { return noCheatPlus; }

    /**
     * Checks whether or not RPGInventory is active on the server
     *
     * @return true if active, false otherwise
     */
    public static boolean isRPGInventoryActive() { return rpgInventory; }

    public static boolean isPlaceholderAPIActive() { return papi; }

    /**
     * Checks whether or not bungee is present
     *
     * @return true if present, false otherwise
     */
    public static boolean isBungeeActive() { return bungee; }

    public static boolean isMythicMobsActive() { return mythicMobs; }

    public static boolean isWorldGuardActive() { return worldGuard; }

    public static boolean isPartiesActive() { return parties; }
}
