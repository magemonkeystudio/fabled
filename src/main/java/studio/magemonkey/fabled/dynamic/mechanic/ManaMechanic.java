/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.mechanic.ManaMechanic
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
package studio.magemonkey.fabled.dynamic.mechanic;

import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.enums.ManaCost;
import studio.magemonkey.fabled.api.enums.ManaSource;
import studio.magemonkey.fabled.api.player.PlayerData;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Gives mana to each target
 */
public class ManaMechanic extends MechanicComponent {
    private static final String TYPE  = "type";
    private static final String VALUE = "value";

    @Override
    public String getKey() {
        return "mana";
    }

    /**
     * Executes the component
     *
     * @param caster  caster of the skill
     * @param level   level of the skill
     * @param targets targets to apply to
     * @param force
     * @return true if applied to something, false otherwise
     */
    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        boolean percent = settings.getString(TYPE, "mana").toLowerCase().equals("percent");
        double  value   = parseValues(caster, VALUE, level, 1.0);

        boolean worked = false;
        for (LivingEntity target : targets) {
            if (!(target instanceof Player)) {
                continue;
            }

            worked = true;

            PlayerData data = Fabled.getPlayerData((Player) target);
            double     amount;
            if (percent) {
                amount = data.getMaxMana() * value / 100;
            } else {
                amount = value;
            }

            if (amount > 0) {
                data.giveMana(amount, ManaSource.SKILL);
            } else {
                data.useMana(-amount, ManaCost.SKILL_EFFECT);
            }
        }
        return worked;
    }
}
