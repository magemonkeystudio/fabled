/**
 * SkillAPI
 * com.sucy.skill.dynamic.target.SingleTarget
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
package com.sucy.skill.dynamic.target;

import com.google.common.collect.ImmutableList;
import com.sucy.skill.api.target.TargetHelper;
import com.sucy.skill.cast.ConePreview;
import com.sucy.skill.cast.PreviewSettings;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Applies child components to the closest linear entity of each of the
 * provided targets.
 */
public class SingleTarget extends TargetComponent {
    private static final String RANGE     = "range";
    private static final String TOLERANCE = "tolerance";

    private ConePreview preview;

    /**
     * {@inheritDoc}
     */
    @Override
    public void playPreview(Player caster, int level, LivingEntity target, int step) {
        double arc    = parseValues(caster, TOLERANCE, level, 4.0) * Math.PI / 180;
        double radius = parseValues(caster, RANGE, level, 3.0);
        if (preview == null || preview.getArc() != arc || preview.getRadius() != radius) {
            preview = new ConePreview(arc, radius);
        }
        preview.playParticles(caster, PreviewSettings.particle, target.getLocation(), step);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    List<LivingEntity> getTargets(
            final LivingEntity caster, final int level, final List<LivingEntity> targets) {

        double range     = parseValues(caster, RANGE, level, 5.0);
        double tolerance = parseValues(caster, TOLERANCE, level, 4.0);
        return determineTargets(caster, level, targets, t -> {
            final LivingEntity target = TargetHelper.getLivingTarget(t, range, tolerance);
            return target == null ? ImmutableList.of() : ImmutableList.of(target);
        });
    }

    @Override
    public String getKey() {
        return "single";
    }
}
