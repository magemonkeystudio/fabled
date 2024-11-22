/**
 * Fabled
 * studio.magemonkey.fabled.listener.CastListener
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 MageMonkeyStudio
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
package studio.magemonkey.fabled.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import studio.magemonkey.codex.util.InventoryUtil;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.event.PlayerClassChangeEvent;
import studio.magemonkey.fabled.api.event.PlayerSkillUnlockEvent;
import studio.magemonkey.fabled.cast.PlayerCastBars;
import studio.magemonkey.fabled.gui.customization.tool.GUITool;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Listener for the main casting system
 */
public class CastBarsListener extends FabledListener {
    private final Set<UUID> playersDropping = new HashSet<>();

    private static void cleanup(Player player) {
        if (Fabled.getSettings().isWorldEnabled(player.getWorld())) {
            forceCleanup(player);
        }
    }

    private static void forceCleanup(Player player) {
        Fabled.getData(player).getCastBars().restore();
        GUITool.removeCastItems(player);
    }

    @Override
    public void init() {
        MainListener.registerJoin(this::init);
        MainListener.registerClear(this::handleClear);
        Bukkit.getOnlinePlayers().forEach(this::init);
    }

    /**
     * Cleans up
     */
    @Override
    public void cleanup() {
        Bukkit.getOnlinePlayers().forEach(CastBarsListener::cleanup);
    }

    @EventHandler
    public void onDamaged(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Fabled.getData(player).getCastBars().restore();
        }
    }

    @EventHandler
    public void onClassChange(PlayerClassChangeEvent event) {
        event.getPlayerData().getCastBars().reset();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChangedWorldPre(PlayerChangedWorldEvent event) {
        boolean from = Fabled.getSettings().isWorldEnabled(event.getFrom());
        boolean to   = Fabled.getSettings().isWorldEnabled(event.getPlayer().getWorld());
        if (from && !to)
            forceCleanup(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChangeWorld(PlayerChangedWorldEvent event) {
        boolean from = Fabled.getSettings().isWorldEnabled(event.getFrom());
        boolean to   = Fabled.getSettings().isWorldEnabled(event.getPlayer().getWorld());

        if (to && !from) init(event.getPlayer());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (Fabled.getSettings().isWorldEnabled(event.getEntity().getWorld())) {
            event.getDrops().remove(event.getEntity().getInventory().getItem(Fabled.getSettings().getCastSlot()));
        }
    }

    private void init(Player player) {
        GUITool.removeCastItems(player);
        if (!Fabled.getSettings().isWorldEnabled(player.getWorld())) return;

        PlayerInventory inv  = player.getInventory();
        int             slot = Fabled.getSettings().getCastSlot();
        ItemStack       item = inv.getItem(slot);
        inv.setItem(slot, Fabled.getSettings().getCastItem());
        if (item != null && item.getType() != Material.AIR)
            inv.addItem(item);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        cleanup(event.getPlayer());
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        Fabled.getData((Player) event.getPlayer()).getCastBars().handleOpen();
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Fabled.getData((Player) event.getPlayer()).getCastBars().restore();
        init((Player) event.getPlayer());
    }

    /**
     * Adds unlocked skills to the skill bar if applicable
     *
     * @param event event details
     */
    @EventHandler
    public void onUnlock(PlayerSkillUnlockEvent event) {
        if (event.getUnlockedSkill().getData().canCast() && event.getPlayerData().getPlayer() != null)
            event.getPlayerData().getCastBars().unlock(event.getUnlockedSkill());
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (Fabled.getSettings().isWorldEnabled(event.getWhoClicked().getWorld())) {
            int slot = Fabled.getSettings().getCastSlot();
            if ((InventoryUtil.getTopInventory(event).getHolder() instanceof PlayerCastBars // Organizer menu
                    && event.getClickedInventory() instanceof PlayerInventory
                    && event.getSlot() == slot + 27)
                    || (event.getSlot() == slot
                    && event.getSlotType() == InventoryType.SlotType.QUICKBAR)
                    || ((event.getAction() == InventoryAction.HOTBAR_SWAP
                    || event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD)
                    && event.getHotbarButton() == slot)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (!Fabled.getSettings().isWorldEnabled(player.getWorld()))
            return;

        // Cancelling te event would re-add the skill indicator to the inventory after event handling
        // That's why Entity#remove() is used instead
        if (Fabled.getData(player).getCastBars().handleInteract(player)) {
            event.getItemDrop().remove();
            dropped(player.getUniqueId());
        } else if (player.getInventory().getHeldItemSlot() == Fabled.getSettings().getCastSlot()) {
            event.getItemDrop().remove();
            dropped(player.getUniqueId());
            Fabled.getData(player).getCastBars().showOrganizer(player);
        }
    }

    private void dropped(UUID uuid) {
        this.playersDropping.add(uuid);
        new BukkitRunnable() {
            @Override
            public void run() {
                playersDropping.remove(uuid);
            }
        }.runTask(Fabled.inst());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!Fabled.getSettings().isWorldEnabled(event.getPlayer().getWorld()))
            return;

        PlayerCastBars bars = Fabled.getData(event.getPlayer()).getCastBars();

        // Interaction while in a view
        if (bars.isHovering()) event.setCancelled(true);
        if (event.getPlayer().getInventory().getHeldItemSlot() == Fabled.getSettings().getCastSlot()) {
            event.setCancelled(true);

            if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (this.playersDropping.remove(event.getPlayer().getUniqueId())) {
                    return;
                }
                Fabled.getData(event.getPlayer()).getCastBars().restore();
                bars.showHoverBar(event.getPlayer());
            } else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                bars.showInstantBar(event.getPlayer());
        }
    }

    @EventHandler
    public void onHeld(PlayerItemHeldEvent event) {
        Fabled.getData(event.getPlayer()).getCastBars().handle(event);
    }

    private void handleClear(final Player player) {
        player.getInventory().setItem(Fabled.getSettings().getCastSlot(), Fabled.getSettings().getCastItem());
        Fabled.getData(player).setOnPreviewStop(null);
    }
}
