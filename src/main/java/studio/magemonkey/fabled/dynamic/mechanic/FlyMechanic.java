package studio.magemonkey.fabled.dynamic.mechanic;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.player.PlayerData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Sets the flight state and flight speed of a player.
 * Does not persist on logout. 
 */
public class FlyMechanic extends MechanicComponent {
    private static final String SECONDS = "seconds";
    private static final String FLYSPEED = "flyspeed";
    private static final String FLYING = "flying";

    private final Map<Integer, Map<String, FlyTask>> tasks = new HashMap<>();

    @Override
    public String getKey() {
        return "fly";
    }
    
    @Override
    protected void doCleanUp(final LivingEntity user) {
        final Map<String, FlyTask> casterTasks = tasks.remove(user.getEntityId());
        if (casterTasks != null) {
            casterTasks.values().forEach(FlyTask::stop);
        }
    }

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
        final double    seconds  = parseValues(caster, SECONDS, level, 3.0); // Get seconds or default to 3 seconds.
        final int       ticks    = (int) (seconds * 20);
        float           flyspeed = (float) parseValues(caster, FLYSPEED, level, 0.1); // Get flyspeed or default value.
        boolean         flying   = settings.getString(FLYING, "false").equalsIgnoreCase("true"); // Get if a player wants to grant or remove flight.
        final Map<String, FlyTask> casterTasks = tasks.computeIfAbsent(caster.getEntityId(), HashMap::new); // Map of all current Tasks.

        for (LivingEntity target : targets) {
            // Only target players.
            if (target instanceof Player){
                Player player = (Player) target;
                final PlayerData data = Fabled.getData((Player) target);
                // Bound Flightspeed as it cannot be greater than 1 or less than -1.
                if (flyspeed > 1){
                    flyspeed = 1.0f;
                }
                else if (flyspeed < -1){
                    flyspeed = -1.0f;
                }
                // Set player flight based on given boolean.
                player.setAllowFlight(flying);
                player.setFlying(flying);
                player.setFlySpeed(flyspeed);

                /* 
                / Cancel previous tasks if one already exists.
                / This allows flight to be extended if players cast multiple skills.
                / Without this players may fall too early or unexpectedly.
                */
                if (casterTasks.containsKey(data.getPlayerName())){
                    final FlyTask oldTask = casterTasks.remove(data.getPlayerName());
                    oldTask.cancel();
                }
                // Only create a new task and schedule if the players wants flight, otherwise do nothing.
                if (flying) {
                    final FlyTask task = new FlyTask(caster.getEntityId(), data);
                    if (ticks >= 0){
                        Fabled.schedule(task, ticks);
                    }
                }
            }
        }
        return targets.size() > 0;
    }

    private class FlyTask extends BukkitRunnable {

        private final PlayerData         data;
        private final int                id;
        private       boolean            running = false;
        private       boolean            stopped = false;

        FlyTask(int id, PlayerData data) {
            this.id = id;
            this.data = data;
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
            Player player = data.getPlayer();
            player.setFlying(false);
            player.setAllowFlight(false);
            if (tasks.containsKey(id)) {
                tasks.get(id).remove(data.getPlayerName());
            }
            running = false;
        }
    }

}