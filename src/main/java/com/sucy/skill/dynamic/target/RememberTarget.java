/**
 * SkillAPI
 * com.sucy.skill.dynamic.target.RememberTarget
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
import com.sucy.skill.cast.PreviewSettings;
import com.sucy.skill.dynamic.DynamicSkill;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Applies a flag to each target
 */
public class RememberTarget extends TargetComponent {
    private static final String KEY = "key";

    /** {@inheritDoc} */
    @Override
    public void playPreview(Player caster, final int level, final LivingEntity target, int step) {
        final List<LivingEntity> targets = getTargets(caster, level, null);
        for (LivingEntity entity : targets) {
            switch (previewType) {
                case DIM_2:
                    circlePreview.playParticles(caster, PreviewSettings.particle, entity.getLocation().add(0, 0.1, 0), step);
                    break;
                case DIM_3:
                    spherePreview.playParticles(caster, PreviewSettings.particle, entity.getLocation().add(0, 0.1, 0), step);
                    break;
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    List<LivingEntity> getTargets(
            final LivingEntity caster, final int level, final List<LivingEntity> targets) {
        return remember(caster, settings.getString(KEY));
    }

    @SuppressWarnings("unchecked")
    public static List<LivingEntity> remember(final LivingEntity caster, final String key) {
        final Object data = DynamicSkill.getCastData(caster).get(key);
        //proper skill setup should cause this to work
        return data == null ? ImmutableList.of() : (List<LivingEntity>) data;
    }

    @Override
    public String getKey() {
        return "remember";
    }
}
