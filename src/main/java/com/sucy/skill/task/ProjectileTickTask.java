package com.sucy.skill.task;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.event.ProjectileTickEvent;
import com.sucy.skill.listener.ProjectileListener;
import com.sucy.skill.thread.RepeatThreadTask;
import org.bukkit.Bukkit;

/**
 * Task used to implement {@link ProjectileTickEvent}
 */
public class ProjectileTickTask extends RepeatThreadTask {

    private final ProjectileTickEvent event;

    public ProjectileTickTask(ProjectileTickEvent event) {
        super(0, 1);
        expired = false;
        this.event = event;
    }

    /**
     * Checks if projectile still did not hit the entity/ground
     * and then throws @{@link ProjectileTickEvent}
     */
    @Override
    public void run() {
        expired = !ProjectileListener.isFlying(event.getProjectile());
        if (!expired) {
            Bukkit.getScheduler().runTask(SkillAPI.inst(),()->Bukkit.getPluginManager().callEvent(event));
        }
    }
}
