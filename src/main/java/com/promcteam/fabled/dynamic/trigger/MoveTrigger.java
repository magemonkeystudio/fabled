package com.promcteam.fabled.dynamic.trigger;

import com.promcteam.fabled.api.CastData;
import com.promcteam.fabled.api.Settings;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Fabled © 2023
 * com.promcteam.fabled.dynamic.trigger.BlockBreakTrigger
 */
public class MoveTrigger implements Trigger<PlayerMoveEvent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "MOVE";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<PlayerMoveEvent> getEvent() {
        return PlayerMoveEvent.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldTrigger(final PlayerMoveEvent event, final int level, final Settings settings) {
        return event.getFrom().getWorld() == event.getTo().getWorld();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValues(final PlayerMoveEvent event, final CastData data) {
        final double distance = event.getTo().distance(event.getFrom());
        data.put("api-distance", distance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(final PlayerMoveEvent event) {
        return event.getPlayer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(final PlayerMoveEvent event, final Settings settings) {
        return event.getPlayer();
    }
}
