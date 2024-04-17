/**
 * Fabled
 * studio.magemonkey.fabled.listener.ItemListener
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

import com.google.common.collect.ImmutableSet;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.DefaultCombatProtection;
import studio.magemonkey.fabled.api.event.PlayerClassChangeEvent;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.data.PlayerEquips;
import studio.magemonkey.fabled.language.ErrorNodes;
import studio.magemonkey.codex.api.armor.ArmorEquipEvent;
import studio.magemonkey.codex.mccore.config.FilterType;
import studio.magemonkey.codex.mccore.util.VersionManager;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

/**
 * Listener that handles weapon item lore requirements
 */
public class ItemListener extends FabledListener {

    public static final Set<Material> ARMOR_TYPES = getArmorMaterials();

    private static Set<Material> getArmorMaterials() {
        final Set<String> armorSuffixes =
                ImmutableSet.of("BOOTS", "LEGGINGS", "CHESTPLATE", "HELMET");
        final ImmutableSet.Builder<Material> builder = ImmutableSet.builder();
        for (Material material : Material.values()) {
            final int    index  = material.name().lastIndexOf('_') + 1;
            final String suffix = material.name().substring(index);
            if (armorSuffixes.contains(suffix)) {
                builder.add(material);
            }
        }
        return builder.build();
    }

    @Override
    public void init() {
        MainListener.registerJoin(this::onJoin);
    }

    /**
     * Removes weapon bonuses when dropped
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        if (Fabled.getSettings().isWorldEnabled(event.getPlayer().getWorld())) {
            Player     player     = event.getPlayer();
            PlayerData playerData = Fabled.getPlayerData(player);
            playerData.getEquips().update(player);
            playerData.updatePlayerStat(player);
        }
    }

    /**
     * Updates player equips when an item breaks
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(PlayerItemBreakEvent event) {
        Player player = event.getPlayer();
        if (Fabled.getSettings().isWorldEnabled(player.getWorld())) {
            PlayerData playerData = Fabled.getPlayerData(player);
            if (playerData.getEquips().updateHandHeldItem(player, null)) {
                playerData.updatePlayerStat(player);
            }
        }
    }

    /**
     * Updates equipment data on join
     */
    public void onJoin(final Player player) {
        if (Fabled.getSettings().isWorldEnabled(player.getWorld()))
            Fabled.getPlayerData(player).getEquips().update(player);
    }

    @EventHandler
    public void onProfess(PlayerClassChangeEvent event) {
        final Player player = event.getPlayerData().getPlayer();
        if (Fabled.getSettings().isWorldEnabled(player.getWorld()))
            event.getPlayerData().getEquips().update(player);
    }

    /**
     * Updates weapon on pickup
     * Clear attribute buff data on quit
     *
     * @param event event details
     */
    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        Entity ent = event.getEntity();
        if (!(ent instanceof Player))
            return;

