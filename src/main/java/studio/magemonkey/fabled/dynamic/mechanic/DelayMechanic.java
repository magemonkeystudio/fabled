/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.mechanic.DelayMechanic
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 MageMonkeyStudio
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package studio.magemonkey.fabled.dynamic.mechanic;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import studio.magemonkey.fabled.Fabled;

import java.util.*;

/**
 * Executes child components after a delay
 */
public class DelayMechanic extends MechanicComponent {
    private static final String                   SECONDS         = "delay";
    private static final String                   CLEANUP         = "cleanup";
    private static final String                   SINGLE_INSTANCE = "single-instance";
    final                Map<UUID, List<Integer>> tasks           = new HashMap<>();

    @Override
    public String getKey() {
        return "delay";
    }

    @Override
    public boolean execute(final LivingEntity caster,
                           final int level,
                           final List<LivingEntity> targets,
                           boolean force) {
        if (targets.isEmpty()) {
            return false;
        }

        boolean singleInstance = settings.getBool(SINGLE_INSTANCE, false);
        double  seconds        = parseValues(caster, SECONDS, level, 2.0);

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                executeChildren(caster, level, targets, force);
                tasks.get(caster.getUniqueId()).remove(Integer.valueOf(this.getTaskId()));
            }
        }.runTaskLater(Fabled.inst(), (long) (seconds * 20));

        tasks.compute(caster.getUniqueId(), (uuid, list) -> {
            if (list == null) {
                list = new ArrayList<>();
            }

            if (singleInstance) {
                list.forEach(Bukkit.getScheduler()::cancelTask);
                list.clear();
            }

            list.add(task.getTaskId());
            return list;
        });

        return true;
    }

    @Override
    protected void doCleanUp(LivingEntity caster) {
        boolean shouldGetCleanedUp = settings.getBool(CLEANUP, true);
        if (!shouldGetCleanedUp) return;

        List<Integer> taskList = tasks.remove(caster.getUniqueId());
        if (taskList != null) {
            taskList.forEach(Bukkit.getScheduler()::cancelTask);
        }
    }
}
