package studio.magemonkey.fabled.dynamic.mechanic;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Allows target to fly 
 */
public class FlyMechanic extends MechanicComponent{
    // public static final String FLYSPEED = "flyspeed";
    // public static final String SECONDS = "duration";

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
        if (!(caster instanceof Player)) {
            return false;
        }

        cleanUp(caster);

        final Player player = (Player) caster;

        player.setFlying(true);

        return true;
    }
}
