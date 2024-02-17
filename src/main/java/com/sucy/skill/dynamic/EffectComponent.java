/**
 * SkillAPI
 * com.sucy.skill.dynamic.EffectComponent
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
package com.sucy.skill.dynamic;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.CastData;
import com.sucy.skill.api.Settings;
import com.sucy.skill.api.particle.ParticleHelper;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import com.sucy.skill.log.Logger;
import mc.promcteam.engine.mccore.config.parse.DataSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * A component for dynamic skills which takes care of one effect
 */
public abstract class EffectComponent {
    private static final String                     ICON_KEY         = "icon-key";
    private static final String                     COUNTS_KEY       = "counts";
    private static final String                     TYPE             = "type";
    private static final String                     PREVIEW          = "preview";
    private static       boolean                    passed;
    /**
     * Child components
     */
    public final         ArrayList<EffectComponent> children         = new ArrayList<>();
    /**
     * The settings for the component
     */
    protected final      Settings                   settings         = new Settings();
    /**
     * Whether the component should display preview effects
     */
    private              boolean                    isPreviewEnabled = false;
    /**
     * The preview settings for the component
     */
    protected final      Settings                   preview          = new Settings();
    /**
     * Parent class of the component
     */
    protected            DynamicSkill               skill;
    private              String                     instanceKey;

    private static String filterSpecialChars(String string) {
        int           i       = 0;
        int           j       = string.indexOf('&');
        StringBuilder builder = new StringBuilder();
        while (j >= 0) {
            String key = string.substring(j + 1, j + 3);
            switch (key) {
                case "rc":
                    builder.append(string, i, j);
                    builder.append('}');
                    i = j + 3;
                    break;
                case "lc":
                    builder.append(string, i, j);
                    builder.append('{');
                    i = j + 3;
                    break;
                case "sq":
                    builder.append(string, i, j);
                    builder.append('\'');
                    i = j + 3;
                    break;
                default:
                    i++;
                    break;
            }
            j = string.indexOf('&', i);
        }
        builder.append(string.substring(i));
        return builder.toString();
    }

    /**
     * Retrieves the config key for the component
     *
     * @return config key of the component
     */
    public String getInstanceKey() {
        return instanceKey;
    }

    public abstract String getKey();

    /**
     * Retrieves the type of the component
     *
     * @return component type
     */
    public abstract ComponentType getType();

    /**
     * Retrieves the settings of the dynamic component
     *
     * @return settings of the dynamic component
     */
    public Settings getSettings() {
        return settings;
    }

    /**
     * @return true if the component or its children should play their preview effects, or false otherwise
     */
    public boolean isPreviewEnabled() {return isPreviewEnabled;}

    /**
     * Retrieves an attribute value while applying attribute
     * data if enabled and a player is using the skill
     *
     * @param caster   caster of the skill
     * @param key      key of the value to grab
     * @param level    level of the skill
     * @param fallback default value for the attribute
     * @return the value with attribute modifications if applicable
     */
    protected double parseValues(LivingEntity caster, String key, int level, double fallback) {
        double base  = getNum(caster, key + "-base", fallback);
        double scale = getNum(caster, key + "-scale", 0);
        double value = base + (level - 1) * scale;

        // Apply global modifiers
        if (SkillAPI.getSettings().isAttributesEnabled() && caster instanceof Player) {
            PlayerData data = SkillAPI.getPlayerData((Player) caster);
            value = data.scaleDynamic(this, key, value);
        }

        return value;
    }

    /**
     * Retrieves a numerical value while using non-numerical values as
     * keys for the cast data. If the value doesn't exist, this will
     * return the default value. If it is a key that doesn't have an
     * attached value, it will return 0. Otherwise, it will return
     * the appropriate value.
     *
     * @param caster   the caster of the skill
     * @param key      key of the value
     * @param fallback fallback value in case the settings don't have it
     * @return the settings value or, if not a number, the cast data value
     */
    protected double getNum(LivingEntity caster, String key, double fallback) {
        String val = settings.getString(key);
        if (val == null) {
            return fallback;
        }

        try {
            return Double.parseDouble(val);
        } catch (Exception ex) { /* Not a number */ }

        final CastData castData = DynamicSkill.getCastData(caster);
        if (castData.contains(val)) {
            final String mapVal = castData.get(val);
            try {
                return Double.parseDouble(mapVal);
            } catch (Exception ex) { /* Not a number */ }
        }

        try {
            final int    mid = val.indexOf('-', 1);
            final double min = Double.parseDouble(val.substring(0, mid));
            final double max = Double.parseDouble(val.substring(mid + 1));
            return Math.random() * (max - min) + min;
        } catch (Exception ex) { /* Not a range */ }

        return 0;
    }

    /**
     * Checks whether the last component passed or not
     *
     * @return true if passed, false otherwise
     */
    protected boolean lastPassed() {
        return passed;
    }

    /**
     * Executes the children of the component using the given targets
     *
     * @param caster  caster of the skill
     * @param level   level of the skill
     * @param targets targets to execute on
     * @param force
     * @return true if executed, false if conditions not met
     */
    protected boolean executeChildren(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        if (targets.isEmpty()) {
            return false;
        }

        boolean worked = false;
        for (EffectComponent child : children) {
            boolean counts = !child.settings.getString(COUNTS_KEY, "true").equalsIgnoreCase("false");
            passed = child.execute(caster, level, targets, force);
            worked = (passed && counts) || worked;
        }
        return worked;
    }

