/**
 * SkillAPI
 * com.sucy.skill.listener.ClickListener
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
package com.sucy.skill.listener;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.DefaultCombatProtection;
import com.sucy.skill.api.event.KeyPressEvent;
import com.sucy.skill.api.skills.Skill;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

/**
 * Handles transferring click actions by the player to
 * combos that cast skills.
 */
public class ClickListener extends SkillAPIListener {

    /**
     * Registers clicks as they happen
     *
     * @param event event details
     */
    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        // Left clicks
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Bukkit.getServer()
                    .getPluginManager()
                    .callEvent(new KeyPressEvent(event.getPlayer(), KeyPressEvent.Key.LEFT));
        }

        // Right clicks
        else if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            Bukkit.getServer()
                    .getPluginManager()
                    .callEvent(new KeyPressEvent(event.getPlayer(), KeyPressEvent.Key.RIGHT));
        }
    }

    @EventHandler
    public void onEntityClick(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        if (SkillAPI.getSettings().isInteractRightClick()) {
            KeyPressEvent keyEvent = new KeyPressEvent(event.getPlayer(), KeyPressEvent.Key.RIGHT);
            Bukkit.getServer()
                    .getPluginManager()
                    .callEvent(keyEvent);
            if (keyEvent.isCancelParent()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (Skill.isSkillDamage() || !(event.getDamager() instanceof Player)
                || DefaultCombatProtection.isFakeDamageEvent(event)) {
            return;
        }

        if (SkillAPI.getSettings().isDamageLeftClick()) {
            KeyPressEvent keyEvent = new KeyPressEvent((Player) event.getDamager(), KeyPressEvent.Key.LEFT);
            Bukkit.getServer()
                    .getPluginManager()
                    .callEvent(keyEvent);
            if (keyEvent.isCancelParent()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDrop(final PlayerDropItemEvent event) {
        Bukkit.getServer().getPluginManager().callEvent(new KeyPressEvent(event.getPlayer(), KeyPressEvent.Key.Q));
    }
}
