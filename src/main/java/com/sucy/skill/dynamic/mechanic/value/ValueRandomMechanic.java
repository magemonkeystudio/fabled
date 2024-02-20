/**
 * SkillAPI
 * com.sucy.skill.dynamic.mechanic.value.ValueSetMechanic
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2014 Steven Sucy
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
import java.util.Map;

/**
 * Adds to a cast data value
 */
public class ValueRandomMechanic extends MechanicComponent {
    private static final String KEY  = "key";
    private static final String TYPE = "type";
    private static final String MIN  = "min";
    private static final String MAX  = "max";
    private static final String INT  = "integer";
    private static final String SAVE   = "save";

    @Override
    public String getKey() {
        return "value random";
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

        String  key        = settings.getString(KEY);
        boolean triangular = settings.getString(TYPE).equalsIgnoreCase("triangular");

        CastData data = DynamicSkill.getCastData(caster);
        if (settings.getBool(INT, false)) {
            int  min        = (int) Math.ceil(parseValues(caster, MIN, level, 1));
            int  max        = (int) Math.floor(parseValues(caster, MAX, level, 1));
            if (triangular) {
                int middle = SkillAPI.RANDOM.nextInt(max-min+1)+min+SkillAPI.RANDOM.nextInt(max-min+1)+min;
                middle = middle/2 + (middle%2 == 1 ? (Math.random() < 0.5 ? 1 : 0) : 0);
                data.put(key, middle);
            } else data.put(key, SkillAPI.RANDOM.nextInt(max-min+1)+min);
        } else {
            double  min        = parseValues(caster, MIN, level, 1);
            double  max        = parseValues(caster, MAX, level, 1);
            double rand = triangular ? 0.5 * (Math.random() + Math.random()) : Math.random();
            data.put(key, rand * (max - min) + min);
        }

        if (settings.getBool(SAVE, false))
            SkillAPI.getPlayerData((OfflinePlayer) caster).setPersistentData(key,data.getRaw(key));
        return true;
    }
}
