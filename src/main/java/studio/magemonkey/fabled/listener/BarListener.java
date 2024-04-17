/**
 * Fabled
 * studio.magemonkey.fabled.listener.BarListener
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
package studio.magemonkey.fabled.listener;

import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.event.*;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.api.player.PlayerSkillBar;
import studio.magemonkey.fabled.api.skills.Skill;
import studio.magemonkey.fabled.gui.handlers.SkillHandler;
import studio.magemonkey.fabled.gui.map.SkillDetailMenu;
import studio.magemonkey.fabled.gui.map.SkillListMenu;
import studio.magemonkey.fabled.hook.CitizensHook;
import studio.magemonkey.codex.mccore.gui.MapData;
import studio.magemonkey.codex.mccore.gui.MapMenu;
import studio.magemonkey.codex.mccore.gui.MapMenuManager;
import studio.magemonkey.codex.mccore.util.VersionManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

import java.util.HashSet;
import java.util.UUID;

/**
 * Handles interactions with skill bars. This shouldn't be
 * use by other plugins as it is handled by the API.
 */
public class BarListener extends FabledListener {
    private final HashSet<UUID> ignored = new HashSet<UUID>();

    @Override
    public void init() {
        MainListener.registerJoin(this::onJoin);
        MainListener.registerClear(this::handleClear);
        for (Player player : VersionManager.getOnlinePlayers()) {
            if (Fabled.getSettings().isWorldEnabled(player.getWorld())) {
                PlayerData data = Fabled.getPlayerData(player);
                if (data.hasClass())
                    data.getSkillBar().setup(player);
            }
        }
    }

