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

    @Override
    public boolean execute(final LivingEntity caster, final int level, final List<LivingEntity> targets, boolean force) {
        final int air = Math.max(1, (int) parseValues(caster, AIR, level, 1));

        for (final LivingEntity target : targets) {
            target.setRemainingAir(Math.min(air, target.getMaximumAir()));
        }

        return true;
    }
}
