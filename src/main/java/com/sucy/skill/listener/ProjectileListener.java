package com.sucy.skill.listener;

import com.sucy.skill.api.event.ProjectileTickEvent;
import com.sucy.skill.task.ProjectileTickTask;
import com.sucy.skill.thread.MainThread;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.projectiles.ProjectileSource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Listener to throw custom {@link ProjectileTickEvent}
 */
public class ProjectileListener extends SkillAPIListener {
    private static final List<UUID> flyingProjectiles = new ArrayList<>();

    @Override
    public void cleanup() {
        ProjectileListener.flyingProjectiles.clear();
    }

    /**
     * Marks projectiles as "flying"
     */
    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {
        ProjectileSource shooter = event.getEntity().getShooter();
        if (!(shooter instanceof LivingEntity)) return;

        flyingProjectiles.add(event.getEntity().getUniqueId());
        MainThread.register(new ProjectileTickTask((LivingEntity) event.getEntity().getShooter(), event.getEntity()));
    }

    /**
     * Removes mark from projectile
     */
    @EventHandler
    public void onHit(ProjectileHitEvent event) {
        ProjectileSource shooter = event.getEntity().getShooter();
        if (!(shooter instanceof LivingEntity)) return;

        flyingProjectiles.remove(event.getEntity().getUniqueId());
    }

    /**
     * Checks if specified projectile
     * is still in air
     */
    public static boolean isFlying(Projectile projectile) {
        boolean isValid = projectile.isValid();
        if (!isValid)
            flyingProjectiles.remove(projectile.getUniqueId());

        return flyingProjectiles.contains(projectile.getUniqueId());
    }
}
