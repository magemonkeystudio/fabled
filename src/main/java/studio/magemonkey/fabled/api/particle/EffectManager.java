/**
 * Fabled
 * studio.magemonkey.fabled.api.particle.EffectManager
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
package studio.magemonkey.fabled.api.particle;

import org.bukkit.entity.LivingEntity;
import studio.magemonkey.codex.mccore.config.CommentedConfig;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import studio.magemonkey.codex.util.FileUT;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.particle.direction.XZHandler;
import studio.magemonkey.fabled.api.particle.target.EffectTarget;
import studio.magemonkey.fabled.api.particle.target.EntityTarget;
import studio.magemonkey.fabled.task.EffectTask;
import studio.magemonkey.fabled.thread.MainThread;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles the management of particle effects and related components
 */
public class EffectManager {
    private static Map<EffectTarget, EffectData> instances = new ConcurrentHashMap<>();
    private static Map<String, IParticleEffect>  effects   = new HashMap<>();
    private static Map<String, PolarSettings>    formulas  = new HashMap<>();

    /**
     * Initializes the utility, loading formulas from the config file
     */
    public static void init() {
        CommentedConfig config = Fabled.getConfig("effects");
        config.saveDefaultConfig();
        DataSection data = config.getConfig();
        for (String key : data.keys()) {
            formulas.put(key, new PolarSettings(data.getSection(key)));
            if (key.equals("one-circle")) {
                formulas.get(key).getPoints(XZHandler.instance);
            }
        }

        try {
            File imagesDir = new File(Fabled.inst().getDataFolder(), "images");
            if (!imagesDir.exists() && imagesDir.mkdirs()) {
                FileUT.copy(Objects.requireNonNull(Fabled.inst().getResource("images/default.png")),
                        new File(imagesDir, "default.png"));
            }
        } catch (Exception e) {
            Fabled.inst().getLogger().warning("Failed to create images directory: " + e.getMessage());
        }

        MainThread.register(new EffectTask());
    }

    public static void cleanUp() {
        formulas.clear();
        effects.clear();
        instances.clear();
    }

    /**
     * Registers a new particle effect, replacing any conflicting
     * effects already registered under the key
     *
     * @param effect effect to register
     */
    public static void register(IParticleEffect effect) {
        if (effect != null) {
            effects.put(effect.getName(), effect);
        }
    }

    /**
     * Registers a new formula for effects
     *
     * @param key     key to register under
     * @param formula formula to register
     */
    public static void register(String key, PolarSettings formula) {
        if (formula != null) {
            formulas.put(key, formula);
        }
    }

    /**
     * Gets a formula by key
     *
     * @param key formula key
     * @return formula
     */
    public static PolarSettings getFormula(String key) {
        return formulas.get(key);
    }

    /**
     * Fetches an effect by key
     *
     * @param name name of the effect
     * @return particle effect
     */
    public static IParticleEffect getEffect(String name) {
        return effects.get(name);
    }

    /**
     * Clears effects for a given target
     *
     * @param target target to clear for
     */
    public static void clear(EffectTarget target) {
        instances.remove(target);
    }

    /**
     * Clears effects for a given target
     *
     * @param target target to clear for
     */
    public static void clear(LivingEntity target) {
        instances.entrySet()
                .removeIf(entry -> entry.getKey() instanceof EntityTarget
                        && ((EntityTarget) entry.getKey()).getEntity() == target);
    }

    /**
     * Gets the effect data for the given target
     *
     * @param target target to get the data for
     * @return effect data for the target or null if it doesn't exist
     */
    public static EffectData getEffectData(EffectTarget target) {
        return instances.get(target);
    }

    /**
     * Fetches an active effect for a given target
     *
     * @param target target to get the effect for
     * @param key    effect key
     * @return active effect or null if not found
     */
    public static EffectInstance getEffect(EffectTarget target, String key) {
        if (!instances.containsKey(target)) {
            return null;
        }

        return instances.get(target).getEffect(key);
    }

    /**
     * Starts running an effect for a target. If the effect is already
     * running for the target, the running effect will be stopped before
     * the new one is started.
     *
     * @param effect effect to run
     * @param target target to run for
     * @param ticks  ticks to run for
     * @param level  effect level
     */
    public static void runEffect(IParticleEffect effect, EffectTarget target, int ticks, int level) {
        if (!instances.containsKey(target)) {
            instances.put(target, new EffectData(target));
        }
        instances.get(target).runEffect(effect, ticks, level);
    }

    /**
     * Ticks all active effects
     */
    public static void tick() {
        Iterator<EffectData> iterator = instances.values().iterator();
        while (iterator.hasNext()) {
            EffectData data = iterator.next();
            if (data.isValid()) {
                data.tick();
            } else {
                iterator.remove();
            }
        }
    }
}
