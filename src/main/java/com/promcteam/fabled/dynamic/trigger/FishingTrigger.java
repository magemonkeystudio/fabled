package com.promcteam.fabled.dynamic.trigger;

import com.promcteam.fabled.api.CastData;
import com.promcteam.fabled.api.Settings;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerFishEvent;

public abstract class FishingTrigger implements Trigger<PlayerFishEvent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<PlayerFishEvent> getEvent() {
        return PlayerFishEvent.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValues(final PlayerFishEvent event, final CastData data) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(final PlayerFishEvent event) {
        return event.getPlayer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(PlayerFishEvent event, Settings settings) {
        return event.getPlayer();
    }

}
