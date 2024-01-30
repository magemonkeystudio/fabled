/**
 * SkillAPI
 * com.sucy.skill.dynamic.mechanic.value.ValueRoundMechanic
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 ProMCTeam
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
package com.sucy.skill.dynamic.mechanic.value;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.CastData;
import com.sucy.skill.dynamic.DynamicSkill;
import com.sucy.skill.dynamic.mechanic.MechanicComponent;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;

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
        if (targets.isEmpty() || !settings.has(KEY)) {
            return false;
        }

        String   key  = settings.getString(KEY);
        String   type = settings.getString(TYPE).toUpperCase(Locale.US);
        CastData data = DynamicSkill.getCastData(caster);
        if (data.contains(key)) {
            double value = data.getDouble(key);
            switch (type) {
                case "ROUND":
                    value = Math.round(value);
                    break;
                case "CEILING":
                    value = Math.ceil(value);
                    break;
                case "FLOOR":
                    value = Math.floor(value);
                    break;
            }

            data.put(key, value);
        }
        if (settings.getBool(SAVE, false))
            SkillAPI.getPlayerData((OfflinePlayer) caster).setPersistentData(key, data.getRaw(key));
        return true;
    }
}
