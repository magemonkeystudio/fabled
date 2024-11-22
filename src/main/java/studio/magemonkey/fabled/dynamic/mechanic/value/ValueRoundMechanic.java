/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.mechanic.value.ValueRoundMechanic
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 MageMonkeyStudio
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
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.dynamic.DynamicSkill;
import studio.magemonkey.fabled.dynamic.mechanic.MechanicComponent;

import java.util.List;
import java.util.Locale;

/**
 * Adds to a cast data value
 */
public class ValueRoundMechanic extends MechanicComponent {
    private static final String KEY  = "key";
    private static final String TYPE = "type";
    private static final String SAVE = "save";

    @Override
    public String getKey() {
        return "value round";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        if (targets.isEmpty() || !settings.has(KEY)) {
            return false;
        }

        String   key  = settings.getString(KEY);
        String   type = settings.getString(TYPE).toUpperCase(Locale.US);
        CastData data = DynamicSkill.getCastData(caster);
        if (data.contains(key)) {
            double value = data.getDouble(key);
            value = switch (type) {
                case "ROUND" -> Math.round(value);
                case "CEILING" -> Math.ceil(value);
                case "FLOOR" -> Math.floor(value);
                default -> value;
            };

            data.put(key, value);
        }
        if (settings.getBool(SAVE, false))
            Fabled.getData((OfflinePlayer) caster).setPersistentData(key, data.getRaw(key));
        return true;
    }
}
