package studio.magemonkey.fabled.dynamic.trigger;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.Settings;

/**
 * © 2026 VoidEdge
 * studio.magemonkey.fabled.dynamic.trigger.GlideTrigger
 */
public class GlideTrigger implements Trigger<EntityToggleGlideEvent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "GLIDE";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<EntityToggleGlideEvent> getEvent() {
        return EntityToggleGlideEvent.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldTrigger(final EntityToggleGlideEvent event, final int level, final Settings settings) {
        final String type = settings.getString("type", "start gliding");
        return type.equalsIgnoreCase("both") || event.isGliding() != type.equalsIgnoreCase("stop gliding");
    }

    @Override
    public void setValues(EntityToggleGlideEvent event, final CastData data) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(final EntityToggleGlideEvent event) {
        return (LivingEntity) event.getEntity();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(final EntityToggleGlideEvent event, final Settings settings) {
        return (LivingEntity) event.getEntity();
    }
}
