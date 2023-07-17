package com.sucy.skill.task;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.event.ProjectileTickEvent;
import com.sucy.skill.dynamic.ComponentRegistry;
import com.sucy.skill.dynamic.trigger.ProjectileTickTrigger;
import com.sucy.skill.listener.ProjectileListener;
import com.sucy.skill.thread.RepeatThreadTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;

/**
 * Task used to implement {@link ProjectileTickEvent}
 */
public class ProjectileTickTask extends RepeatThreadTask {

    private final LivingEntity shooter;
    private final Projectile projectile;
    private int tick;

    public ProjectileTickTask(LivingEntity shooter, Projectile projectile) {
        super(0, 1);
        expired = false;
        tick = 1;
        this.shooter = shooter;
        this.projectile = projectile;
    }

    /**
     * Checks if projectile still did not hit the entity/ground
     * and then throws @{@link ProjectileTickEvent}
     */
    @Override
    public void run() {
        expired = !ProjectileListener.isFlying(projectile);
        if (!expired) {
            ProjectileTickEvent event = new ProjectileTickEvent(shooter,projectile,tick);
            Bukkit.getScheduler().runTask(SkillAPI.inst(),()->Bukkit.getPluginManager().callEvent(event));
            tick++;
        }else{
            ((ProjectileTickTrigger)ComponentRegistry.getTrigger("PROJECTILE_TICK")).removeProjectile(projectile.getUniqueId());
        }
    }
}
