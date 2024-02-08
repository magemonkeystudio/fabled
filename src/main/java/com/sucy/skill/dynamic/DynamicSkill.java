/**
 * SkillAPI
 * com.sucy.skill.dynamic.DynamicSkill
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

import com.google.common.collect.ImmutableList;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.CastData;
import com.sucy.skill.api.event.DynamicTriggerEvent;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.skills.PassiveSkill;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.api.skills.SkillShot;
import com.sucy.skill.dynamic.trigger.TriggerComponent;
import com.sucy.skill.log.Logger;
import mc.promcteam.engine.mccore.config.parse.DataSection;
import mc.promcteam.engine.mccore.util.TextFormatter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sucy.skill.dynamic.ComponentRegistry.getTrigger;

/**
 * A skill implementation for the Dynamic system
 */
public class DynamicSkill extends Skill implements SkillShot, PassiveSkill, Listener {
    private static final Map<Integer, CastData>       castData           = new HashMap<>();
    private final        List<TriggerHandler>         triggers           = new ArrayList<>();
    private final        Map<String, EffectComponent> attribKeys         = new HashMap<>();
    private final        Map<Integer, Integer>        active             = new HashMap<>();
    private final        List<Integer>                forced             = new ArrayList<>();
    private              List<TriggerComponent>       castTriggers       = new ArrayList<>();
    private              List<TriggerComponent>       initializeTriggers = new ArrayList<>();
    private              List<TriggerComponent>       cleanupTriggers    = new ArrayList<>();

    private boolean cancel     = false;
    private double  multiplier = 1;
    private double  bonus      = 0;

    /**
     * Initializes a new dynamic skill
     *
     * @param name name of the skill
     */
    public DynamicSkill(final String name) {
        super(name, "Dynamic", Material.JACK_O_LANTERN, 1);
    }

    /**
     * Retrieves the cast data for the caster
     *
     * @param caster caster to get the data for
     * @return cast data for the caster
     */
    public static CastData getCastData(final LivingEntity caster) {
        if (caster == null) {
            return null;
        }
        CastData entCastData = castData.get(caster.getEntityId());
        if (entCastData == null) {
            entCastData = new CastData(caster);
            castData.put(caster.getEntityId(), entCastData);
        }
        return entCastData;
    }

    /**
     * Clears any stored cast data for the entity
     *
     * @param entity entity to clear cast data for
     */
    public static void clearCastData(final LivingEntity entity) {
        castData.remove(entity.getEntityId());
    }

    /**
     * Checks whether the dynamic skill can be cast
     *
     * @return true if can cast, false otherwise
     */
    public boolean canCast() {
        return castTriggers != null && !castTriggers.isEmpty();
    }

    /**
     * Checks whether the caster's passives are currently active
     *
     * @param caster caster to check for
     * @return true if active, false otherwise
     */
    public boolean isActive(final LivingEntity caster) {
        return active.containsKey(caster.getEntityId());
    }

    /**
     * Retrieves the active level of the caster for the skill
     *
     * @param caster caster of the skill
     * @return active level of the skill
     */
    public int getActiveLevel(final LivingEntity caster) {
        return active.containsKey(caster.getEntityId()) ? active.get(caster.getEntityId()) : 0;
    }

    /**
     * Sets an attribute key for obtaining attributes used
     * in the skill indicator.
     *
     * @param key       key string
     * @param component component to grab attributes from
     */
    void setAttribKey(final String key, final EffectComponent component) {
        attribKeys.put(key, component);
    }

    /**
     * Cancels the event causing a trigger to go off
     */
    public void cancelTrigger() {
        cancel = true;
    }

    void applyCancelled(final Cancellable event) {
        if (checkCancelled()) {
            event.setCancelled(true);
        }
    }

    public boolean checkCancelled() {
        final boolean result = cancel;
        cancel = false;
        return result;
    }

    public void setImmediateBuff(final double value, final boolean flat) {
        if (flat) {
            this.bonus = value;
        } else {
            this.multiplier = value;
        }
    }

    public double applyImmediateBuff(final double damage) {
        final double result = damage * multiplier + bonus;
        multiplier = 1;
        bonus = 0;
        return result;
    }

