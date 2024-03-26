package com.promcteam.fabled.dynamic.trigger;

import com.promcteam.fabled.api.CastData;
import com.promcteam.fabled.api.Settings;
import com.promcteam.fabled.api.event.ProjectileTickEvent;
import com.promcteam.fabled.api.particle.target.EntityTarget;
import com.promcteam.fabled.dynamic.TempEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Fabled © 2024
 * com.promcteam.fabled.dynamic.trigger.ProjectileTickTrigger
 */
public class ProjectileTickTrigger implements Trigger<ProjectileTickEvent> {
    private Map<UUID, Integer> timer = new HashMap<>();

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
        Entity       proj        = event.getEntity();
        List<String> projectiles = settings.getStringList("projectile");
        boolean correctProjectile = projectiles.isEmpty()
                || projectiles.contains("Any")
                || projectiles.stream().anyMatch(projectile -> proj.getType().name().equalsIgnoreCase(projectile));
        if (!correctProjectile) return false;

        int interval = settings.getInt("interval", 1);
        int delay    = settings.getInt("delay", 0);

        UUID projectileUUID = event.getProjectile().getUniqueId();
        if (!timer.containsKey(projectileUUID)) timer.put(projectileUUID, 0);

        int     timerValue  = timer.get(event.getProjectile().getUniqueId());
        boolean isTimerTick = event.getTick() - timerValue * interval - delay >= 0;
        if (isTimerTick) timer.put(projectileUUID, timerValue + 1);

        return isTimerTick;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValues(ProjectileTickEvent event, final CastData data) {}

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
        boolean targetCaster = settings.getBool("target", false);
        return targetCaster
                ? (LivingEntity) event.getEntity()
                : new TempEntity(new EntityTarget(event.getProjectile()));
    }

    /**
     * Removes projectile from the timer Map.
     *
     * @param uuid UUID of the projectile
     */
    public void removeProjectile(UUID uuid) {
        timer.remove(uuid);
    }
}