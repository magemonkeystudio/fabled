/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.mechanic.value.ValueAttributeMechanic
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
import org.bukkit.entity.Player;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.dynamic.DynamicSkill;
import studio.magemonkey.fabled.dynamic.mechanic.MechanicComponent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Adds to a cast data value
 */
public class ValueAttributeMechanic extends MechanicComponent {
    private static final String KEY  = "key";
    private static final String ATTR = "attribute";
    private static final String SAVE = "save";

    @Override
    public String getKey() {
        return "value attribute";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        if (!settings.has(KEY) || !settings.has(ATTR) || !(targets.get(0) instanceof Player)) {
            return false;
        }

        String key = getAttributeKey();
        if (key == null) return false;

        String   attr = settings.getString(ATTR);
        CastData data = DynamicSkill.getCastData(caster);
        data.put(key, (double) Fabled.getData((Player) targets.get(0)).getAttribute(attr));
        if (settings.getBool(SAVE, false))
            Fabled.getData((OfflinePlayer) caster).setPersistentData(key, data.getRaw(key));
        return true;
    }

    @Nullable String getAttributeKey() {
        List<String> keys;
        if (!settings.getStringList(ATTR).isEmpty()) {
            keys = settings.getStringList(ATTR)
                    .stream()
                    .filter(key -> Fabled.getAttributeManager().getAttribute(key) != null)
                    .collect(Collectors.toList());
        } else {
            // Attempt to read it as a string, optionally comma separated
            String data = settings.getString(ATTR);
            if (data == null || data.isBlank() || data.equals("[]")) {
                keys = new ArrayList<>();
            } else {
                keys = List.of(settings.getString(ATTR).split(","));
            }
        }
        String key = null;
        if (!keys.isEmpty()) key = keys.get(0);
        return key;
    }
}
