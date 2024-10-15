package studio.magemonkey.fabled.dynamic.mechanic;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Grants or takes away creative level flight to a player.
 * Does not persist on logout. 
 */
public class FlyMechanic extends MechanicComponent {
    private static final String FLYSPEED = "flyspeed";
    private static final String SECONDS = "seconds";


    @Override
    public String getKey() {
        return "fly";
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
        float flyspeed = (float) parseValues(caster, FLYSPEED, level, 0.6);
        double seconds = parseValues(caster, SECONDS, level, 3.0);
        
        for (LivingEntity target : targets) {
            if (target instanceof Player){
                Player player = (Player) target;
                /*

                Cause realistically you're doing this tomorrow:
                    - Allow players to set flyspeed, must be a float. (Between -1,1)
                    - Duration, how long can fly.
                    - Set a default fallback speed!

                */ 
                player.setAllowFlight(true);
                player.setFlying(true);
                player.setFlySpeed(flyspeed);
            }
        }

        return targets.size() > 0;
    }
}
