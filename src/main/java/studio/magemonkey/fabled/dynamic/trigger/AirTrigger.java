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
        return true;
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