package studio.magemonkey.fabled.task;

import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.event.ProjectileTickEvent;
import studio.magemonkey.fabled.dynamic.ComponentRegistry;
import studio.magemonkey.fabled.dynamic.trigger.ProjectileTickTrigger;
import studio.magemonkey.fabled.listener.ProjectileListener;
import studio.magemonkey.fabled.thread.RepeatThreadTask;
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
