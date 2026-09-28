/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.mechanic.StatusMechanic
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
import studio.magemonkey.divinity.stats.EntityStats;
import studio.magemonkey.divinity.stats.items.attributes.api.TypedStat;
import studio.magemonkey.fabled.api.util.FlagManager;
import studio.magemonkey.fabled.hook.PluginChecker;

import java.util.List;

/**
 * Applies a flag to each target
 */
public class StatusMechanic extends MechanicComponent {
    private static final String KEY              = "status";
    private static final String DURATION         = "duration";
    private static final String IGNORE_CC_RES    = "ignore-cc-resistance";
    private static final String IGNORE_CC_DUR    = "ignore-cc-duration";

    @Override
    public String getKey() {
        return "status";
    }

    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        if (targets.isEmpty() || !settings.has(KEY)) {
            return false;
        }

        String  key           = settings.getString(KEY, "stun").toLowerCase();
        double  seconds       = parseValues(caster, DURATION, level, 3.0);
        boolean ignoreCCRes   = settings.getBool(IGNORE_CC_RES, false);
        boolean ignoreCCDur   = settings.getBool(IGNORE_CC_DUR, false);
        int baseTicks = (int) (seconds * 20);
        if (!ignoreCCDur && PluginChecker.isDivinityActive()) {
            try {
                EntityStats casterStats = EntityStats.get(caster);
                double      ccDuration  = casterStats.getItemStat(TypedStat.Type.CC_DURATION, false);
                if (ccDuration != 0) {
                    baseTicks = (int) (baseTicks * (1.0 + ccDuration / 100.0));
                }
            } catch (Throwable ignored) { /* Divinity present but incompatible/misbehaving */ }
        }

        for (LivingEntity target : targets) {
            int ticks = baseTicks;
            if (!ignoreCCRes && PluginChecker.isDivinityActive()) {
                try {
                    EntityStats stats      = EntityStats.get(target);
                    double      resistance = stats.getItemStat(TypedStat.Type.CC_RESISTANCE, false);
                    if (resistance > 0) {
                        ticks = (int) (ticks * (1.0 - resistance / 100.0));
                    }
                } catch (Throwable ignored) { /* Divinity present but incompatible/misbehaving */ }
            }
            FlagManager.addFlag(target, key, ticks);
        }
        return !targets.isEmpty();
    }
}
