/**
 * Fabled
 * com.promcteam.fabled.listener.DeathListener
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 ProMCTeam
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
package com.promcteam.fabled.listener;

import com.promcteam.fabled.Fabled;
import com.promcteam.fabled.api.event.SkillDamageEvent;
import com.promcteam.fabled.api.event.TrueDamageEvent;
import com.promcteam.fabled.api.particle.EffectManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;

public class DeathListener extends FabledListener {
    private final String KILLER = "sapiKiller";

    /**
     * Launches our own death event for when entities are killed via skills
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSpell(SkillDamageEvent event) {
        handle(event.getTarget(), event.getDamager(), event.getDamage());
    }

    /**
     * Launches our own death event for when entities are killed via skills
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTrue(TrueDamageEvent event) {
        handle(event.getTarget(), event.getDamager(), event.getDamage());
    }

    private void handle(final LivingEntity entity, final LivingEntity damager, final double damage) {
        Fabled.setMeta(entity, KILLER, damager);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(EntityDeathEvent event) {
        EffectManager.clear(event.getEntity());
        Object killer = Fabled.getMeta(event.getEntity(), KILLER);

        if (killer != null && event.getEntity().getKiller() == null) {
            applyDeath(event.getEntity(), (LivingEntity) killer, event.getDroppedExp());
        }
    }

    private void applyDeath(LivingEntity entity, LivingEntity damager, int exp) {
        if (!entity.isDead() || entity.getKiller() != null || !(damager instanceof Player))
            return;

        KillListener.giveExp(entity, (Player) damager, exp);
    }
}
