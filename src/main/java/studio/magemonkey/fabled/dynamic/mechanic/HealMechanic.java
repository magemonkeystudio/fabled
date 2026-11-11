/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.mechanic.HealMechanic
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

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import studio.magemonkey.divinity.stats.EntityStats;
import studio.magemonkey.divinity.stats.items.attributes.api.TypedStat;
import studio.magemonkey.fabled.api.event.SkillHealEvent;
import studio.magemonkey.fabled.hook.PluginChecker;

import java.util.List;

/**
 * Heals each target
 */
public class HealMechanic extends MechanicComponent {
    private static final String TYPE                    = "type";
    private static final String VALUE                   = "value";
    private static final String IGNORE_HEALING_CAST     = "ignore-healing-cast";
    private static final String IGNORE_HEALING_RECEIVED = "ignore-healing-received";

    @Override
    public String getKey() {
        return "heal";
    }

    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        boolean percent              = settings.getString(TYPE, "health").toLowerCase().equals("percent");
        double  value                = parseValues(caster, VALUE, level, 1.0);
        boolean ignoreHealingCast    = settings.getBool(IGNORE_HEALING_CAST, false);
        boolean ignoreHealingReceived = settings.getBool(IGNORE_HEALING_RECEIVED, false);
        if (value < 0) {
            return false;
        }
        for (LivingEntity target : targets) {
            if (target.isDead()) {
                continue;
            }

            double amount = value;
            if (percent) {
                amount = target.getMaxHealth() * value / 100;
            }

            if (!ignoreHealingCast && PluginChecker.isDivinityActive()) {
                try {
                    double healCast = EntityStats.get(caster).getItemStat(TypedStat.Type.HEALING_CAST, false);
                    if (healCast != 0) amount *= (1.0 + healCast / 100.0);
                } catch (Throwable ignored) { /* Divinity present but incompatible/misbehaving */ }
            }

            if (!ignoreHealingReceived && PluginChecker.isDivinityActive()) {
                try {
                    double healReceived = EntityStats.get(target).getItemStat(TypedStat.Type.HEALING_RECEIVED, false);
                    if (healReceived != 0) amount *= (1.0 + healReceived / 100.0);
                } catch (Throwable ignored) { /* Divinity present but incompatible/misbehaving */ }
            }

            SkillHealEvent event = new SkillHealEvent(caster, target, amount);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                target.setHealth(Math.min(target.getHealth() + event.getAmount(), target.getMaxHealth()));
            }
        }
        return targets.size() > 0;
    }
}
