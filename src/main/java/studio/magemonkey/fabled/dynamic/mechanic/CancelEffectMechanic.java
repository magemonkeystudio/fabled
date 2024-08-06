/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.mechanic.ParticleEffectCancel
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 MageMonkeyStudio
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
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

import studio.magemonkey.fabled.api.particle.EffectData;
import studio.magemonkey.fabled.api.particle.EffectManager;
import studio.magemonkey.fabled.api.particle.target.EntityTarget;
import org.bukkit.entity.LivingEntity;

import java.util.List;

public class CancelEffectMechanic extends MechanicComponent {

    private static final String KEY = "effect-key";

    @Override
    public String getKey() {
        return "cancel effect";
    }

    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        String key = settings.getString(KEY, skill.getName());

        for (LivingEntity target : targets) {
            EffectData effectData = EffectManager.getEffectData(new EntityTarget(target));
            if (effectData != null) {
                effectData.cancel(key);
            }
        }

        return targets.size() > 0;
    }
}
