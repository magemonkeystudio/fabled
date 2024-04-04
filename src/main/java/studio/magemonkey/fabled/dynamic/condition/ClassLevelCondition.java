/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.condition.ClassLevelCondition
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 Mage Monkey Studios
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

import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.player.PlayerClass;
import studio.magemonkey.fabled.dynamic.DynamicSkill;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class ClassLevelCondition extends ConditionComponent {
    private static final String MIN_LEVEL = "min-level";
    private static final String MAX_LEVEL = "max-level";

    private int min, max;

    @Override
    public String getKey() {
        return "class level";
    }

    @Override
    public void load(DynamicSkill skill, DataSection config) {
        super.load(skill, config);
        min = settings.getInt(MIN_LEVEL);
        max = settings.getInt(MAX_LEVEL);
    }

    @Override
    boolean test(final LivingEntity caster, final int level, final LivingEntity target) {
        if (!(target instanceof Player)) return false;

        final PlayerClass playerClass = Fabled.getPlayerData((Player) target).getMainClass();
        return playerClass != null && playerClass.getLevel() >= min && playerClass.getLevel() <= max;
    }
}