    public void cleanUp(final LivingEntity caster) {
        doCleanUp(caster);
        children.forEach(child -> child.cleanUp(caster));
    }

    protected void doCleanUp(final LivingEntity caster) {
    }

    /**
     * Gets the skill data for the caster
     *
     * @param caster caster of the skill
     * @return skill data for the caster or null if not found
     */
    protected PlayerSkill getSkillData(LivingEntity caster) {
        if (caster instanceof Player) {
            return SkillAPI.getPlayerData((Player) caster).getSkill(skill.getName());
        } else {
            return null;
        }
    }

    protected String filter(LivingEntity caster, LivingEntity target, String text) {
        CastData data    = DynamicSkill.getCastData(caster);
        return filterSpecialChars(Pattern.compile("\\{((\\{[^{}]+})|[^{}])+}")
            .matcher(text)
            .replaceAll(match->{
                String key = match.group().substring(1,match.group().length()-1);
                if (data.contains(key)) return data.get(key);
                else if (key.equals("player")) return caster.getName();
                else if (key.equals("target")) return target.getName();
                else if (key.equals("targetUUID")) return target.getUniqueId().toString();
                else return "{"+filter(caster, target, key)+"}";
            }));
    }

    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets) {
        return execute(caster, level, targets, false);
    }

    /**
     * Executes the component (to be implemented)
     *
     * @param caster  caster of the skill
     * @param level   level of the skill
     * @param targets targets to execute on
     * @param force   whether skill should be forced
     * @return true if executed, false if conditions not met
     */
    public abstract boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force);

    /**
     * Starts the component's preview effects.
     * Removal of any registered listeners, tasks, entities, or other
     * temporary effects should be included in a {@link Runnable} added
     * to the provided {@link List}.
     *
     * @param onPreviewStop  {@link List} of {@link Runnable}s to add to
     * @param caster         caster reference
     * @param level          the level of the skill to preview for
     * @param targetSupplier targets to preview on
     */
    public void playPreview(List<Runnable> onPreviewStop,
                            Player caster,
                            int level,
                            Supplier<List<LivingEntity>> targetSupplier) {
        if (preview.getBool("per-target")) {
            BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {
                    for (LivingEntity target : targetSupplier.get()) {
                        ParticleHelper.play(target.getLocation(), preview, Set.of(caster), "per-target-",
                                preview.getBool("per-target-" + "hitbox") ? target.getBoundingBox() : null
                        );
                    }
                }
            }.runTaskTimer(SkillAPI.inst(), 0, Math.max(1, preview.getInt("per-target-" + "period", 5)));
            onPreviewStop.add(task::cancel);
        }
        playChildrenPreviews(onPreviewStop, caster, level, targetSupplier);
    }

    /**
     * Starts the preview effects of children components
     * with previews enabled (see {@link #isPreviewEnabled()}),
     * and adds their onPreviewStop {@link Runnable}s to the provided {@link List}.
     *
     * @param onPreviewStop  {@link List} of {@link Runnable}s to add to
     * @param caster         caster reference
     * @param level          the level of the skill to preview for
     * @param targetSupplier targets to preview on
     */
    public void playChildrenPreviews(List<Runnable> onPreviewStop,
                                     Player caster,
                                     int level,
                                     Supplier<List<LivingEntity>> targetSupplier) {
        for (EffectComponent child : children) {
            child.playPreview(onPreviewStop, caster, level, targetSupplier);
        }
    }

    /**
     * Saves the component and its children to the config
     *
     * @param config config to save to
     */
    public void save(DataSection config) {
        config.set(TYPE, getType().name().toLowerCase());
        settings.save(config.createSection("data"));
        preview.save(config.createSection(PREVIEW));
        DataSection children = config.createSection("children");
        for (EffectComponent child : this.children) {
            child.save(children.createSection(child.instanceKey));
        }
    }

    /**
     * Loads component data from the configuration
     *
     * @param skill  owning skill of the component
     * @param config config data to load from
     */
    public void load(DynamicSkill skill, DataSection config) {
        this.skill = skill;
        if (config == null) {
            return;
        }
        settings.load(config.getSection("data"));
        if (settings.has(ICON_KEY)) {
            String key = settings.getString(ICON_KEY);
            if (!key.equals("")) {
                skill.setAttribKey(key, this);
            }
        }

        preview.load(config.getSection(PREVIEW));
        isPreviewEnabled = preview.getBool("enabled", false);

        DataSection children = config.getSection("children");
        if (children != null) {
            for (String key : children.keys()) {
                final String        typeName = children.getSection(key).getString(TYPE, "missing").toUpperCase(Locale.US);
                final ComponentType type     = ComponentType.valueOf(typeName);
                final String        mkey     = key.replaceAll("-.+", "");
                try {
                    final EffectComponent child = ComponentRegistry.getComponent(type, mkey);
                    if (child != null) {
                        child.instanceKey = key;
                        child.load(skill, children.getSection(key));
                        this.children.add(child);
                    } else {
                        Logger.invalid("Invalid " + type + " component: " + mkey);
                    }
                } catch (Exception ex) {
                    // Failed to create the component, just don't add it
                    Logger.bug("Failed to create " + type + " component: " + key);
                }
            }
        }
    }
}
