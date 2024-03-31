/**
 * Fabled
 * com.promcteam.fabled.api.particle.EffectData
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 ProMCTeam
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
package com.promcteam.fabled.api.particle;

import com.promcteam.fabled.api.particle.target.EffectTarget;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

/**
 * A collection of effects played on a target
 */
public class EffectData {
    private final Map<String, EffectInstance> effects = new HashMap<>();

    private final EffectTarget target;

    /**
     * @param target target of each effect
     */
    public EffectData(EffectTarget target) {
        this.target = target;
    }

    /**
     * Checks whether an effect is still running
     *
     * @param key effect key
     * @return true if running
     */
    public boolean isEffectActive(String key) {
        return this.effects.containsKey(key);
    }

    /**
     * Fetches an active effect by key
     *
     * @param key effect key
     * @return active effect or null if not found
     */
    public EffectInstance getEffect(String key) {
        return effects.get(key);
    }

    /**
     * @return true should keep the data, false otherwise
     */
    public boolean isValid() {
        return !effects.isEmpty() && target.isValid();
    }

    /**
     * Starts running an effect for the target. If the effect is already
     * running for the target, the running effect will be stopped before
     * the new one is started.
     *
     * @param effect effect to run
     * @param ticks  ticks to run for
     * @param level  effect level
     */
    public void runEffect(IParticleEffect effect, int ticks, int level) {
        EffectInstance instance = new EffectInstance(effect, target, level);
        instance.extend(ticks);
        effects.put(effect.getName(), instance);
    }

    /**
     * Cancels an effect via its associated key
     *
     * @param key key of the effect to cancel
     */
    public void cancel(String key) {
        effects.remove(key);
    }

    /**
     * Ticks each effect for the target
     */
    public void tick() {
        Iterator<EffectInstance> iterator = effects.values().iterator();
        while (iterator.hasNext()) {
            EffectInstance effect = iterator.next();
            if (effect.isValid())
                effect.tick();
            else
                iterator.remove();
        }
    }
}
