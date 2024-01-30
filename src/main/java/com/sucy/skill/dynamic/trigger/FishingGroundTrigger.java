package com.sucy.skill.dynamic.trigger;

import com.sucy.skill.api.CastData;
import com.sucy.skill.api.Settings;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.Map;

public class FishingGroundTrigger implements Trigger<ProjectileHitEvent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "FISHING_GROUND";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<ProjectileHitEvent> getEvent() {
        return ProjectileHitEvent.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldTrigger(ProjectileHitEvent event, int level, Settings settings) {

        if (event.getEntityType() == EntityType.FISHING_HOOK && event.getHitEntity() == null
                && event.getHitBlock() != null) {
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValues(ProjectileHitEvent event, final CastData data) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(ProjectileHitEvent event) {
        return ((LivingEntity) event.getEntity().getShooter());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(ProjectileHitEvent event, Settings settings) {
        return ((LivingEntity) event.getEntity().getShooter());
    }

}
