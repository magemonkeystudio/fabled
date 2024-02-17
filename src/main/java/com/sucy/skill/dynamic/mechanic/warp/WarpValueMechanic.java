/**
 * SkillAPI
 * com.sucy.skill.dynamic.mechanic.warp.WarpValueMechanic
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
import com.sucy.skill.api.CastData;
import com.sucy.skill.api.particle.ParticleHelper;
import com.sucy.skill.dynamic.DynamicSkill;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Applies a flag to each target
 */
public class WarpValueMechanic extends AbstractWarpingMechanic {
    private static final String KEY = "key";

    @Override
    public String getKey() {
        return "warp value";
    }

    @Nullable
    private Location parseLocation(LivingEntity caster) {
        CastData data = DynamicSkill.getCastData(caster);
        if (data == null) return null;
        String key = settings.getString(KEY);
        if (key == null) return null;
        Object obj = data.getRaw(key);
        if (obj instanceof Location) return (Location) obj;
        return null;
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

        Location loc = parseLocation(caster);
        if (loc == null) return false;
        for (LivingEntity target : targets) {
            warp(target, caster, loc, level);
        }
        return true;
    }

    @Override
    public void playPreview(List<Runnable> onPreviewStop,
                            Player caster,
                            int level,
                            Supplier<List<LivingEntity>> targetSupplier) {
        if (preview.getBool("per-target")) {
            BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {
                    Location loc = parseLocation(caster);
                    if (loc == null) return;
                    ParticleHelper.play(loc, preview, Set.of(caster), "per-target-", null);
                }
            }.runTaskTimer(SkillAPI.inst(), 0, Math.max(1, preview.getInt("per-target-" + "period", 5)));
            onPreviewStop.add(task::cancel);
        }
    }
}
