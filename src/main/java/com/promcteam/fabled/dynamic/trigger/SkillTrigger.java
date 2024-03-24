package com.promcteam.fabled.dynamic.trigger;

import com.promcteam.fabled.api.Settings;
import com.promcteam.fabled.api.event.SkillDamageEvent;
import com.promcteam.fabled.dynamic.DynamicSkill;

import java.util.List;

/**
 * Fabled © 2023
 * com.promcteam.fabled.dynamic.trigger.BlockBreakTrigger
 */
public abstract class SkillTrigger implements Trigger<SkillDamageEvent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<SkillDamageEvent> getEvent() {
        return SkillDamageEvent.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldTrigger(final SkillDamageEvent event, final int level, final Settings settings) {
        final double       min   = settings.getDouble("dmg-min");
        final double       max   = settings.getDouble("dmg-max");
        final List<String> types = settings.getStringList("category");
        final boolean      empty = types.isEmpty() || types.get(0).isEmpty();
        return event.getDamage() >= min && event.getDamage() <= max &&
                (empty || types.contains(event.getClassification()));
    }

    /**
     * Handles applying other effects after the skill resolves
     *
     * @param event event details
     * @param skill skill to resolve
     */
    @Override
    public void postProcess(final SkillDamageEvent event, final DynamicSkill skill) {
        final double damage = skill.applyImmediateBuff(event.getDamage());
        event.setDamage(damage);
    }

    boolean isUsingTarget(final Settings settings) {
        return settings.getString("target", "true").equalsIgnoreCase("false");
    }
}
