/**
 * Fabled
 * com.promcteam.fabled.task.RemoveTask
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 ProMCTeam
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
package com.promcteam.fabled.task;

import com.promcteam.fabled.Fabled;
import com.promcteam.fabled.api.skills.PassiveSkill;
import com.promcteam.fabled.api.skills.Skill;
import com.promcteam.fabled.api.util.BuffManager;
import com.promcteam.fabled.api.util.FlagManager;
import com.promcteam.fabled.dynamic.DynamicSkill;
import com.promcteam.fabled.dynamic.mechanic.WolfMechanic;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * A simple task for removing an entity after a duration
 */
public class RemoveTask extends BukkitRunnable {
    private List<? extends Entity> entities;

    /**
     * Initializes a new task to remove the entity after the
     * given number of ticks.
     *
     * @param entities entities to remove
     * @param ticks    ticks to wait before removing the entity
     */
    public RemoveTask(List<? extends Entity> entities, int ticks) {
        this.entities = entities;
        Fabled.schedule(this, ticks);
    }

    /**
     * Removes the entity once the time is up
     */
    @Override
    @SuppressWarnings("unchecked")
    public void run() {
        // Clear skill setup
        for (Entity entity : entities) {
            if (entity.hasMetadata(WolfMechanic.SKILL_META)) {
                final List<String> skills = (List<String>) Fabled.getMeta(entity, WolfMechanic.SKILL_META);
                final int          level  = Fabled.getMetaInt(entity, WolfMechanic.LEVEL);
                for (final String skillName : skills) {
                    final Skill skill = Fabled.getSkill(skillName);
                    if (skill instanceof PassiveSkill) {
                        ((PassiveSkill) skill).stopEffects((LivingEntity) entity, level);
                    }
                }

                DynamicSkill.clearCastData((LivingEntity) entity);
                FlagManager.clearFlags((LivingEntity) entity);
                BuffManager.clearData((LivingEntity) entity);
            }

            // Remove entity
            if (entity.isValid()) {
                entity.remove();
            }
        }
    }
}
