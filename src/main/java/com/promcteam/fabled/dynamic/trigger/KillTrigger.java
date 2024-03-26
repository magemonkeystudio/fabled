package com.promcteam.fabled.dynamic.trigger;

import com.promcteam.fabled.api.CastData;
import com.promcteam.fabled.api.Settings;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 * Fabled © 2024
 * com.promcteam.fabled.dynamic.trigger.BlockBreakTrigger
 */
public class KillTrigger implements Trigger<EntityDeathEvent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "KILL";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<EntityDeathEvent> getEvent() {
        return EntityDeathEvent.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldTrigger(final EntityDeathEvent event, final int level, final Settings settings) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValues(final EntityDeathEvent event, final CastData data) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(final EntityDeathEvent event) {
        return event.getEntity().getKiller();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(final EntityDeathEvent event, final Settings settings) {
        boolean targetCaster = settings.getBool("target", true);
        return targetCaster ? event.getEntity().getKiller() : event.getEntity();
    }
}
