/**
 * SkillAPI
 * com.sucy.skill.dynamic.mechanic.AbstractWarping
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
package com.sucy.skill.dynamic.mechanic.warp;

import com.sucy.skill.dynamic.mechanic.MechanicComponent;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

abstract class AbstractWarpingMechanic extends MechanicComponent {
    protected static final String PRESERVE  = "preserve";
    protected static final String SET_YAW   = "setYaw";
    protected static final String SET_PITCH = "setPitch";
    protected static final String YAW       = "yaw";
    protected static final String PITCH     = "pitch";

    public boolean preserveVelocity() {
        return settings.getBool(PRESERVE, false);
    }

    public boolean setYaw() {
        return settings.getBool(SET_YAW, false);
    }

    public boolean setPitch() {
        return settings.getBool(SET_PITCH, false);
    }

    public void warp(LivingEntity target, LivingEntity caster, Location location, int level) {
        if (setYaw()) {
            location.setYaw((float) parseValues(caster, YAW, level, 0));
        }
        if (setPitch()) {
            location.setPitch((float) parseValues(caster, PITCH, level, 0));
        }

        Vector velocity = target.getVelocity().clone();
        target.teleport(location);

        if (preserveVelocity()) {
            target.setVelocity(velocity);
        }
    }
}
