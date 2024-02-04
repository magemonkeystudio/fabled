/**
 * SkillAPI
 * com.sucy.skill.dynamic.mechanic.MountMechanic
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
package com.sucy.skill.dynamic.mechanic;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.List;

/**
 * Mounts the target entity
 */
public class MountMechanic extends MechanicComponent {
    private static final String TYPE = "type";
    private static final String MAX  = "max";

    @Override
    public String getKey() {
        return "mount";
    }

    /**
     * Executes the component
     *
     * @param caster  caster of the skill
     * @param level   level of the skill
     * @param targets targets to apply to
     * @param force
     * @return true if applied to something, false otherwise
     */
    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        if (targets.isEmpty()) {
            return false;
        }

        final String type = settings.getString(TYPE, "caster->target");
        final int    max  = (int) parseValues(caster, MAX, level, 1.0);

        if (type.equalsIgnoreCase("caster->target")) {

            for (LivingEntity target : targets) {
                int casterSize = getStackSize(caster);
                int targetSize = getStackSize(target);
                if (casterSize + targetSize + 1 > max) {
                    break;
                }
                Entity bottom    = getBottomOfStack(caster);
                Entity targetTop = getTopOfStack(target);
                targetTop.addPassenger(bottom);
            }
        } else if (type.equalsIgnoreCase("target->caster")) {
            for (LivingEntity target : targets) {
                int casterSize = getStackSize(caster);
                int targetSize = getStackSize(target);
                if (casterSize + targetSize + 1 > max) {
                    break;
                }
                Entity top = getTopOfStack(caster);
                top.addPassenger(target);
            }
        } else {
            return false;
        }

        return true;
    }

    private int getStackSize(Entity entity) {
        int size = 0;
        entity = getBottomOfStack(entity);
        while (!entity.getPassengers().isEmpty()) {
            size++;
            entity = entity.getPassengers().get(0);
        }
        return size;
    }

    private Entity getBottomOfStack(Entity entity) {
        Entity bottom = entity;
        while (bottom.getVehicle() != null) {
            bottom = bottom.getVehicle();
        }
        return bottom;
    }

    private Entity getTopOfStack(Entity entity) {
        Entity top = entity;
        while (!top.getPassengers().isEmpty()) {
            top = top.getPassengers().get(0);
        }
        return top;
    }
}
