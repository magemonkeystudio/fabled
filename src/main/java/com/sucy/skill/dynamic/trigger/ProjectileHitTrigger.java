package com.sucy.skill.dynamic.trigger;

import com.sucy.skill.api.Settings;
import com.sucy.skill.api.particle.target.EntityTarget;
import com.sucy.skill.dynamic.TempEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * ProSkillAPI © 2023
 * com.sucy.skill.dynamic.trigger.ProjectileHitTrigger
 */
public class ProjectileHitTrigger implements Trigger<ProjectileHitEvent> {
    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "PROJECTILE_HIT";
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
        if (!(event.getEntity().getShooter() instanceof LivingEntity)) return false;
        List<String> projectiles = settings.getStringList("projectile");
        String type = settings.getString("type","both");
        boolean hit = !Objects.isNull(event.getHitEntity());
        boolean isCorrectProjectile = projectiles.isEmpty() || projectiles.contains("Any") || projectiles.stream().anyMatch(projectile->event.getEntityType().name().equalsIgnoreCase(projectile));
        boolean isCorrectType = type.equalsIgnoreCase("both") || type.equalsIgnoreCase("entity") == hit;
        return  isCorrectProjectile && isCorrectType;
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
        TempEntity projectile = new TempEntity(new EntityTarget(event.getEntity()));
        boolean targetCaster = settings.getBool("target",false);
        Entity hit = event.getHitEntity();
        return targetCaster? (LivingEntity) event.getEntity().getShooter() : (Objects.isNull(hit)? projectile: (hit instanceof LivingEntity? (LivingEntity) hit :new TempEntity(hit.getLocation())));
    }
}
