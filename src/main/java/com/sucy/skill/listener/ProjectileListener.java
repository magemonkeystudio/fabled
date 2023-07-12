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

public class ProjectileListener extends SkillAPIListener{
    private static final List<UUID> flyingProjectiles = new ArrayList<>();

    @Override
    public void cleanup() {
        ProjectileListener.flyingProjectiles.clear();
    }
    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event){
        var shooter = event.getEntity().getShooter();
        if (!(shooter instanceof LivingEntity)) return;
        var tickEvent = new ProjectileTickEvent((LivingEntity) shooter,event.getEntity());
        flyingProjectiles.add(event.getEntity().getUniqueId());
        Logger.log("Launch "+event.getEntity().getUniqueId());
        Logger.log("flyingProjectiles length "+flyingProjectiles.size());
        MainThread.register(new ProjectileTickTask(tickEvent));
    }
    @EventHandler
    public void onHit(ProjectileHitEvent event){
        var shooter = event.getEntity().getShooter();
        if(!(shooter instanceof LivingEntity)) return;
        if(isFlying(event.getEntity())){
            Logger.log("flyingProjectiles before remove "+flyingProjectiles);
            flyingProjectiles.remove(event.getEntity().getUniqueId());
            Logger.log("flyingProjectiles after remove "+flyingProjectiles);
        }

    }
    /**
     * Checks if specified projectile
     * is still in air
     */
    public static boolean isFlying(Projectile projectile){
        var res = flyingProjectiles.contains(projectile.getUniqueId());
        Logger.log("isFlying "+projectile.getUniqueId()+":"+res);
        return res;
    }
}
