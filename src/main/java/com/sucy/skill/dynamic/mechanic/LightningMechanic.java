/**
 * SkillAPI
 * com.sucy.skill.dynamic.mechanic.LightningMechanic
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.dynamic.TempEntity;
import com.sucy.skill.listener.MechanicListener;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Strikes lightning about each target with an offset
 */
public class LightningMechanic extends MechanicComponent {
    private static final Vector up = new Vector(0, 1, 0);

    private static final String DAMAGE = "damage";
    private static final String GROUP = "group";
    private static final String CASTER = "caster";
    private static final String FORWARD = "forward";
    private static final String RIGHT   = "right";

    @Override
    public String getKey() {
        return "lightning";
    }

    /**
     * Executes the component
     *
     * @param caster  caster of the skill
     * @param level   level of the skill
     * @param targets targets to apply to
     *
     * @param force
     * @return true if applied to something, false otherwise
     */
    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        if (targets.size() == 0) {
            return false;
        }
        double forward = parseValues(caster, FORWARD, level, 0);
        double right = parseValues(caster, RIGHT, level, 0);
        for (LivingEntity target : targets) {
            Vector dir = target.getLocation().getDirection().setY(0).normalize();
            Vector nor = dir.clone().crossProduct(up);
            Location loc = target.getLocation().add(dir.multiply(forward).add(nor.multiply(right)));
            LightningStrike lightning =  target.getWorld().strikeLightning(loc);
            SkillAPI.setMeta(lightning, MechanicListener.P_CALL, new Callback(caster, level, force));
        }
        return targets.size() > 0;
    }

    public class Callback {
        private final LivingEntity caster;
        private final int level;
        private final boolean force;
        private final List<LivingEntity> struckEntities = new ArrayList<>();

        public Callback(LivingEntity caster, int level, boolean force) {
            this.caster = caster;
            this.level = level;
            this.force = force;
        }

        public double execute(LivingEntity entity) {
            if (struckEntities.contains(entity)) {
                return -1; // Deals with MC-72028 (lightning bolts sometimes dealing damage twice)
            }
            boolean canTarget = false;
            if (SkillAPI.getSettings().isValidTarget(entity)) {
                String group = settings.getString(GROUP, "ENEMY").toUpperCase();
                if (caster != entity) {
                    canTarget = group.equals("BOTH") || group.equals("ALLY") == SkillAPI.getSettings().isAlly(caster, entity);
                } else {
                    canTarget = settings.getBool(CASTER, false);
                }
            }
            if (canTarget) {
                executeChildren(caster, level, Collections.singletonList(entity), force);
                struckEntities.add(entity);
                return parseValues(caster, DAMAGE, level, 5);
            } else {
                return -1;
            }
        }
    }
}
