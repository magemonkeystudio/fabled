/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.mechanic.PotionMechanic
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

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Locale;

/**
 * Executes child components after a delay
 */
public class PotionMechanic extends MechanicComponent {
    private static final String POTION  = "potion";
    private static final String AMBIENT = "ambient";
    private static final String TIER    = "tier";
    private static final String SECONDS = "seconds";

    @Override
    public String getKey() {
        return "potion";
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
        if (targets.size() == 0) {
            return false;
        }

        try {
            PotionEffectType potion = PotionEffectType.getByName(settings.getString(POTION, "Absorption")
                    .toUpperCase(Locale.US)
                    .replace(' ', '_'));
            int     tier    = (int) parseValues(caster, TIER, level, 1) - 1;
            double  seconds = parseValues(caster, SECONDS, level, 3.0);
            boolean ambient = settings.getBool(AMBIENT, true);
            int     ticks   = (int) (seconds * 20);
            for (LivingEntity target : targets) {
                target.addPotionEffect(new PotionEffect(potion, ticks, tier, ambient, ambient));
            }
            return targets.size() > 0;
        } catch (Exception ex) {
            return false;
        }
    }
}
