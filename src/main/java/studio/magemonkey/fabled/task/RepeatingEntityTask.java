/**
 * Fabled
 * studio.magemonkey.fabled.task.RepeatingEntityTask
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
package studio.magemonkey.fabled.task;

import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import studio.magemonkey.fabled.Fabled;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple task for performing an action on a list of entities every tick
 */
public class RepeatingEntityTask<T extends Entity> extends BukkitRunnable {
    private List<T>       entities;
    private EntityTask<T> entityTask;

    /**
     * Initializes a new task to perform a given action on an entity every tick
     *
     * @param entities   entities to affect
     * @param entityTask the task to apply on all remaining projectiles
     */
    public RepeatingEntityTask(List<T> entities, EntityTask<T> entityTask) {
        this.entities = entities;
        this.entityTask = entityTask;
        Fabled.schedule(this, 1, 1);
    }

    /**
     * Applies the entity task
     */
    @Override
    public void run() {
        List<T> toRemove = new ArrayList<>();
        for (T entity : entities) {
            if (!entity.isValid() || entity.isDead() || stoppedMoving(entity)) {
                toRemove.add(entity);
                continue;
            }

            entityTask.apply(entity);
        }

        entities.removeAll(toRemove);
        if (entities.size() == 0) this.cancel();
    }

    private boolean stoppedMoving(T ent) {
        Vector vector = ent.getVelocity();
        return vector.getX() == 0 && vector.getY() == 0 && vector.getZ() == 0;
    }
}
