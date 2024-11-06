package studio.magemonkey.fabled.dynamic.trigger;

import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.Settings;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityAirChangeEvent;

public class AirTrigger implements Trigger<EntityAirChangeEvent> {

    @Override
    public String getKey() {
        return "AIR";
    }

    @Override
    public Class<EntityAirChangeEvent> getEvent() {return EntityAirChangeEvent.class;}

    @Override
    public boolean shouldTrigger(EntityAirChangeEvent event, int level, Settings settings) {
        final String type = settings.getString("type", "decreasing");
        final LivingEntity triggerEntity = (LivingEntity) event.getEntity();
        // This only allows it to trigger whenever a bubble comes back / goes away.
        return (triggerEntity.isInWater() && type.equalsIgnoreCase("decreasing")) || (!triggerEntity.isInWater() &&  type.equalsIgnoreCase("increasing"));
    }

    @Override
    public void setValues(EntityAirChangeEvent event, final CastData data) {

    }

    @Override
    public LivingEntity getCaster(EntityAirChangeEvent event) {
        return (LivingEntity) event.getEntity();
    }

    @Override
    public LivingEntity getTarget(EntityAirChangeEvent event, Settings settings) {
        return (LivingEntity) event.getEntity();
    }

}