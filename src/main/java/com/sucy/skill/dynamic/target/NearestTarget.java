/**
 * SkillAPI
 * com.sucy.skill.dynamic.target.NearestTarget
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
package com.sucy.skill.dynamic.target;

import com.sucy.skill.api.util.Nearby;
import com.sucy.skill.cast.*;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Applies child components to the closest all nearby entities around
 * each of the current targets.
 */
public class NearestTarget extends TargetComponent {
    private static final String RADIUS = "radius";

    private Preview preview;
    private double radius = 0;

    /** {@inheritDoc} */
    @Override
    List<LivingEntity> getTargets(
            final LivingEntity caster, final int level, final List<LivingEntity> targets) {

        final double radius = parseValues(caster, RADIUS, level, 3.0);
        final List<LivingEntity> result = new ArrayList<>();
        for (LivingEntity target : targets) {
            final Comparator<LivingEntity> comparator = new DistanceComparator(target.getLocation());
            Nearby.getLivingNearby(target, radius).stream()
                    .min(comparator)
                    .ifPresent(result::add);

        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    void playPreview(Player caster, final int level, final LivingEntity target, int step) {
        double currentRadius = parseValues(caster, RADIUS, level, 3.0);
        if (preview == null || currentRadius != radius) {
            radius = currentRadius;
            preview = previewType == PreviewType.DIM_2 ?
                    new CirclePreview(radius) :
                    new SpherePreview(radius);
        }
        preview.playParticles(caster, PreviewSettings.particle, target.getLocation().add(0, 0.1, 0), step);
    }

    @Override
    public String getKey() {
        return "nearest";
    }

    private static class DistanceComparator implements Comparator<LivingEntity> {
        private Location loc;

        private DistanceComparator(final Location loc) {
            this.loc = loc;
        }

        @Override
        public int compare(final LivingEntity o1, final LivingEntity o2) {
            return Double.compare(o1.getLocation().distanceSquared(loc), o2.getLocation().distanceSquared(loc));
        }
    }
}
