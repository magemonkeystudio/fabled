package com.sucy.skill.dynamic.trigger;

import com.sucy.skill.api.CastData;
import com.sucy.skill.api.Settings;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Map;

public abstract class ClickTrigger implements Trigger<PlayerInteractEvent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract String getKey();

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<PlayerInteractEvent> getEvent() {
        return PlayerInteractEvent.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract boolean shouldTrigger(PlayerInteractEvent event, int level, Settings settings);

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValues(PlayerInteractEvent event, CastData data) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(PlayerInteractEvent event) {
        return event.getPlayer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(PlayerInteractEvent event, Settings settings) {
        return event.getPlayer();
    }

}
