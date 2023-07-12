package com.sucy.skill.listener;

import com.sucy.skill.api.event.ProjectileTickEvent;
import com.sucy.skill.log.Logger;
import com.sucy.skill.task.ProjectileTickTask;
import com.sucy.skill.thread.MainThread;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Listener to throw custom {@link ProjectileTickEvent}
 */
public class ProjectileListener extends SkillAPIListener{
    private static final List<UUID> flyingProjectiles = new ArrayList<>();

    @Override
    public void cleanup() {
        ProjectileListener.flyingProjectiles.clear();
    }

    /**
     * Marks projectiles as "flying"
     */
    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event){
        var shooter = event.getEntity().getShooter();
        if (!(shooter instanceof LivingEntity)) return;
        var tickEvent = new ProjectileTickEvent((LivingEntity) shooter,event.getEntity());
        flyingProjectiles.add(event.getEntity().getUniqueId());
        MainThread.register(new ProjectileTickTask(tickEvent));
    }

    /**
     * Removes mark from projectile
     */
    @EventHandler
    public void onHit(ProjectileHitEvent event){
        var shooter = event.getEntity().getShooter();
        if(!(shooter instanceof LivingEntity)) return;
        if(isFlying(event.getEntity())){
            flyingProjectiles.remove(event.getEntity().getUniqueId());
        }

    }
    /**
     * Checks if specified projectile
     * is still in air
     */
    public static boolean isFlying(Projectile projectile){
        var res = flyingProjectiles.contains(projectile.getUniqueId());
        return res;
    }
}
