/**
 * SkillAPI
 * com.sucy.skill.dynamic.target.AreaTarget
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

import com.sucy.skill.api.util.Nearby;
import com.sucy.skill.cast.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Applies child components to the closest all nearby entities around
 * each of the current targets.
 */
public class AreaTarget extends TargetComponent {
    private static final String RADIUS = "radius";
    private static final String RANDOM = "random";

    private RoundPreview preview;

    private final Random random = new Random();

    /** {@inheritDoc} */
    @Override
    List<LivingEntity> getTargets(
            final LivingEntity caster, final int level, final List<LivingEntity> targets) {

        final double  radius = parseValues(caster, RADIUS, level, 3.0);
        final boolean random = settings.getBool(RANDOM, false);
        return determineTargets(caster, level, targets, t -> shuffle(Nearby.getLivingNearby(t, radius, true), random));
    }

    /** {@inheritDoc} */
    @Override
    void playPreview(Player caster, final int level, final LivingEntity target, int step) {
        double radius = parseValues(caster, RADIUS, level, 3.0);
        if (preview == null || radius != preview.getRadius()) {
            preview = previewType == PreviewType.DIM_2 ?
                    new CirclePreview(radius) :
                    new SpherePreview(radius);
        }
        preview.playParticles(caster, PreviewSettings.particle, target.getLocation().add(0, 0.1, 0), step);
    }

    @Override
    public String getKey() {
        return "area";
    }

    private List<LivingEntity> shuffle(final List<LivingEntity> targets, final boolean random) {
        if (!random) return targets;

        final List<LivingEntity> list = new ArrayList<>();
        while (!targets.isEmpty()) {
            list.add(targets.remove(this.random.nextInt(targets.size())));
        }
        return list;
    }
}
