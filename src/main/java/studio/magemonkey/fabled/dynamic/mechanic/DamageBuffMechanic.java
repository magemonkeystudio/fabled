/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.mechanic.DamageBuffMechanic
 * <p>
 * The MIT License (MIT)
 * <p>
 * © 2026 VoidEdge
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
import studio.magemonkey.fabled.api.util.Buff;
import studio.magemonkey.fabled.api.util.BuffManager;
import studio.magemonkey.fabled.api.util.BuffType;

import java.util.List;

/**
 * Represents the mechanic for applying a damage buff to targets.
 * This class handles the logic for adding either skill or flat damage buffs
 * to a list of LivingEntity targets for a specified duration.
 */
public class DamageBuffMechanic extends MechanicComponent {
    private static final String TYPE           = "type";
    private static final String SKILL          = "skill";
    private static final String VALUE          = "value";
    private static final String SECONDS        = "seconds";
    private static final String CLASSIFICATION = "classification";

    @Override
    public String getKey() {
        return "damage buff";
    }

    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        if (targets.isEmpty()) {
            return false;
        }

        boolean skill   = settings.getString(SKILL, "false").equalsIgnoreCase("true");
        boolean percent = settings.getString(TYPE, "flat").equalsIgnoreCase("multiplier");
        double  value   = parseValues(caster, VALUE, level, 1.0);
        double  seconds = parseValues(caster, SECONDS, level, 3.0);
        int     ticks   = (int) (seconds * 20);
        for (LivingEntity target : targets) {
            BuffManager.getBuffData(target, true).addBuff(
                    (skill ? BuffType.SKILL_DAMAGE : BuffType.DAMAGE).getLocalizedName(),
                    skill ? settings.getString(CLASSIFICATION, "default") : null,
                    new Buff(this.skill.getName(), value, percent),
                    ticks);
        }
        return !targets.isEmpty();
    }
}