        Player player = (Player) ent;
        if (Fabled.getSettings().isWorldEnabled(player.getWorld())) {

            Fabled.schedule(new BukkitRunnable() {
                @Override
                public void run() {
                    PlayerData playerData = Fabled.getPlayerData(player);
                    if (playerData.getEquips().update(player)) {
                        playerData.updatePlayerStat(player);
                    }
                }
            }, 1);

        }
    }

    /**
     * Update equips on world change into an active world
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorld(PlayerChangedWorldEvent event) {
        if (!Fabled.getSettings().isWorldEnabled(event.getFrom())
                && Fabled.getSettings().isWorldEnabled(event.getPlayer().getWorld()))
            Fabled.getPlayerData(event.getPlayer()).getEquips().update(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        if (Fabled.getSettings().isWorldEnabled(player.getWorld())) {
            PlayerData playerData = Fabled.getPlayerData(player);
            if (playerData.getEquips().updateHandHeldItem(player, player.getInventory().getItem(event.getNewSlot()))) {
                playerData.updatePlayerStat(player);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (Fabled.getSettings().isWorldEnabled(player.getWorld())) {
            PlayerData playerData = Fabled.getPlayerData(player);
            if (playerData.getEquips().update(player)) {
                playerData.updatePlayerStat(player);
            }
        }
    }

    private int getArmorSlot(Material mat) {
        if (mat.name().contains("CHESTPLATE")) return 38;
        else if (mat.name().contains("LEGGINGS")) return 37;
        else if (mat.name().contains("BOOTS")) return 36;
        else return 39;
    }

//    @EventHandler
//    public void onArmorEquip(PlayerInteractEvent event) {
//        Player    player = event.getPlayer();
//        ItemStack item   = event.getItem();
//
//        if (Fabled.getSettings().isWorldEnabled(player.getWorld())
//                && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
//                && item != null
//                && ARMOR_TYPES.contains(item.getType())) {
//            int equippedSlot = getArmorSlot(item.getType());
//
//            PlayerData playerData = Fabled.getPlayerData(player);
//            playerData.getEquips().updateEquip(player, equippedSlot, item);
//            playerData.updatePlayerStat(player);
//        }
//    }

    @EventHandler
    public void armorEquip(ArmorEquipEvent event) {
        Player    player = event.getPlayer();
        ItemStack item   = event.getNewArmorPiece();

        if (Fabled.getSettings().isWorldEnabled(player.getWorld())
                && item != null && ARMOR_TYPES.contains(item.getType())) {
            int slot = getArmorSlot(item.getType());

            PlayerData playerData = Fabled.getPlayerData(player);

            PlayerEquips.EquipData data =
                    playerData.getEquips().getEquipData(item, PlayerEquips.EquipType.fromSlot(slot));
            if (!data.hasMetConditions()) {
                event.setCancelled(true);
                Fabled.getLanguage().sendMessage(ErrorNodes.CANNOT_USE, player, FilterType.COLOR);
                return;
            }

            playerData.getEquips().updateEquip(player, slot, item);
            playerData.updatePlayerStat(player);
        }
    }

    /**
     * Cancels left clicks on disabled items
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onAttack(EntityDamageByEntityEvent event) {
        if (!Fabled.getSettings().isWorldEnabled(event.getEntity().getWorld())
                || DefaultCombatProtection.isFakeDamageEvent(event)) {
            return;
        }

        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if (!Fabled.getPlayerData(player).getEquips().canHit()) {
                Fabled.getLanguage().sendMessage(ErrorNodes.CANNOT_USE, player, FilterType.COLOR);
                event.setCancelled(true);
            }
        }
        if (event.getEntity() instanceof Player && VersionManager.isVersionAtLeast(VersionManager.V1_9_0)) {
            Player        player   = (Player) event.getEntity();
            final boolean blocking = event.getDamage(EntityDamageEvent.DamageModifier.BLOCKING) < 0;
            if (blocking && !Fabled.getPlayerData(player).getEquips().canBlock()) {
                Fabled.getLanguage().sendMessage(ErrorNodes.CANNOT_USE, event.getEntity(), FilterType.COLOR);
                event.setDamage(EntityDamageEvent.DamageModifier.BLOCKING, 0);
            }
        }
    }

    /**
     * Cancels firing a bow with a disabled weapon
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onShoot(EntityShootBowEvent event) {
        if (!Fabled.getSettings().isWorldEnabled(event.getEntity().getWorld())) {
            return;
        }

        if (event.getEntity() instanceof Player) {
            final PlayerEquips equips = Fabled.getPlayerData((Player) event.getEntity()).getEquips();
            if (isMainhand(event.getBow(), event.getEntity())) {
                if (!equips.canHit()) {
                    Fabled.getLanguage().sendMessage(ErrorNodes.CANNOT_USE, event.getEntity(), FilterType.COLOR);
                    event.setCancelled(true);
                }
            } else if (!equips.canBlock()) {
                Fabled.getLanguage().sendMessage(ErrorNodes.CANNOT_USE, event.getEntity(), FilterType.COLOR);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!Fabled.getSettings().isWorldEnabled(event.getEntity().getWorld())) {
            return;
        }
        ProjectileSource shooter = event.getEntity().getShooter();
        if (shooter instanceof Player) {
            Player             player = (Player) shooter;
            final PlayerEquips equips = Fabled.getPlayerData(player).getEquips();
            if (!equips.canHit()) {
                Fabled.getLanguage().sendMessage(ErrorNodes.CANNOT_USE, player, FilterType.COLOR);
                event.setCancelled(true);
            }
        }
    }

    private boolean isMainhand(final ItemStack bow, final LivingEntity entity) {
        ItemStack item = entity.getEquipment().getItemInMainHand();
        return bow == item || bow.equals(item);
    }

}
