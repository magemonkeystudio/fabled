/**
 * SkillAPI
 * com.sucy.skill.dynamic.mechanic.ThrowMechanic
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
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Throws entities off the target's head
 */
public class ThrowMechanic extends MechanicComponent {
    private static final String SPEED    = "speed";
    private static final String RELATIVE = "relative";
    private              Vector up       = new Vector(0, 1, 0);

    @Override
    public String getKey() {
        return "throw";
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

        double speed    = parseValues(caster, SPEED, level, 0);
        String relative = settings.getString(RELATIVE, "target").toLowerCase();

        List<LivingEntity> thrown = new ArrayList<>();
        boolean            worked = false;
        for (LivingEntity target : targets) {
            Entity toThrow = getTopOfStack(target);
            if (toThrow.equals(target)) continue;

            final Vector dir;
            if (relative.equals("caster")) {
                dir = caster.getLocation().getDirection().normalize();
            } else if (relative.equals("thrown")) {
                dir = toThrow.getLocation().getDirection().normalize();
            } else {
                dir = target.getLocation().getDirection().normalize();
            }

            dir.multiply(speed);
            toThrow.leaveVehicle();
            toThrow.setVelocity(dir);

            if (toThrow instanceof LivingEntity) {
                thrown.add((LivingEntity) toThrow);
            }
            worked = true;
        }

        if (!thrown.isEmpty()) {
            executeChildren(caster, level, thrown, force);
        }
        return worked;
    }

    private Entity getTopOfStack(Entity entity) {
        Entity top = entity;
        while (!top.getPassengers().isEmpty()) {
            top = top.getPassengers().get(0);
        }
        return top;
    }
}
