/**
 * SkillAPI
 * com.sucy.skill.hook.PluginChecker
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

import com.sucy.skill.hook.mimic.MimicHook;
import com.sucy.skill.listener.SkillAPIListener;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

/**
 * Handler for checking whether or not hooked plugins are present
 * and active before using related code.
 */
public class PluginChecker extends SkillAPIListener {
    private static boolean vault;
    private static boolean libsDisguises;
    private static boolean noCheatPlus;
    private static boolean papi;
    private static boolean bungee;
    private static boolean mythicMobs;
    private static boolean worldGuard;
    private static boolean parties;
    private static boolean mimic;

    /**
     * Checks if vault is active on the server
     *
     * @return true if active with permissions plugin, false otherwise
     */
    public static boolean isVaultActive() {return vault;}

    /**
     * Checks whether or not Lib's Disguises is active
     *
     * @return true if active
     */
    public static boolean isDisguiseActive() {return libsDisguises;}

    /**
     * Checks whether or not NoCheatPlus is active on the server
     *
     * @return true if active, false otherwise
     */
    public static boolean isNoCheatActive() {return noCheatPlus;}

    public static boolean isPlaceholderAPIActive() {return papi;}

    /**
     * Checks whether or not bungee is present
     *
     * @return true if present, false otherwise
     */
    public static boolean isBungeeActive() {return bungee;}

    public static boolean isMythicMobsActive() {return mythicMobs;}

    public static boolean isWorldGuardActive() {return worldGuard;}

    public static boolean isPartiesActive() {return parties || Bukkit.getPluginManager().isPluginEnabled("ProSkillAPIParties");}

    /** Checks whether Mimic is present. */
    public static boolean isMimicActive() {return mimic && MimicHook.isHooked();}

    @Override
    public void init() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        vault = pluginManager.isPluginEnabled("Vault") && VaultHook.isValid();
        libsDisguises = pluginManager.isPluginEnabled("LibsDisguises");
        noCheatPlus = pluginManager.isPluginEnabled("NoCheatPlus");
        papi = pluginManager.isPluginEnabled("PlaceholderAPI");
        try {
            Class.forName("net.md_5.bungee.Util");
            bungee = true;
        } catch (Exception ex) {bungee = false;}
        mythicMobs = pluginManager.isPluginEnabled("MythicMobs");
        worldGuard = pluginManager.isPluginEnabled("WorldGuard");
        parties = pluginManager.isPluginEnabled("ProSkillAPIParties");
        mimic = pluginManager.isPluginEnabled("Mimic");
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        onPluginToggled(event.getPlugin(), true);
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        onPluginToggled(event.getPlugin(), false);
    }

    private void onPluginToggled(Plugin plugin, Boolean isEnabled) {
        switch (plugin.getName()) {
            case "Vault":
                vault = isEnabled;
                break;
            case "LibsDisguises":
                libsDisguises = isEnabled;
                break;
            case "NoCheatPlus":
                noCheatPlus = isEnabled;
                break;
            case "PlaceholderAPI":
                papi = isEnabled;
                break;
            case "MythicMobs":
                mythicMobs = isEnabled;
                break;
            case "WorldGuard":
                worldGuard = isEnabled;
                break;
            case "ProSkillAPIParties":
                parties = isEnabled;
                break;
            case "Mimic":
                mimic = isEnabled;
                break;
        }
    }
}
