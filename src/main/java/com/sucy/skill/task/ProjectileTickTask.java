package com.sucy.skill.task;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.event.ProjectileTickEvent;
import com.sucy.skill.listener.ProjectileListener;
import com.sucy.skill.log.Logger;
import com.sucy.skill.thread.IThreadTask;
import com.sucy.skill.thread.MainThread;
import com.sucy.skill.thread.RepeatThreadTask;
import org.bukkit.Bukkit;

public class ProjectileTickTask extends RepeatThreadTask {

    private final ProjectileTickEvent event;

    public ProjectileTickTask(ProjectileTickEvent event) {
        super(0, 1);
        expired = false;
        this.event = event;
    }

    @Override
    public void run() {
        expired = !ProjectileListener.isFlying(event.getProjectile());
        Logger.log("Run "+event.getProjectile().getUniqueId()+": "+expired);
        if (!expired) {
            Bukkit.getScheduler().runTask(SkillAPI.inst(),()->Bukkit.getPluginManager().callEvent(event));
            //            MainThread.register(new IThreadTask() {
//                @Override
//                public boolean tick() {
//                    return false;
//                }
//
//                @Override
//                public void run() {
//                    Bukkit.getPluginManager().callEvent(event);
//                }
//            });
            //Bukkit.getPluginManager().callEvent(event);
        }
    }
}
