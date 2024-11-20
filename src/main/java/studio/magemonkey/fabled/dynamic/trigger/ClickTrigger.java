package studio.magemonkey.fabled.dynamic.trigger;

import org.bukkit.entity.LivingEntity;
import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.Settings;
import studio.magemonkey.fabled.api.event.KeyPressEvent;

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
