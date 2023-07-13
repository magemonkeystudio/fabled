package com.sucy.skill.dynamic.trigger;

import com.sucy.skill.api.Settings;
import com.sucy.skill.api.event.ProjectileTickEvent;
import com.sucy.skill.api.particle.target.EntityTarget;
import com.sucy.skill.dynamic.TempEntity;
import org.bukkit.entity.LivingEntity;

import java.util.List;
import java.util.Map;

/**
 * ProSkillAPI © 2023
 * com.sucy.skill.dynamic.trigger.ProjectileTickTrigger
 */
public class ProjectileTickTrigger implements Trigger<ProjectileTickEvent> {
    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "PROJECTILE_TICK";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<ProjectileTickEvent> getEvent() {
        return ProjectileTickEvent.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldTrigger(ProjectileTickEvent event, int level, Settings settings) {
        List<String> projectiles = settings.getStringList("projectile");
        return projectiles.isEmpty() || projectiles.contains("Any") || projectiles.stream().anyMatch(projectile->event.getProjectile().getType().name().equalsIgnoreCase(projectile));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValues(ProjectileTickEvent event, Map<String, Object> data) {}
    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(ProjectileTickEvent event) {
        return (LivingEntity) event.getEntity();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(ProjectileTickEvent event, Settings settings) {
        boolean targetCaster = settings.getBool("target",false);
        return targetCaster? (LivingEntity) event.getEntity() : new TempEntity(new EntityTarget(event.getProjectile()));
    }
}