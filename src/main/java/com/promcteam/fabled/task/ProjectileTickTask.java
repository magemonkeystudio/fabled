package com.promcteam.fabled.task;

import com.promcteam.fabled.Fabled;
import com.promcteam.fabled.api.event.ProjectileTickEvent;
import com.promcteam.fabled.dynamic.ComponentRegistry;
import com.promcteam.fabled.dynamic.trigger.ProjectileTickTrigger;
import com.promcteam.fabled.listener.ProjectileListener;
import com.promcteam.fabled.thread.RepeatThreadTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;

/**
 * Task used to implement {@link ProjectileTickEvent}
 */
public class ProjectileTickTask extends RepeatThreadTask {

    private final LivingEntity shooter;
    private final Projectile   projectile;
    private       int          tick;

    public ProjectileTickTask(LivingEntity shooter, Projectile projectile) {
        super(0, 1);

        this.shooter = shooter;
        this.projectile = projectile;

        expired = false;
        tick = 1;
    }

    /**
     * Checks if projectile still did not hit the entity/ground
     * and then throws @{@link ProjectileTickEvent}
     */
    @Override
    public void run() {
        expired = !ProjectileListener.isFlying(projectile);
        if (!expired) {
            ProjectileTickEvent event = new ProjectileTickEvent(shooter, projectile, tick);
            Bukkit.getScheduler().runTask(Fabled.inst(), () -> Bukkit.getPluginManager().callEvent(event));
            tick++;
        } else {
            ((ProjectileTickTrigger) ComponentRegistry.getTrigger("PROJECTILE_TICK")).removeProjectile(projectile.getUniqueId());
        }
    }
}
