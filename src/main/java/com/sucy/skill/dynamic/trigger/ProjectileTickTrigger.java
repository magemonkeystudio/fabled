package com.sucy.skill.dynamic.trigger;

import com.sucy.skill.api.Settings;
import com.sucy.skill.api.event.ProjectileTickEvent;
import com.sucy.skill.dynamic.TempEntity;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import java.util.List;
import java.util.Map;

/**
 * ProSkillAPI © 2023
 * com.sucy.skill.dynamic.trigger.ProjectileLandTrigger
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
        Location loc = event.getProjectile().getLocation();
        var shooter = event.getEntity().getLocation();
        loc.setYaw(shooter.getYaw());
        loc.setPitch(shooter.getPitch());
        return new TempEntity(loc);
    }
}