    /**
     * Registers needed events for the skill, ignoring any unused events for efficiency
     *
     * @param plugin plugin reference
     */
    public void registerEvents(final SkillAPI plugin) {
        for (final TriggerHandler triggerHandler : triggers) {
            triggerHandler.register(plugin);
        }
    }

    /**
     * Updates the skill effects
     *
     * @param user      user to refresh the effect for
     * @param prevLevel previous skill level
     * @param newLevel  new skill level
     */
    @Override
    public void update(final LivingEntity user, final int prevLevel, final int newLevel) {
        active.put(user.getEntityId(), newLevel);
        for (final TriggerHandler triggerHandler : triggers) {
            triggerHandler.init(user, newLevel);
        }
    }

    /**
     * Initializes any corresponding effects when the skill is unlocked/enabled
     *
     * @param user  user to initialize the effects for
     * @param level skill level
     */
    @Override
    public void initialize(final LivingEntity user, final int level) {
        for (TriggerComponent initializeTrigger : initializeTriggers) {
            trigger(user, user, level, initializeTrigger);
        }
        Bukkit.getPluginManager().callEvent(new DynamicTriggerEvent(user, this, null, "initialize"));
        active.put(user.getEntityId(), level);
        for (final TriggerHandler triggerHandler : triggers) {
            triggerHandler.init(user, level);
        }
    }

    /**
     * Removes active users from the map
     *
     * @param user  user to stop the effects for
     * @param level skill level
     */
    @Override
    public void stopEffects(final LivingEntity user, final int level) {
        active.remove(user.getEntityId());
        if (forced.contains(user.getEntityId())) forced.remove(Integer.valueOf(user.getEntityId()));
        for (final TriggerHandler triggerHandler : triggers) {
            triggerHandler.cleanup(user);
        }
        cleanup(user, castTriggers);
        cleanup(user, initializeTriggers);

        for (TriggerComponent cleanupTrigger : cleanupTriggers) {
            trigger(user, user, 1, cleanupTrigger);
        }
        Bukkit.getPluginManager().callEvent(new DynamicTriggerEvent(user, this, null, "cleanup"));
    }

    private void cleanup(final LivingEntity user, final List<TriggerComponent> components) {
        if (components == null) return;

        components.forEach(component -> cleanup(user, component));
    }

    private void cleanup(final LivingEntity user, final TriggerComponent component) {
        if (component != null) component.cleanUp(user);
    }

    public boolean isForced(LivingEntity user) {
        return forced.contains(user.getEntityId());
    }

    /**
     * Casts the skill if applicable
     *
     * @param user  user of the skill
     * @param level skill level
     * @param force
     * @return true if cast successfully, false if conditions weren't met or no effects are using the cast trigger
     */
    @Override
    public boolean cast(final LivingEntity user, final int level, boolean force) {
        if (!force && !SkillAPI.getSettings().isWorldEnabled(user.getWorld())) return false;
        if (force && !isForced(user)) forced.add(user.getEntityId());

        boolean cast = false;
        for (TriggerComponent castTrigger : castTriggers) {
            boolean result = trigger(user, user, level, castTrigger, force);
            cast = cast || result;
        }

        if (!cast) return false;

        Bukkit.getPluginManager().callEvent(new DynamicTriggerEvent(user, this, null, "cast"));
        return true;
    }

