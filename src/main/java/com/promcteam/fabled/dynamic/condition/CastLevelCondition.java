/**
 * Fabled
 * com.promcteam.fabled.dynamic.condition.CastLevelCondition
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 ProMCTeam
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
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
package com.promcteam.fabled.dynamic.condition;

import com.promcteam.fabled.dynamic.DynamicSkill;
import com.promcteam.codex.mccore.config.parse.DataSection;
import org.bukkit.entity.LivingEntity;

import java.util.List;

public class CastLevelCondition extends ConditionComponent {
    private static final String MIN_LEVEL = "min-level";
    private static final String MAX_LEVEL = "max-level";

    private int min, max;

    @Override
    public void load(DynamicSkill skill, DataSection config) {
        super.load(skill, config);
        min = settings.getInt(MIN_LEVEL, 1);
        max = settings.getInt(MAX_LEVEL, 99);
    }

    @Override
    public String getKey() {
        return "cast level";
    }

    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        return test(caster, level, null) && executeChildren(caster, level, targets, force);
    }

    @Override
    boolean test(final LivingEntity caster, final int level, final LivingEntity target) {
        return level >= min && level <= max;
    }
}
