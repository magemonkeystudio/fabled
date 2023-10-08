/**
 * SkillAPI
 * com.sucy.skill.listener.attribute.AttributeListener
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
package com.sucy.skill.listener.attribute;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.enums.ExpSource;
import com.sucy.skill.api.enums.ManaSource;
import com.sucy.skill.api.event.*;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.hook.CitizensHook;
import com.sucy.skill.hook.PluginChecker;
import com.sucy.skill.hook.RPGItemsHook;
import com.sucy.skill.listener.MainListener;
import com.sucy.skill.listener.SkillAPIListener;
import com.sucy.skill.log.LogType;
import com.sucy.skill.log.Logger;
import com.sucy.skill.manager.AttributeManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Listener for managing applying attribute bonuses for players
 */
public class AttributeListener extends SkillAPIListener {
    public static final String PHYSICAL = "physical";

    @Override
    public void init() {
        MainListener.registerJoin(this::onJoin);
    }

    /**
     * Refresh player stats on login
     */
    public void onJoin(final Player player) {
        updatePlayer(SkillAPI.getPlayerData(player));
    }

    /**
     * Updates attributes on respawn
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onRespawn(PlayerRespawnEvent event) {
        if (event.getPlayer().hasMetadata("NPC"))
            return;

        updatePlayer(SkillAPI.getPlayerData(event.getPlayer()));
    }

    /**
     * Applies health and mana bonus attributes
     *
     * @param event event details
     */
    @EventHandler
    public void onLevelUp(PlayerLevelUpEvent event) {
        updatePlayer(event.getPlayerData());
    }

    /**
     * Applies health and mana attribute bonuses on upgrading the attribute
     *
     * @param event event details
     */
    @EventHandler
    public void onInvest(PlayerUpAttributeEvent event) {
        updatePlayer(event.getPlayerData());
    }

    /**
     * Apply attributes to mana regen
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onManaRegen(PlayerManaGainEvent event) {
        // Bonus to regen from attributes
        if (event.getSource() == ManaSource.REGEN) {
            double newAmount = event.getPlayerData().scaleStat(AttributeManager.MANA_REGEN, event.getAmount());
            Logger.log(LogType.MANA, 3, "Attributes scaled mana gain to " + newAmount);
            event.setAmount(newAmount);
        }
    }

    /**
     * Apply physical damage/defense attribute buffs
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPhysicalDamage(PhysicalDamageEvent event) {
        // Physical Damage
        if (event.getDamager() instanceof Player) {
            Player    player = (Player) event.getDamager();
            ItemStack item   = player.getInventory().getItemInMainHand();
            // If it's an RPGItem, we'll handle this through the AttributeRegistry
            if (RPGItemsHook.isRPGItem(item)) return;
            if (CitizensHook.isNPC(player)) return;

            PlayerData data = SkillAPI.getPlayerData(player);

            double newAmount = data.scaleStat(AttributeManager.PHYSICAL_DAMAGE, event.getDamage());
            if (event.isProjectile()) {
                newAmount = data.scaleStat(AttributeManager.PROJECTILE_DAMAGE, newAmount);
            } else {
                newAmount = data.scaleStat(AttributeManager.MELEE_DAMAGE, newAmount);
            }
            event.setDamage(newAmount);
        }

        // Physical Defense
        if (event.getTarget() instanceof Player) {
            Player player = (Player) event.getTarget();
            if (CitizensHook.isNPC(player))
                return;

            PlayerData data = SkillAPI.getPlayerData(player);

            double newAmount = data.scaleStat(AttributeManager.PHYSICAL_DEFENSE, event.getDamage());
            if (event.isProjectile()) {
                newAmount = data.scaleStat(AttributeManager.PROJECTILE_DEFENSE, newAmount);
            } else {
                newAmount = data.scaleStat(AttributeManager.MELEE_DEFENSE, newAmount);
            }
            event.setDamage(newAmount);
        }
    }

    /**
     * Apply skill damage/defense attribute buffs
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onSkillDamage(final SkillDamageEvent event) {
        // Skill Damage
        if (event.getDamager() instanceof Player) {
            final Player player = (Player) event.getDamager();
            if (CitizensHook.isNPC(player))
                return;

            final PlayerData data = SkillAPI.getPlayerData(player);

            if (event.getClassification().equalsIgnoreCase(PHYSICAL)) {
                event.setDamage(data.scaleStat(AttributeManager.PHYSICAL_DAMAGE, event.getDamage()));
            } else {
                final String classified = AttributeManager.SKILL_DAMAGE + "-" + event.getClassification();
                final double firstPass  = data.scaleStat(classified, event.getDamage());
                final double newAmount  = data.scaleStat(AttributeManager.SKILL_DAMAGE, firstPass);
                event.setDamage(newAmount);
            }
        }

        // Skill Defense
        if (event.getTarget() instanceof Player) {
            final Player player = (Player) event.getTarget();
            if (CitizensHook.isNPC(player))
                return;

            final PlayerData data = SkillAPI.getPlayerData(player);

            if (event.getClassification().equalsIgnoreCase(PHYSICAL)) {
                event.setDamage(data.scaleStat(AttributeManager.PHYSICAL_DEFENSE, event.getDamage()));
            } else {
                final String classified = AttributeManager.SKILL_DEFENSE + "-" + event.getClassification();
                final double firstPass  = data.scaleStat(classified, event.getDamage());
                final double newAmount  = data.scaleStat(AttributeManager.SKILL_DEFENSE, firstPass);
                event.setDamage(newAmount);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(final EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        final Player player = (Player) event.getEntity();
        if (CitizensHook.isNPC(player)) {
            return;
        }

        final PlayerData data = SkillAPI.getPlayerData(player);
        event.setDamage(data.scaleStat("defense-" + event.getCause().name().toLowerCase(), event.getDamage()));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onExp(final PlayerExperienceGainEvent event) {
        if (event.getSource() != ExpSource.COMMAND) {
            final double newExp = event.getPlayerData().scaleStat(AttributeManager.EXPERIENCE, event.getExp());
            event.setExp(newExp);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldChange(PlayerChangedWorldEvent event) {
        boolean oldEnabled = SkillAPI.getSettings().isWorldEnabled(event.getFrom());
        boolean newEnabled = SkillAPI.getSettings().isWorldEnabled(event.getPlayer().getWorld());
        if (oldEnabled && !newEnabled) {
            //TODO: Clear global Attribute/Stats bonus
        } else if (!oldEnabled && newEnabled) {
            updatePlayer(SkillAPI.getPlayerData(event.getPlayer()));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onHungerChange(final FoodLevelChangeEvent event) {
        final Player player = (Player) event.getEntity();
        if (event.getFoodLevel() < player.getFoodLevel()) {
            final PlayerData data = SkillAPI.getPlayerData(player);
            final int        lost = data.subtractHungerValue(player.getFoodLevel() - event.getFoodLevel());
            event.setFoodLevel(player.getFoodLevel() - lost);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onHungerHeal(final EntityRegainHealthEvent event) {
        if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED
                && event.getEntity() instanceof Player) {
            final Player     player = (Player) event.getEntity();
            final PlayerData data   = SkillAPI.getPlayerData(player);
            final double     scaled = data.scaleStat(AttributeManager.HUNGER_HEAL, event.getAmount());
            event.setAmount(scaled);
        }
    }

    /**
     * Updates the stats of a player based on their current attributes
     *
     * @param data player to update
     */
    private void updatePlayer(PlayerData data) {
        data.updatePlayerStat(data.getPlayer());
    }

}
