/**
 * SkillAPI
 * com.sucy.skill.dynamic.mechanic.warp.WarpRandomMechanic
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
package com.sucy.skill.dynamic.mechanic.warp;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.target.TargetHelper;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;

import java.util.List;

/**
 * Warps a random distance
 */
public class WarpRandomMechanic extends AbstractWarpingMechanic {
    private static final String WALL       = "walls";
    private static final String HORIZONTAL = "horizontal";
    private static final String DISTANCE   = "distance";

    @Override
    public String getKey() {
        return "warp random";
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

        // Get the world
        boolean throughWalls = settings.getString(WALL, "false").toLowerCase().equals("true");
        boolean horizontal   = !settings.getString(HORIZONTAL, "true").toLowerCase().equals("false");
        double  distance     = parseValues(caster, DISTANCE, level, 3.0);

        for (LivingEntity target : targets) {
            Location loc;
            Location temp = target.getLocation();
            do {
                loc = temp.clone().add(rand(distance), 0, rand(distance));
                if (!horizontal) {
                    loc.add(0, rand(distance), 0);
                }
            }
            while (temp.distanceSquared(loc) > distance * distance);
            loc = TargetHelper.getOpenLocation(target.getLocation().add(0, 1, 0), loc, throughWalls);
            if (!loc.getBlock().getType().isSolid() && loc.getBlock().getRelative(BlockFace.DOWN).getType().isSolid()) {
                loc.add(0, 1, 0);
            }
            warp(target, caster, loc.subtract(0, 1, 0), level);
        }
        return !targets.isEmpty();
    }

    private double rand(double distance) {
        return SkillAPI.RANDOM.nextDouble() * distance * 2 - distance;
    }
}
