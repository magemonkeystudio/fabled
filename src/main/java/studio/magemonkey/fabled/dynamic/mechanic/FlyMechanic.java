package studio.magemonkey.fabled.dynamic.mechanic;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Allows target to fly 
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
        double flyspeed = parseValues(caster, FLYSPEED, level, 5);
        double seconds = parseValues(caster, SECONDS, level, 3.0);
        
        for (LivingEntity target : targets) {
            if (target instanceof Player){
                Player player = (Player) target;
                player.setAllowFlight(true);
                player.setFlying(true);
            }
        }

        return targets.size() > 0;
    }
}
