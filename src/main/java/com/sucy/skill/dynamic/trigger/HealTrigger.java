package com.sucy.skill.dynamic.trigger;

import com.sucy.skill.api.Settings;
import com.sucy.skill.dynamic.DynamicSkill;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import java.util.Map;

public class HealTrigger implements Trigger<EntityRegainHealthEvent>{
    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "HEAL";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<EntityRegainHealthEvent> getEvent() {return EntityRegainHealthEvent.class;}

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldTrigger(EntityRegainHealthEvent event, int level, Settings settings) {
        final double  min        = settings.getDouble("heal-min");
        final double  max        = settings.getDouble("heal-max");
        return event.getAmount() >= min && event.getAmount() <= max;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValues(EntityRegainHealthEvent event, Map<String, Object> data) {
        data.put("api-heal", event.getAmount());
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(EntityRegainHealthEvent event) {
        return (LivingEntity) event.getEntity();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(EntityRegainHealthEvent event, Settings settings) {
        return (LivingEntity) event.getEntity();
    }
}