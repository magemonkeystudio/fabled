package com.sucy.skill.dynamic.trigger;

import com.sucy.skill.api.CastData;
import com.sucy.skill.api.Settings;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * ProSkillAPI © 2023
 * com.sucy.skill.dynamic.trigger.EntityTargetEvent
 */
public class EntityTargetTrigger implements Trigger<EntityTargetLivingEntityEvent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "ENTITY_TARGET";
    }

    @Override
    public Class<EntityTargetLivingEntityEvent> getEvent() {
        return EntityTargetLivingEntityEvent.class;
    }

    @Override
    public boolean shouldTrigger(EntityTargetLivingEntityEvent event, int level, Settings settings) {
        if (!(event.getEntity() instanceof LivingEntity) || event.getTarget() == null)
            return false;

        List<String> types    = settings.getStringList("types");
        boolean      inverted = settings.getBool("blacklist", false);
        if (!types.isEmpty() && !types.get(0).equalsIgnoreCase("Any")) {
            for (String type : types) {
                EntityType entityType = EntityType.valueOf(type.toUpperCase(Locale.US));
                if (event.getEntityType() == entityType)
                    return !inverted;
            }
            return inverted;
        }

        return !inverted;
    }

    @Override
    public void setValues(EntityTargetLivingEntityEvent event, final CastData data) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(final EntityTargetLivingEntityEvent event) {
        return event.getTarget();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(final EntityTargetLivingEntityEvent event, final Settings settings) {
        boolean targetCaster = settings.getBool("target", true);
        return targetCaster ? event.getTarget() : (LivingEntity) event.getEntity();
    }
}
