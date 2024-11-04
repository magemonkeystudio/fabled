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
        final double air = parseValues(caster, AIR, level, 1);
        int ticks = (int) (air * 20);

        for (final LivingEntity target : targets) {
            final int airValue = Math.max(-20, Math.min(ticks, target.getMaximumAir())); // Bound by -20 and MaximumAir of Target
            target.setRemainingAir(airValue);
        }

        return true;
    }
}