    @Override
    public boolean cast(LivingEntity user, int level) {
        return cast(user, level, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void playPreview(PlayerData playerData, int level) {
        if (castTriggers == null || castTriggers.isEmpty())
            return;

        List<Runnable> onPreviewStop = new ArrayList<>();
        for (TriggerComponent castTrigger : castTriggers) {
            castTrigger.playChildrenPreviews(onPreviewStop,
                    playerData.getPlayer(),
                    level,
                    () -> ImmutableList.of(playerData.getPlayer()));
        }
        playerData.setOnPreviewStop(() -> onPreviewStop.forEach(Runnable::run));
    }

    /**
     * Retrieves the formatted name of an attribute which ignores the dynamic
     * path overhead.
     *
     * @param key attribute key
     * @return formatted attribute name
     */
    @Override
    protected String getAttrName(String key) {
        if (key.contains(".")) {
            return TextFormatter.format(key.substring(key.lastIndexOf('.') + 1));
        } else {
            return super.getAttrName(key);
        }
    }

    /**
     * Retrieves an attribute while supporting dynamic skill attribute paths.
     * Paths are set up by the "icon-key" setting in components. An invalid
     * path will instead return a value of 0. If a path is not provided, this
     * returns a normal attribute on the skill.
     *
     * @param caster owner of the skill
     * @param key    attribute key
     * @param level  skill level
     * @return attribute value or 0 if invalid dynamic path
     */
    @Override
    protected Object getAttr(final LivingEntity caster, final String key, final int level) {
        // Dynamic attribute paths use periods
        if (key.contains(".")) {
            final String[] path = key.split("\\.");
            final String   attr = path[1].toLowerCase();
            if (attribKeys.containsKey(path[0]) && attribKeys.get(path[0]).settings.has(attr)) {
                return format(attribKeys.get(path[0]).parseValues(caster, attr, level, 0));
            } else {
                return 0;
            }
        }

        // Otherwise get the attribute normally
        else {
            return super.getAttr(caster, key, level);
        }
    }

    private boolean trigger(
            final LivingEntity user,
            final LivingEntity target,
            final int level,
            final TriggerComponent component) {
        return trigger(user, target, level, component, false);
    }

    private boolean trigger(
            final LivingEntity user,
            final LivingEntity target,
            final int level,
            final TriggerComponent component, boolean force) {
        return component != null && component.trigger(user, target, level, force);
    }

    /**
     * Loads dynamic components in addition to the normal values
     *
     * @param config config data to load from
     */
    @Override
    public void load(final DataSection config) {
        super.load(config);

        final DataSection triggers = config.getSection("components");
        if (triggers == null) {
            return;
        }

        castTriggers.clear();
        initializeTriggers.clear();
        cleanupTriggers.clear();

        for (final String key : triggers.keys()) {
            final String modified = key.replaceAll("-.+", "");
            try {
                final DataSection settings = triggers.getSection(key);
                if (modified.equalsIgnoreCase("CAST")) {
                    castTriggers.add(loadComponent(settings));
                } else if (modified.equalsIgnoreCase("INITIALIZE")) {
                    initializeTriggers.add(loadComponent(settings));
                } else if (modified.equalsIgnoreCase("CLEANUP")) {
                    cleanupTriggers.add(loadComponent(settings));
                } else {
                    this.triggers.add(new TriggerHandler(this, key, getTrigger(modified), loadComponent(settings)));
                }
            } catch (final Exception ex) {
                // Invalid trigger
                ex.printStackTrace();
                Logger.invalid("Invalid trigger for the skill \"" + getName() + "\" - \"" + key + "\"");
            }
        }
    }

    private TriggerComponent loadComponent(final DataSection data) {
        final TriggerComponent component = new TriggerComponent();
        component.load(this, data);
        return component;
    }

    /**
     * Saves the skill back to the config, appending component data
     * on top of the normal skill data
     *
     * @param config config to save to
     */
    @Override
    public void save(final DataSection config) {
        super.save(config);
        final DataSection triggers = config.createSection("components");
        for (final TriggerHandler triggerHandler : this.triggers) {
            triggerHandler.getComponent()
                    .save(triggers.createSection(TextFormatter.format(triggerHandler.getKey())));
        }
        for (int i = 0; i < castTriggers.size(); i++) {
            TriggerComponent castTrigger = castTriggers.get(i);
            save(triggers, castTrigger, "Cast-" + i);
        }
        for (int i = 0; i < initializeTriggers.size(); i++) {
            TriggerComponent initializeTrigger = initializeTriggers.get(i);
            save(triggers, initializeTrigger, "Initialize-" + i);
        }
        for (int i = 0; i < cleanupTriggers.size(); i++) {
            TriggerComponent cleanupTrigger = cleanupTriggers.get(i);
            save(triggers, cleanupTrigger, "Cleanup-" + i);
        }
    }

    private void save(final DataSection triggers, final TriggerComponent component, final String key) {
        if (component != null) component.save(triggers.createSection(TextFormatter.format(key)));
    }
}
