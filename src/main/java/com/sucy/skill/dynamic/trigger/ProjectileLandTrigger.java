package com.sucy.skill.dynamic.trigger;

import com.sucy.skill.api.Settings;
import com.sucy.skill.dynamic.TempEntity;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * ProSkillAPI © 2023
 * com.sucy.skill.dynamic.trigger.ProjectileLandTrigger
 */
public class ProjectileLandTrigger implements Trigger<ProjectileHitEvent> {
    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "PROJECTILE_LAND";
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
        if(!Objects.isNull(event.getHitEntity())) return false;
            if (!(event.getEntity().getShooter() instanceof LivingEntity)) return false;
            List<String> projectiles = settings.getStringList("projectile");
            return projectiles.isEmpty() || projectiles.contains("Any") || projectiles.stream().anyMatch(projectile->event.getEntityType().name().equalsIgnoreCase(projectile));

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValues(ProjectileHitEvent event, Map<String, Object> data) {}
    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(ProjectileHitEvent event) {
        var shooter = event.getEntity().getShooter();
        return shooter instanceof LivingEntity? (LivingEntity) shooter : null;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(ProjectileHitEvent event, Settings settings) {
        Location loc = event.getEntity().getLocation();
        var shooter = ((LivingEntity) event.getEntity().getShooter()).getLocation();
        loc.setYaw(shooter.getYaw());
        loc.setPitch(shooter.getPitch());
        return new TempEntity(loc);
    }
}
