/**
 * SkillAPI
 * com.sucy.skill.listener.CastItemListener
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2016 Steven Sucy
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
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
package com.sucy.skill.listener;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.event.PlayerClassChangeEvent;
import com.sucy.skill.api.event.PlayerSkillUnlockEvent;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkillSlot;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Handles the alternate casting option for casting via a cycling slot
 */
public class CastItemListener extends SkillAPIListener {
    private final HashMap<UUID, PlayerSkillSlot> data = new HashMap<>();
    private final Set<UUID> playersDropping = new HashSet<>();

    private void cleanup(Player player) {
        data.remove(player.getUniqueId());
        if (SkillAPI.getSettings().isWorldEnabled(player.getWorld()))
            player.getInventory().setItem(SkillAPI.getSettings().getCastSlot(), null);
    }

    @Override
    public void init() {
        MainListener.registerJoin(this::init);
        MainListener.registerClear(this::handleClear);
        for (Player player : Bukkit.getOnlinePlayers())
            init(player);
    }

    /**
     * Cleans up the listener functions
     */
    @Override
    public void cleanup() {
        for (Player player : Bukkit.getOnlinePlayers())
            cleanup(player);
    }

    /**
     * Re-initializes cast data on class change
     *
     * @param event event details
     */
    @EventHandler
    public void onClassChange(PlayerClassChangeEvent event) {
        data.get(event.getPlayerData().getPlayer().getUniqueId()).init(event.getPlayerData());
    }

    /**
     * Enables/disables cast when changing worlds
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onWorldChangePre(PlayerChangedWorldEvent event) {
        boolean from = SkillAPI.getSettings().isWorldEnabled(event.getFrom());
        boolean to   = SkillAPI.getSettings().isWorldEnabled(event.getPlayer().getWorld());
        if (from && !to)
            event.getPlayer().getInventory().setItem(SkillAPI.getSettings().getCastSlot(), null);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldChange(PlayerChangedWorldEvent event) {
        boolean from = SkillAPI.getSettings().isWorldEnabled(event.getFrom());
        boolean to   = SkillAPI.getSettings().isWorldEnabled(event.getPlayer().getWorld());

        if (to && !from) init(event.getPlayer());
    }

    private PlayerSkillSlot get(Player player) {
        return data.get(player.getUniqueId());
    }

    private PlayerSkillSlot get(PlayerData data) {
        return this.data.get(data.getPlayer().getUniqueId());
    }

    /**
     * Gives the player the cast item
     *
     * @param player player to give to
     */
    private void init(Player player) {
        if (!SkillAPI.getSettings().isWorldEnabled(player.getWorld())) return;

        PlayerSkillSlot slotData = new PlayerSkillSlot();
        data.put(player.getUniqueId(), slotData);
        slotData.init(SkillAPI.getPlayerData(player));

        PlayerInventory inv  = player.getInventory();
        int             slot = SkillAPI.getSettings().getCastSlot();
        ItemStack       item = inv.getItem(slot);
        slotData.updateItem(player);
        if (item != null && item.getType() != Material.AIR)
            inv.addItem(item);
    }

    /**
     * Removes the cast item on quit
     *
     * @param event event details
     */

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        cleanup(event.getPlayer());
    }

    /**
     * Adds unlocked skills to the skill bar if applicable
     *
     * @param event event details
     */
    @EventHandler
    public void onUnlock(PlayerSkillUnlockEvent event) {
        get(event.getPlayerData()).unlock(event.getUnlockedSkill());
    }

    /**
     * Prevents moving the cast item
     *
     * @param event event details
     */
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (SkillAPI.getSettings().isWorldEnabled(event.getWhoClicked().getWorld())) {
            if (event.getSlot() == SkillAPI.getSettings().getCastSlot() && event.getSlotType() == InventoryType.SlotType.QUICKBAR)
                event.setCancelled(true);
            else if (event.getAction() == InventoryAction.HOTBAR_SWAP
                    && event.getHotbarButton() == SkillAPI.getSettings().getCastSlot())
                event.setCancelled(true);
        }
    }

    /**
     * Casts a skill when dropping the cast item
     *
     * @param event event details
     */
    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (SkillAPI.getSettings().isWorldEnabled(event.getPlayer().getWorld())
                && event.getPlayer().getInventory().getHeldItemSlot() == SkillAPI.getSettings().getCastSlot()) {
            event.setCancelled(true);
            get(event.getPlayer()).activate();
            this.playersDropping.add(event.getPlayer().getUniqueId());
            new BukkitRunnable() {
                @Override
                public void run() {
                    playersDropping.remove(event.getPlayer().getUniqueId());
                }
            }.runTask(SkillAPI.inst());
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (SkillAPI.getSettings().isWorldEnabled(event.getEntity().getWorld())) {
            event.getDrops().remove(event.getEntity().getInventory().getItem(SkillAPI.getSettings().getCastSlot()));
        }
    }

    /**
     * Cycles through skills upon interact
     *
     * @param event event details
     */
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        // Cycling skills
        if (SkillAPI.getSettings().isWorldEnabled(event.getPlayer().getWorld())
                && event.getPlayer().getInventory().getHeldItemSlot() == SkillAPI.getSettings().getCastSlot()) {
            event.setCancelled(true);
            if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (this.playersDropping.remove(event.getPlayer().getUniqueId())) {
                    return;
                }
                get(event.getPlayer()).prev();
            } else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                get(event.getPlayer()).next();
        }
    }

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        data.get(event.getPlayer().getUniqueId()).setHovering(event.getNewSlot() == SkillAPI.getSettings().getCastSlot());
    }

    private void handleClear(final Player player) {
        player.getInventory().setItem(SkillAPI.getSettings().getCastSlot(), SkillAPI.getSettings().getCastItem());
    }
}
