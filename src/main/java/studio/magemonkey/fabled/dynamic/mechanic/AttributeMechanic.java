/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.mechanic.FlagMechanic
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

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.enums.Operation;
import studio.magemonkey.fabled.api.player.PlayerAttributeModifier;
import studio.magemonkey.fabled.api.player.PlayerData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Applies a flag to each target
 */
public class AttributeMechanic extends MechanicComponent {
    private static final String KEY       = "key";
    private static final String OPERATION = "operation";
    private static final String AMOUNT    = "amount";
    private static final String SECONDS   = "seconds";
    private static final String STACKABLE = "stackable";

    private final Map<Integer, Map<String, AttribTask>> tasks = new HashMap<>();

    @Override
    public String getKey() {
        return "attribute";
    }

    @Override
    protected void doCleanUp(final LivingEntity user) {
        final Map<String, AttribTask> casterTasks = tasks.remove(user.getEntityId());
        if (casterTasks != null) {
            casterTasks.values().forEach(AttribTask::stop);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        if (targets.isEmpty()) {
            return false;
        }

        List<String> keys;
        if (!settings.getStringList(KEY).isEmpty()) {
            keys = settings.getStringList(KEY)
                    .stream()
                    .filter(key -> Fabled.getAttributeManager().getAttribute(key) != null)
                    .collect(Collectors.toList());
        } else {
            // Attempt to read it as a string, optionally comma separated
            String data = settings.getString(KEY);
            if (data == null || data.isBlank() || data.equals("[]")) {
                keys = new ArrayList<>();
            } else {
                keys = List.of(settings.getString(KEY).split(","));
            }
        }

        final Map<String, AttribTask> casterTasks = tasks.computeIfAbsent(caster.getEntityId(), HashMap::new);
        final double                  amount      = parseValues(caster, AMOUNT, level, 5);
        final double                  seconds     = parseValues(caster, SECONDS, level, 3.0);
        final boolean                 stackable   = settings.getString(STACKABLE, "false").equalsIgnoreCase("true");
        final int                     ticks       = (int) (seconds * 20);
        final String                  operation   = settings.getString(OPERATION, "ADD_NUMBER");

        boolean worked = false;
        for (LivingEntity target : targets) {
            if (target instanceof Player) {
                worked = true;
                final PlayerData data = Fabled.getData((Player) target);
                PlayerAttributeModifier modifier = new PlayerAttributeModifier("fabled.mechanic.attribute_mechanic",
                        amount,
                        Operation.valueOf(operation),
                        false);


                if (casterTasks.containsKey(data.getPlayerName()) && !stackable) {
                    final AttribTask old = casterTasks.remove(data.getPlayerName());
                    data.removeAttributeModifier(old.modifier.getUUID(), false);
                    old.cancel();
                }

                for (String key : keys) {
                    data.addAttributeModifier(key, modifier, true);
                }

                final AttribTask task = new AttribTask(caster.getEntityId(), data, modifier);
                casterTasks.put(data.getPlayerName(), task);
                if (ticks >= 0) {
                    Fabled.schedule(task, ticks);
                }
            }
        }
        return worked;
    }

    private class AttribTask extends BukkitRunnable {

        private final PlayerData              data;
        private final PlayerAttributeModifier modifier;
        private final int                     id;
        private       boolean                 running = false;
        private       boolean                 stopped = false;

        AttribTask(int id, PlayerData data, PlayerAttributeModifier modifier) {
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
