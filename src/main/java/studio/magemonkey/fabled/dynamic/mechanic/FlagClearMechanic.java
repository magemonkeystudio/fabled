/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.mechanic.FlagClearMechanic
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
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.util.FlagData;
import studio.magemonkey.fabled.api.util.FlagManager;

import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Applies a flag to each target
 */
public class FlagClearMechanic extends MechanicComponent {
    private static final String KEY   = "key";
    private static final String REGEX = "regex";

    @Override
    public String getKey() {
        return "flag clear";
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
        if (targets.size() == 0 || !settings.has(KEY)) {
            return false;
        }

        String  key      = settings.getString(KEY);
        boolean useRegex = settings.getBool(REGEX, false);

        if (useRegex) {
            Pattern pattern;
            try {
                pattern = Pattern.compile(key);
            } catch (PatternSyntaxException e) {
                Fabled.inst().getLogger().warning("Invalid regex pattern for flag clear mechanic: \"" + key + "\" - " + e.getDescription());
                return false;
            }
            for (LivingEntity target : targets) {
                FlagData data = FlagManager.getFlagData(target, false);
                if (data != null) {
                    new HashSet<>(data.flagList()).stream()
                            .filter(f -> pattern.matcher(f).matches())
                            .forEach(f -> FlagManager.removeFlag(target, f));
                }
            }
        } else {
            for (LivingEntity target : targets) {
                FlagManager.removeFlag(target, key);
            }
        }
        return targets.size() > 0;
    }
}
