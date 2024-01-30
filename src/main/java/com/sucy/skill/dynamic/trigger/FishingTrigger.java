package com.sucy.skill.dynamic.trigger;

import com.sucy.skill.api.CastData;
import com.sucy.skill.api.Settings;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.Map;

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
