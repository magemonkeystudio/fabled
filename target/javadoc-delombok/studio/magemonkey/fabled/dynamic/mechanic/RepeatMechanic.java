/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.mechanic.RepeatMechanic
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

import studio.magemonkey.fabled.Fabled;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Executes child components multiple times
 */
public class RepeatMechanic extends MechanicComponent {
    private static final String REPETITIONS  = "repetitions";
    private static final String DELAY        = "delay";
    private static final String PERIOD       = "period";
    private static final String STOP_ON_FAIL = "stop-on-fail";

    private final Map<Integer, List<RepeatTask>> tasks = new HashMap<>();

    /**
     * Executes the component
     *
     * @param caster  caster of the skill
     * @param level   level of the skill
     * @param targets targets to apply to
     * @param force
     * @return true if applied to something, false otherwise
     */
    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        if (targets.size() > 0) {
            int count = (int) parseValues(caster, REPETITIONS, level, 3.0);
            if (count <= 0) {
                return false;
            }

            final int     delay      = (int) (settings.getDouble(DELAY, 0.0) * 20);
            final int     period     = (int) (settings.getDouble(PERIOD, 1.0) * 20);
            final boolean stopOnFail = settings.getBool(STOP_ON_FAIL, false);
            if (period <= 0) {
                // 0 tick loop
                while (count > 0) {
                    count = execute(caster, targets, count, stopOnFail, force);
                }
            } else {
                final RepeatTask task = new RepeatTask(caster, targets, count, delay, period, stopOnFail, force);
                tasks.computeIfAbsent(caster.getEntityId(), ArrayList::new).add(task);
            }
            return true;
        }
        return false;
    }

    private int execute(LivingEntity caster, List<LivingEntity> targets, int count, boolean stopOnFail, boolean force) {
        for (int i = 0; i < targets.size(); i++) {
            if (targets.get(i).isDead() || !targets.get(i).isValid()) {
                targets.remove(i);
            }
        }

        if ((!skill.isActive(caster) && !force) || targets.size() == 0) {
            return 0;
        }

        final int level   = skill.getActiveLevel(caster);
        boolean   success = executeChildren(caster, level, targets, force);

        if (--count <= 0 || (!success && stopOnFail)) {
            return 0;
        }

        if (skill.checkCancelled()) {
            return 0;
        }
        return count;
    }

    @Override
    public String getKey() {
        return "repeat";
    }

    @Override
    protected void doCleanUp(final LivingEntity caster) {
        final List<RepeatTask> casterTasks = tasks.remove(caster.getEntityId());
        if (casterTasks != null) {
            casterTasks.forEach(RepeatTask::cancel);
        }
    }

    private class RepeatTask extends BukkitRunnable {
        private final List<LivingEntity> targets;
        private final LivingEntity       caster;
        private final boolean            stopOnFail;
        private final boolean            force;

        private int count;

        RepeatTask(
                LivingEntity caster,
                List<LivingEntity> targets,
                int count,
                int delay,
                int period,
                boolean stopOnFail,
                boolean force) {
            this.targets = new ArrayList<>(targets);
            this.caster = caster;
            this.count = count;
            this.stopOnFail = stopOnFail;
            this.force = force;

            Fabled.schedule(this, delay, period);
        }

        @Override
        public void cancel() {
            super.cancel();
            final List<RepeatTask> casterTasks = tasks.get(caster.getEntityId());
            if (casterTasks != null) {
                casterTasks.remove(this);
            }
        }

        @Override
        public void run() {
            count = execute(caster, targets, count, stopOnFail, force);
            if (count <= 0) {
                cancel();
            }
        }
    }
}