    @Override
    public void cleanup() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (Fabled.getSettings().isWorldEnabled(player.getWorld())) {
                cleanup(player);
            }
        }
    }

    private void cleanup(Player player) {
        PlayerData data = Fabled.getPlayerData(player);
        if (data.getSkillBar().isSetup()) data.getSkillBar().clear(player);
        ignored.remove(player.getUniqueId());
    }

    /**
     * Sets up skill bars on joining
     */
    public void onJoin(final Player player) {
        if (Fabled.getSettings().isWorldEnabled(player.getWorld())) {
            final PlayerData data = Fabled.getPlayerData(player);
            if (data.hasClass()) data.getSkillBar().setup(player);
        }
    }

    /**
     * Clears skill bars upon quitting the game
     *
     * @param event event details
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        cleanup(event.getPlayer());
    }

    /**
     * Manages setting up and clearing the skill bar when a player changes professions
     *
     * @param event event details
     */
    @EventHandler
    public void onProfess(PlayerClassChangeEvent event) {
        Player p = event.getPlayerData().getPlayer();
        if (!Fabled.getSettings().isWorldEnabled(p.getWorld()))
            return;

        // Professing as a first class sets up the skill bar
        if (event.getPreviousClass() == null && event.getNewClass() != null) {
            PlayerSkillBar bar = event.getPlayerData().getSkillBar();
            if (!bar.isSetup()) bar.setup(p);
        }

        // Resetting your class clears the skill bar
        else if (event.getPreviousClass() != null && event.getNewClass() == null) {
            PlayerSkillBar bar = event.getPlayerData().getSkillBar();
            bar.reset();
            bar.clear(p);
        }
    }

    /**
     * Adds unlocked skills to the skill bar if applicable
     *
     * @param event event details
     */
    @EventHandler
    public void onUnlock(PlayerSkillUnlockEvent event) {
        if (!event.getUnlockedSkill().getData().canCast() || event.getPlayerData().getPlayer() == null) return;
        event.getPlayerData().getSkillBar().unlock(event.getUnlockedSkill());
    }

    /**
     * Updates the skill bar when a skill is upgraded
     *
     * @param event event details
     */
    @EventHandler
    public void onUpgrade(final PlayerSkillUpgradeEvent event) {
        final Player player = event.getPlayerData().getPlayer();
        if (player != null && event.getPlayerData().getSkillBar().isSetup())
            Fabled.schedule(() -> event.getPlayerData().getSkillBar().update(player), 0);
    }

    /**
     * Updates a player's skill bar when downgrading a skill to level 0
     *
     * @param event event details
     */
    @EventHandler
    public void onDowngrade(final PlayerSkillDowngradeEvent event) {
        if (event.getPlayerData().getSkillBar().isSetup()) {
            Fabled.schedule(() -> Fabled.getPlayerData(event.getPlayerData().getPlayer())
                    .getSkillBar()
                    .update(event.getPlayerData().getPlayer()), 1);
        }
    }

    /**
     * Clears the skill bar on death
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent event) {
        if (CitizensHook.isNPC(event.getEntity())
                || !Fabled.getSettings().isWorldEnabled(event.getEntity().getWorld())) return;

        PlayerData data = Fabled.getPlayerData(event.getEntity());
        if (data.getSkillBar().isSetup()) {
            for (int i = 0; i < 9; i++) {
                if (!data.getSkillBar().isWeaponSlot(i))
                    event.getDrops().remove(event.getEntity().getInventory().getItem(i));
            }
            data.getSkillBar().clear(event.getEntity());
        }
    }

    /**
     * Sets the skill bar back up on respawn
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawn(PlayerRespawnEvent event) {
        if (!Fabled.getSettings().isWorldEnabled(event.getPlayer().getWorld())) return;

        PlayerData data = Fabled.getPlayerData(event.getPlayer());
        if (data.hasClass()) {
            data.getSkillBar().setup(event.getPlayer());
            data.getSkillBar().update(event.getPlayer());
            if (data.getSkillBar().isSetup()
                    && Fabled.getSettings().isWorldEnabled(event.getRespawnLocation().getWorld())
                    && !data.getSkillBar().isWeaponSlot(0))
                ignored.add(event.getPlayer().getUniqueId());
        }
    }

    /**
     * Handles assigning skills to the skill bar
     *
     * @param event event details
     */
    @EventHandler
    public void onAssign(final InventoryClickEvent event) {
        if (event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD && event.getInventory()
                .getHolder() instanceof SkillHandler) {
            final PlayerData data = Fabled.getPlayerData((Player) event.getWhoClicked());
            if (data.getSkillBar().isSetup() && !data.getSkillBar().isWeaponSlot(event.getHotbarButton())) {
                final SkillHandler handler = (SkillHandler) event.getInventory().getHolder();
                final Skill        skill   = handler.get(event.getSlot());
                if (skill != null && skill.canCast()) {
                    data.getSkillBar().assign(data.getSkill(skill.getName()), event.getHotbarButton());
                }
            }
        }
    }

    /**
     * Event for assigning skills to the skill bar
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onToggle(InventoryClickEvent event) {
        // Must click on an active skill bar
        PlayerData           data     = Fabled.getPlayerData((Player) event.getWhoClicked());
        final PlayerSkillBar skillBar = data.getSkillBar();
        if (!skillBar.isSetup())
            return;

        if ((event.getAction() == InventoryAction.HOTBAR_SWAP
                || event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD)
                && (!skillBar.isWeaponSlot(event.getHotbarButton()) || !skillBar.isWeaponSlot(event.getSlot()))) {
            event.setCancelled(true);
            return;
        }

        // Prevent moving skill icons
        int slot = event.getSlot();
        if (event.getSlot() < 9 && event.getRawSlot() > event.getView().getTopInventory().getSize()) {
            if (event.getClick() == ClickType.LEFT || event.getClick() == ClickType.SHIFT_LEFT)
                event.setCancelled(!skillBar.isWeaponSlot(slot));
            else if ((event.getClick() == ClickType.RIGHT || event.getClick() == ClickType.SHIFT_RIGHT)
                    && (!skillBar.isWeaponSlot(slot) || (skillBar.isWeaponSlot(slot) && (event.getCurrentItem() == null
                    || event.getCurrentItem().getType() == Material.AIR)))) {
                event.setCancelled(true);
                skillBar.toggleSlot(slot);
            } else if (event.getAction().name().startsWith("DROP"))
                event.setCancelled(!skillBar.isWeaponSlot(slot));
        }
    }

    /**
     * Ignores the next cast upon changing worlds due to the forced slot
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onChangeWorldPre(PlayerChangedWorldEvent event) {
        PlayerData data    = Fabled.getPlayerData(event.getPlayer());
        boolean    enabled = Fabled.getSettings().isWorldEnabled(event.getPlayer().getWorld());
        if (data.hasClass() && data.getSkillBar().isSetup() && enabled)
            ignored.add(event.getPlayer().getUniqueId());
        if (!enabled) data.getSkillBar().clear(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChangeWorld(PlayerChangedWorldEvent event) {
        PlayerData data    = Fabled.getPlayerData(event.getPlayer());
        boolean    enabled = Fabled.getSettings().isWorldEnabled(event.getPlayer().getWorld());
        if (enabled) data.getSkillBar().setup(event.getPlayer());
    }

    /**
     * Applies skill bar actions when pressing the number keys
     *
     * @param event event details
     */
    @EventHandler
    public void onCast(PlayerItemHeldEvent event) {
        // Doesn't do anything without a class
        PlayerData data = Fabled.getPlayerData(event.getPlayer());
        if (!data.hasClass()) return;

        // Must be a skill slot when the bar is set up
        PlayerSkillBar bar = data.getSkillBar();
        if (!bar.isWeaponSlot(event.getNewSlot()) && bar.isSetup()) {
            event.setCancelled(true);
            if (ignored.remove(event.getPlayer().getUniqueId())) return;

            MapData held = MapMenuManager.getActiveMenuData(event.getPlayer());
            if (held != null) {
                MapMenu menu = held.getMenu(event.getPlayer());
                if (menu instanceof SkillListMenu || menu instanceof SkillDetailMenu)
                    bar.assign(SkillListMenu.getSkill(event.getPlayer()), event.getNewSlot());
            } else
                bar.apply(event.getNewSlot());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChangeAccount(PlayerAccountChangeEvent event) {
        PlayerSkillBar bar = event.getPreviousAccount().getSkillBar();
        if (bar.isSetup()) bar.clear(event.getPreviousAccount().getPlayer());
    }

    /**
     * Clears or sets up the skill bar upon changing from or to creative mode
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChangeMode(PlayerGameModeChangeEvent event) {
        // Clear on entering creative mode
        final PlayerData data = Fabled.getPlayerData(event.getPlayer());
        if (event.getNewGameMode() == GameMode.CREATIVE && data.hasClass())
            data.getSkillBar().clear(event.getPlayer());

            // Setup on leaving creative mode
        else if (event.getPlayer().getGameMode() == GameMode.CREATIVE && data.hasClass()) {
            final Player player = event.getPlayer();
            Fabled.schedule(() -> Fabled.getPlayerData(player).getSkillBar().setup(player), 0);
        }
    }

    private void handleClear(final Player player) {
        final PlayerSkillBar skillBar = Fabled.getPlayerData(player).getSkillBar();
        if (skillBar.isSetup()) skillBar.update(player);
    }
}
