package com.sucy.skill.dynamic.trigger;

import com.sucy.skill.api.CastData;
import com.sucy.skill.api.Settings;
import com.sucy.skill.api.particle.target.EntityTarget;
import com.sucy.skill.dynamic.TempEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.projectiles.ProjectileSource;

import java.util.List;
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
        Projectile   proj        = event.getEntity();
        List<String> projectiles = settings.getStringList("projectile");
        String       type        = translateType(settings.getString("type", "both"));

        boolean hitEntity = Objects.nonNull(event.getHitEntity());

        boolean correctProjectile = projectiles.isEmpty()
                || projectiles.contains("Any")
                || projectiles.stream().anyMatch(projectile -> proj.getType().name().equalsIgnoreCase(projectile));
        boolean correctType = type.equalsIgnoreCase("both") || type.equalsIgnoreCase("entity") == hitEntity;

        return correctProjectile && correctType;
    }

    private String translateType(String typeInput) {
        return switch (typeInput.toLowerCase()) {
            case "both" -> "both";
            case "entity" -> "entity";
            default -> "both";
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValues(ProjectileHitEvent event, CastData data) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(ProjectileHitEvent event) {
        ProjectileSource shooter = event.getEntity().getShooter();
        return shooter instanceof LivingEntity ? (LivingEntity) shooter : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(ProjectileHitEvent event, Settings settings) {
        TempEntity projectile   = new TempEntity(new EntityTarget(event.getEntity()));
        boolean    targetCaster = settings.getBool("target", false);
        Entity     hit          = event.getHitEntity();

        if (targetCaster) {
            return (LivingEntity) event.getEntity().getShooter();
        } else if (Objects.nonNull(hit)) {
            return hit instanceof LivingEntity ? (LivingEntity) hit : new TempEntity(hit.getLocation());
        }

        return projectile;
    }
}
