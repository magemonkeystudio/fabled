/**
 * ProSkillAPI
 * com.sucy.skill.dynamic.condition.DistanceCondition
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2023 ProMCTeam
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
package com.sucy.skill.dynamic.condition;

import org.bukkit.entity.LivingEntity;

/**
 * A condition for dynamic skills that requires the target to fit the distance requirement
 */
public class DistanceCondition extends ConditionComponent {
    private static final String MIN = "min-value";
    private static final String MAX = "max-value";

    @Override
    public String getKey() {
        return "distance";
    }

    @Override
    boolean test(final LivingEntity caster, final int level, final LivingEntity target) {
        double min = parseValues(caster, MIN, level, 0);
        double max = parseValues(caster, MAX, level, 50);
        // Square values, so distanceSquared can be used to decrease load
        min = min * min;
        max = max * max;

        double value = target.getLocation().distanceSquared(caster.getLocation());
        return value >= min && value <= max;
    }
}
