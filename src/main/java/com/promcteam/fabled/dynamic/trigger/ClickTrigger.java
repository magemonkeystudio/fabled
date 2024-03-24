package com.promcteam.fabled.dynamic.trigger;

import com.promcteam.fabled.api.CastData;
import com.promcteam.fabled.api.Settings;
import com.promcteam.fabled.api.event.KeyPressEvent;
import org.bukkit.entity.LivingEntity;

public abstract class ClickTrigger implements Trigger<KeyPressEvent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract String getKey();

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<KeyPressEvent> getEvent() {
        return KeyPressEvent.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract boolean shouldTrigger(KeyPressEvent event, int level, Settings settings);

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValues(KeyPressEvent event, CastData data) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(KeyPressEvent event) {
        return event.getPlayer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(KeyPressEvent event, Settings settings) {
        return event.getPlayer();
    }

}
