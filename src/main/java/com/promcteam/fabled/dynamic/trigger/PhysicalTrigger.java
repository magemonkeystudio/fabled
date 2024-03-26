package com.promcteam.fabled.dynamic.trigger;

import com.promcteam.fabled.api.Settings;
import com.promcteam.fabled.api.event.PhysicalDamageEvent;
import com.promcteam.fabled.dynamic.DynamicSkill;

/**
 * Fabled © 2024
 * com.promcteam.fabled.dynamic.trigger.BlockBreakTrigger
 */
public abstract class PhysicalTrigger implements Trigger<PhysicalDamageEvent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<PhysicalDamageEvent> getEvent() {
        return PhysicalDamageEvent.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldTrigger(final PhysicalDamageEvent event, final int level, final Settings settings) {
        final String  type       = settings.getString("type", "both");
        final double  min        = settings.getDouble("dmg-min");
        final double  max        = settings.getDouble("dmg-max");
        final boolean projectile = event.isProjectile();
        return event.getDamage() >= min && event.getDamage() <= max &&
                (type.equalsIgnoreCase("both") || type.equalsIgnoreCase("projectile") == projectile);
    }

    /**
     * Handles applying other effects after the skill resolves
     *
     * @param event event details
     * @param skill skill to resolve
     */
    @Override
    public void postProcess(final PhysicalDamageEvent event, final DynamicSkill skill) {
        final double damage = skill.applyImmediateBuff(event.getDamage());
        event.setDamage(damage);
    }

    boolean isUsingTarget(final Settings settings) {
        return settings.getString("target", "true").equalsIgnoreCase("false");
    }
}
