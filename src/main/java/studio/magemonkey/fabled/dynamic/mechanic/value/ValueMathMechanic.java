/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.mechanic.value.ValueMathMechanic
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
package studio.magemonkey.fabled.dynamic.mechanic.value;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import studio.magemonkey.codex.util.eval.Evaluator;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.dynamic.DynamicSkill;
import studio.magemonkey.fabled.dynamic.mechanic.MechanicComponent;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Adds to a cast data value
 */
public class ValueMathMechanic extends MechanicComponent {
    private static final String KEY      = "key";
    private static final String FUNCTION = "function";
    private static final String SAVE     = "save";

    private static final Pattern placeholderPattern = Pattern.compile("\\{([^}]+)}");

    @Override
    public String getKey() {
        return "value math";
    }

    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        if (targets.isEmpty() || !settings.has(KEY)) {
            return false;
        }

        String   key  = settings.getString(KEY);
        String   func = filter(caster, targets.get(0), settings.getString(FUNCTION));
        CastData data = DynamicSkill.getCastData(caster);

        // If there are any placeholders remaining, replace them with 0 and log the remaining placeholders as a warning
        if (placeholderPattern.matcher(func).find()) {
            Fabled.inst().getLogger().warning("Invalid math function: \"" + func + "\", contains unresolved placeholders. We'll help you out and replace them with 0.");
            func = placeholderPattern.matcher(func).replaceAll("0");
        }

        double amount = Evaluator.eval(func, 1);
        if (Double.isInfinite(amount) || Double.isNaN(amount)) {
            Fabled.inst().getLogger().warning("Invalid math function: \"" + func + "\", produced: " + amount);
            return false;
        }

        data.put(key, amount);
        if (settings.getBool(SAVE, false))
            Fabled.getData((OfflinePlayer) caster).setPersistentData(key, data.getRaw(key));
        return true;
    }
}
