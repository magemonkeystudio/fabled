/**
 * SkillAPI
 * com.sucy.skill.dynamic.mechanic.FlagMechanic
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2014 Steven Sucy
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
package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.enums.Operation;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerStatModifier;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Applies a flag to each target
 */
public class StatMechanic extends MechanicComponent {
    private static final String KEY       = "key";
    private static final String OPERATION = "operation";
    private static final String AMOUNT    = "amount";
    private static final String SECONDS   = "seconds";
    private static final String STACKABLE = "stackable";

    private final Map<Integer, Map<String, StatTask>> tasks = new HashMap<>();

    /**
     * Executes the component
     *
     * @param caster  caster of the skill
     * @param level   level of the skill
     * @param targets targets to apply to
     *
     * @param force
     * @return true if applied to something, false otherwise
     */
    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        String key = settings.getString(KEY, "");
        if (targets.size() == 0) {
            return false;
        }

        final Map<String, StatTask> casterTasks = tasks.computeIfAbsent(caster.getEntityId(), HashMap::new);
        final int                   amount      = (int) parseValues(caster, AMOUNT, level, 5);
        final double                seconds     = parseValues(caster, SECONDS, level, 3.0);
        final boolean               stackable   = settings.getString(STACKABLE, "false").equalsIgnoreCase("true");
        final int                   ticks       = (int) (seconds * 20);

        boolean worked = false;
        for (LivingEntity target : targets) {
            if (target instanceof Player) {
                worked = true;
                final PlayerData   data     = SkillAPI.getPlayerData((Player) target);
                PlayerStatModifier modifier = new PlayerStatModifier("skillapi.mechanic.stat_mechanic", amount, Operation.valueOf(OPERATION), false);

                if (casterTasks.containsKey(data.getPlayerName()) && !stackable) {
                    final StatTask old = casterTasks.remove(data.getPlayerName());

                    data.removeStatModifier(old.modifier.getUUID(), false);

                    data.addStatModifier(key, modifier, true);

                    old.cancel();
                } else {
                    data.addStatModifier(key, modifier, true);
                }

                final StatTask task = new StatTask(caster.getEntityId(), data, modifier);
                casterTasks.put(data.getPlayerName(), task);
                if (ticks >= 0) {
                    SkillAPI.schedule(task, ticks);
                }
            }
        }
        return worked;
    }

    @Override
    public String getKey() {
        return "stat";
    }

    @Override
    protected void doCleanUp(final LivingEntity user) {
        final Map<String, StatTask> casterTasks = tasks.remove(user.getEntityId());
        if (casterTasks != null) {
            casterTasks.values().forEach(StatTask::stop);
        }
    }

    private class StatTask extends BukkitRunnable {

        private final PlayerData         data;
        private final PlayerStatModifier modifier;
        private final int                id;
        private       boolean            running = false;
        private boolean            stopped = false;

        StatTask(int id, PlayerData data, PlayerStatModifier modifier) {
            this.id = id;
            this.data = data;
            this.modifier = modifier;
        }

        public void stop() {
            if (!stopped) {
                stopped = true;
                run();
                if (running) {
                    cancel();
                }
            }
        }

        @Override
        public BukkitTask runTaskLater(final Plugin plugin, final long delay) {
            running = true;
            return super.runTaskLater(plugin, delay);
        }

        @Override
        public void run() {
            data.removeAttributeModifier(modifier.getUUID(), true);
            if (tasks.containsKey(id)) {
                tasks.get(id).remove(data.getPlayerName());
            }
            running = false;
        }
    }
}
