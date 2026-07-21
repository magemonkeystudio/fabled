/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.mechanic.DamageMechanic
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
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.FixedMetadataValue;
import studio.magemonkey.fabled.Fabled;

import java.util.List;
import java.util.Locale;

/**
 * Deals damage to each target
 */
public class DamageMechanic extends MechanicComponent {
    private static final String TYPE            = "type";
    private static final String DAMAGE          = "value";
    private static final String TRUE            = "true";
    private static final String CLASSIFIER      = "classifier";
    private static final String KNOCKBACK       = "knockback";
    private static final String IGNORE_DIVINITY = "ignore-divinity";
    private static final String CAUSE           = "cause";
    private static final String NO_SHAKE        = "no-shake";
    private static final String IGNORE_CRIT     = "divinity-ignore-crit";
    private static final String IGNORE_DODGE    = "divinity-ignore-dodge";
    private static final String IGNORE_BLOCK    = "divinity-ignore-block";
    private static final String NO_BLEED        = "divinity-no-bleed";
    private static final String NO_VAMP         = "divinity-no-vamp";
    private static final String IGNORE_SKILL_CRIT  = "ignore-skill-crit";

    static final String DMG_FLAGS_META = "fabled_dmg_flags";

    @Override
    public String getKey() {
        return "damage";
    }

    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        String  pString        = settings.getString(TYPE, "damage").toLowerCase();
        boolean percent        = pString.equals("multiplier") || pString.equals("percent");
        boolean missing        = pString.equals("percent missing");
        boolean left           = pString.equals("percent left");
        boolean trueDmg        = settings.getBool(TRUE, false);
        double  damage         = parseValues(caster, DAMAGE, level, 1.0);
        boolean knockback      = settings.getBool(KNOCKBACK, true);
        boolean ignoreDivinity = settings.getBool(IGNORE_DIVINITY, false);
        boolean noShake        = settings.getBool(NO_SHAKE, false);
        String  classification = settings.getString(CLASSIFIER, "default");
        if (damage < 0) {
            return false;
        }

        int dmgFlags = 0;
        if (settings.getBool(IGNORE_CRIT,  false)) dmgFlags |= 0x01;
        if (settings.getBool(IGNORE_DODGE, false)) dmgFlags |= 0x02;
        if (settings.getBool(IGNORE_BLOCK, false)) dmgFlags |= 0x04;
        if (settings.getBool(NO_BLEED,     false)) dmgFlags |= 0x08;
        if (settings.getBool(NO_VAMP,      false)) dmgFlags |= 0x10;
        if (settings.getBool(IGNORE_SKILL_CRIT, false)) dmgFlags |= 0x20;
        if (dmgFlags != 0)
            caster.setMetadata(DMG_FLAGS_META, new FixedMetadataValue(Fabled.inst(), dmgFlags));

        try {
            for (LivingEntity target : targets) {
                if (target.isDead()) {
                    continue;
                }

                double amount = damage;
                if (percent) {
                    amount = damage * target.getMaxHealth() / 100;
                } else if (missing) {
                    amount = damage * (target.getMaxHealth() - target.getHealth()) / 100;
                } else if (left) {
                    amount = damage * target.getHealth() / 100;
                }
                if (trueDmg) {
                    skill.trueDamage(target, amount, caster);
                } else {
                    skill.damage(target,
                            amount,
                            caster,
                            classification,
                            knockback,
                            ignoreDivinity,
                            EntityDamageEvent.DamageCause.valueOf(settings.getString(CAUSE, "Entity Attack")
                                    .toUpperCase(Locale.US)
                                    .replace(' ', '_')),
                            noShake);
                }
            }
        } finally {
            if (dmgFlags != 0)
                caster.removeMetadata(DMG_FLAGS_META, Fabled.inst());
        }

        return !targets.isEmpty();
    }
}
