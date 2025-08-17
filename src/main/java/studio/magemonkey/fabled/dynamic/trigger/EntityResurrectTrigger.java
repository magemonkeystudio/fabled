
package studio.magemonkey.fabled.dynamic.trigger;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityResurrectEvent;
import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.Settings;

/**
 * Fabled © 2024
 * studio.magemonkey.fabled.dynamic.trigger.EntityResurrectTrigger
 *
 * Triggers when an entity is resurrected (e.g., by a totem of undying)
 */
public class EntityResurrectTrigger implements Trigger<EntityResurrectEvent> {
    @Override
    public String getKey() {
        return "ENTITY_RESURRECT";
    }

    @Override
    public Class<EntityResurrectEvent> getEvent() {
        return EntityResurrectEvent.class;
    }

    @Override
    public boolean shouldTrigger(final EntityResurrectEvent event, final int level, final Settings settings) {
        return true;
    }

    @Override
    public void setValues(final EntityResurrectEvent event, final CastData data) {
        // No extra values by default
    }

    @Override
    public LivingEntity getCaster(final EntityResurrectEvent event) {
        return event.getEntity();
    }

    @Override
    public LivingEntity getTarget(final EntityResurrectEvent event, final Settings settings) {
        return event.getEntity();
    }
}
