package studio.magemonkey.fabled.dynamic.mechanic;

import org.bukkit.entity.LivingEntity;

import java.util.List;

/**
 * Fabled © 2024
 * studio.magemonkey.fabled.dynamic.mechanic.HealthSetMechanic
 */
public class AirModifyMechanic extends MechanicComponent {

    private static final String AIR = "air";

    @Override
    public String getKey() {
        return "air modify";
    }

    @Override
    public boolean execute(final LivingEntity caster, final int level, final List<LivingEntity> targets, boolean force) {
        final double air = Math.max(1, parseValues(caster, AIR, level, 1));
        final int ticks = (int) (air * 20);

        for (final LivingEntity target : targets) {
            target.setRemainingAir(300);
        }

        return true;
    }
}
