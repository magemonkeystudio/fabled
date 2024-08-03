/*
 * Fabled
 * studio.magemonkey.fabled.dynamic.target.WorldTarget
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2023 MageMonkeyStudio
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
package studio.magemonkey.fabled.dynamic.target;

import org.bukkit.entity.LivingEntity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Applies child components to all entities in the world.
 */
public class WorldTarget extends TargetComponent {
    @Override
    public String getKey() {
        return "world";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    List<LivingEntity> getTargets(
            final LivingEntity caster, final int level, final List<LivingEntity> targets) {
        final int max = (int) parseValues(caster, MAX, level, 99);
        return caster.getWorld().getLivingEntities().stream()
                .filter(e -> isValidTarget(caster, caster, e) || (self.equals(IncludeCaster.IN_AREA) && caster == e))
                .limit(max)
                .collect(Collectors.toList());
    }
}
