package studio.magemonkey.fabled.dynamic.mechanic;

import org.bukkit.entity.LivingEntity;

import java.util.List;

/**
 * Fabled © 2024
 * studio.magemonkey.fabled.dynamic.mechanic.HealthSetMechanic
 */
public class AirSetMechanic extends MechanicComponent {

    private static final String AIR = "air";

    @Override
    public String getKey() {
        return "air set";
    }

    // Air values are handled via ticks. Has a maximum value of 300, or 15 ticks of air.
    @Override
    public boolean execute(final LivingEntity caster, final int level, final List<LivingEntity> targets, boolean force) {
        final double air = parseValues(caster, AIR, level, 3.0);
        int ticks = (int) (air * 20);

        for (LivingEntity target : targets) {
            target.setRemainingAir(Math.max(-20, Math.min(ticks, target.getMaximumAir())));
        }

        return targets.size() > 0;
    }
}
