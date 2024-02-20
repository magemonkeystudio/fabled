/**
 * SkillAPI
 * com.sucy.skill.dynamic.mechanic.warp.WarpMechanic
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
import com.sucy.skill.api.particle.ParticleHelper;
import com.sucy.skill.api.target.TargetHelper;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Warps the target relative to their current location
 */
public class WarpMechanic extends AbstractWarpingMechanic {
    private static final Vector UP = new Vector(0, 1, 0);

    private static final String WALL    = "walls";
    private static final String OPEN    = "open";
    private static final String FORWARD = "forward";
    private static final String UPWARD  = "upward";
    private static final String RIGHT   = "right";

    @Override
    public String getKey() {
        return "warp";
    }

    private Location getLocation(LivingEntity caster, int level, LivingEntity target) {
        boolean throughWalls = settings.getBool(WALL, false);
        boolean openOnly     = settings.getBool(OPEN, true);
        double  forward      = parseValues(caster, FORWARD, level, 0.0);
        double  upward       = parseValues(caster, UPWARD, level, 0.0);
        double  right        = parseValues(caster, RIGHT, level, 0.0);

        Vector   dir  = target.getLocation().getDirection();
        Vector   side = dir.clone().crossProduct(UP).multiply(right);
        Location loc  = target.getLocation().add(dir.multiply(forward)).add(side).add(0, upward, 0);

        if (openOnly) {
            loc.add(0, 1, 0);
            loc = TargetHelper.getOpenLocation(target.getLocation().add(0, 1, 0), loc, throughWalls);
            if (!loc.getBlock().getType().isSolid() && loc.getBlock().getRelative(BlockFace.DOWN).getType().isSolid()) {
                loc.add(0, 1, 0);
            }
            return loc.subtract(0, 1, 0);
        }

        return loc;
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
        if (targets.isEmpty()) return false;
        for (LivingEntity target : targets) {
            warp(target, caster, getLocation(caster, level, target), level);
        }
        return true;
    }

    @Override
    public void playPreview(List<Runnable> onPreviewStop, Player caster, int level, Supplier<List<LivingEntity>> targetSupplier) {
        if (preview.getBool("per-target")) {
            BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {
                    for (LivingEntity target : targetSupplier.get()) {
                        ParticleHelper.play(getLocation(caster, level, target), preview, Set.of(caster), "per-target-",
                                preview.getBool("per-target-" + "hitbox") ? target.getBoundingBox() : null
                        );
                    }
                }
            }.runTaskTimer(SkillAPI.inst(), 0, Math.max(1, preview.getInt("per-target-" + "period", 5)));
            onPreviewStop.add(task::cancel);
        }
    }
}
