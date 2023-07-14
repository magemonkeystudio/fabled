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
import java.util.stream.Stream;

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
        List<String> projectiles = settings.getStringList("projectile");
        String typeBase = settings.getString("type","both");
        String type = typeBase;
        if (Stream.of("both","entity","block").noneMatch(c-> typeBase.equalsIgnoreCase(c))){
            type = "both";
        }
        boolean hitEntity = !Objects.isNull(event.getHitEntity());
        boolean hitBlock = !Objects.isNull(event.getHitBlock());
        boolean isCorrectProjectile = projectiles.isEmpty() || projectiles.contains("Any") || projectiles.stream().anyMatch(projectile->event.getEntityType().name().equalsIgnoreCase(projectile));
        boolean isCorrectType = type.equalsIgnoreCase("both") || (type.equalsIgnoreCase("entity") == hitEntity && type.equalsIgnoreCase("block") == hitBlock);
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
