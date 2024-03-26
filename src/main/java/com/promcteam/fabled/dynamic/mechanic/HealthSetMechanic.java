package com.promcteam.fabled.dynamic.mechanic;

import org.bukkit.entity.LivingEntity;

import java.util.List;

/**
 * Fabled © 2024
 * com.promcteam.fabled.dynamic.mechanic.HealthSetMechanic
 */
public class HealthSetMechanic extends MechanicComponent {

    private static final String HEALTH = "health";

    @Override
    public String getKey() {
        return "health set";
    }

    @Override
    public boolean execute(final LivingEntity caster,
                           final int level,
                           final List<LivingEntity> targets,
                           boolean force) {
        final double health = Math.max(1, parseValues(caster, HEALTH, level, 1));

        for (final LivingEntity target : targets) {
            target.setHealth(Math.min(health, target.getMaxHealth()));
        }

        return true;
    }
}
