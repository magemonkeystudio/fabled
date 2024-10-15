package studio.magemonkey.fabled.dynamic.mechanic;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import java.util.List;

/**
 * Sets the flight state and flight speed of a player.
 * Does not persist on logout. 
 */
public class FlyMechanic extends MechanicComponent {
    private static final String FLYSPEED = "flyspeed";
    private static final String FLYING = "flying"; 


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
        boolean flying = settings.getString(FLYING, "true").equalsIgnoreCase("false");
        float flyspeed = (float) parseValues(caster, FLYSPEED, level, 0.1);       

        for (LivingEntity target : targets) {
            // Only target players.
            if (target instanceof Player){
                Player player = (Player) target;
                player.setAllowFlight(flying);
                player.setFlying(flying);
                if (flying){
                    // Flightspeed cannot be greater than 1 or less than -1.
                    if (flyspeed > 1){
                        flyspeed = 1.0f;
                    }
                    else if (flyspeed < -1){
                        flyspeed = -1.0f;
                    }
                    player.setFlySpeed(flyspeed);
                }
            }
        }
        return targets.size() > 0;
    }
}