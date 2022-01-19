/**
 * SkillAPI
 * com.sucy.skill.dynamic.mechanic.DamageMechanic
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
package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.hook.VaultHook;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

/**
 * Deals damage to each target
 */
public class MoneyMechanic extends MechanicComponent {
    private static final String TYPE = "type";
    private static final String AMOUNT = "amount";
    private static final String ALLOWS_NEGATIVE = "allows_negative";

    @Override
    public String getKey() {
        return "money";
    }

    /**
     * Executes the component
     *
     * @param caster  caster of the skill
     * @param level   level of the skill
     * @param targets targets to apply to
     *
     * @param force
     * @return true if applied to something, false otherwise
     */
    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        if (!VaultHook.isEconomyValid()) { return false; }

        boolean multiply = settings.getString(TYPE, "add").equalsIgnoreCase("multiply");

        double amount = parseValues(caster, AMOUNT, level, 1);
        boolean allowsNegative = settings.getBool(ALLOWS_NEGATIVE, false);

        boolean worked = false;
        for (LivingEntity target : targets) {
            if (!(target instanceof Player)) {
                continue;
            }
            Player player = (Player) target;
            double balance = VaultHook.getBalance(player);

            double difference = multiply ? balance*(amount-1) : amount;
            if (!allowsNegative && balance+difference < 0) { continue; }

            EconomyResponse.ResponseType result = null;
            if (difference > 0) {
                result = VaultHook.deposit(player, difference).type;
            } else if (difference < 0) {
                result = VaultHook.withdraw(player, -difference).type;
            }
            if (result == EconomyResponse.ResponseType.SUCCESS) { worked = true; }
        }
        return worked;
    }
}
