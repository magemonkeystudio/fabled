/**
 * SkillAPI
 * com.sucy.skill.dynamic.condition.HealthCondition
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
package com.sucy.skill.dynamic.condition;

import com.sucy.skill.hook.VaultHook;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Checks if the player's balance is within a range, using Vault
 */
public class MoneyCondition extends ConditionComponent {
    private static final String TYPE      = "type";
    private static final String MIN_VALUE = "min-value";
    private static final String MAX_VALUE = "max-value";

    private enum CompareType {
        MIN, MAX, BETWEEN
    }

    @Override
    boolean test(final LivingEntity caster, final int level, final LivingEntity target) {
        if (!VaultHook.isEconomyValid() || !(target instanceof Player)) {
            return false;
        }

        CompareType type = CompareType.BETWEEN;
        try {
            type = CompareType.valueOf(settings.getString(TYPE, "between").toUpperCase());
        } catch (IllegalArgumentException ignored) {
        }

        double  balance = VaultHook.getBalance((Player) target);
        boolean result  = false;
        switch (type) {
            case MIN:
                result = balance >= parseValues(caster, MIN_VALUE, level, 0);
                break;
            case MAX:
                result = balance <= parseValues(caster, MAX_VALUE, level, 0);
                break;
            case BETWEEN:
                result = balance >= parseValues(caster, MIN_VALUE, level, 0)
                        && balance <= parseValues(caster, MAX_VALUE, level, 0);
        }
        return result;
    }

    @Override
    public String getKey() {
        return "money";
    }
}
