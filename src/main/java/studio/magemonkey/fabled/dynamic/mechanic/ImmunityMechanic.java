/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.mechanic.ImmunityMechanic
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
import studio.magemonkey.fabled.api.util.FlagManager;
import org.bukkit.entity.LivingEntity;

import java.util.List;
import java.util.Locale;

/**
 * Applies a damage immunity flag to each target
 */
public class ImmunityMechanic extends MechanicComponent {
    public static final String META_KEY = "sapi_immunity";

    private static final String TYPE       = "type";
    private static final String SECONDS    = "seconds";
    private static final String MULTIPLIER = "multiplier";

    @Override
    public String getKey() {
        return "immunity";
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
        if (targets.size() == 0 || !settings.has(TYPE)) {
            return false;
        }

        String key        = settings.getString(TYPE);
        double seconds    = parseValues(caster, SECONDS, level, 3.0);
        double multiplier = parseValues(caster, MULTIPLIER, level, 0);
        int    ticks      = (int) (seconds * 20);
        for (LivingEntity target : targets) {
            FlagManager.addFlag(target, "immune:" + key.toUpperCase(Locale.US).replace(" ", "_"), ticks);
            Fabled.setMeta(target, META_KEY, multiplier);
        }
        return targets.size() > 0;
    }
}
