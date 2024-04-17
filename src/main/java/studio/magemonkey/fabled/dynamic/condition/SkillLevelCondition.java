/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.condition.SkillLevelCondition
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 MageMonkeyStudio
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
package studio.magemonkey.fabled.dynamic.condition;

import studio.magemonkey.fabled.api.player.PlayerSkill;
import org.bukkit.entity.LivingEntity;

import java.util.List;

public class SkillLevelCondition extends ConditionComponent {
    private static final String SKILL     = "skill";
    private static final String MIN_LEVEL = "min-level";
    private static final String MAX_LEVEL = "max-level";

    @Override
    public String getKey() {
        return "skill level";
    }

    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        return test(caster, level, null) && executeChildren(caster, level, targets, force);
    }

    @Override
    boolean test(final LivingEntity caster, final int level, final LivingEntity target) {
        final int min = settings.getInt(MIN_LEVEL, 1);
        final int max = settings.getInt(MAX_LEVEL, 99);

        final String      skill          = settings.getString(SKILL, "");
        final PlayerSkill triggeredSkill = getSkillData(caster);
        if (triggeredSkill == null) {
            return false;
        }
        PlayerSkill data = triggeredSkill.getPlayerData().getSkill(skill);
        if (data == null) {
            data = triggeredSkill;
        }

        return data.getLevel() >= min && data.getLevel() <= max;
    }
}
