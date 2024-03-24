package com.promcteam.fabled.dynamic.condition;

import org.bukkit.entity.LivingEntity;

public class AltitudeCondition extends ConditionComponent {

    private static final String MIN = "min";
    private static final String MAX = "max";

    @Override
    boolean test(LivingEntity caster, int level, LivingEntity target) {
        double min = parseValues(target, MIN, level, settings.getInt(MIN, 0));
        double max = parseValues(target, MAX, level, settings.getInt(MAX, 0));
        return target.getLocation().getY() >= min && target.getLocation().getY() <= max;
    }

    @Override
    public String getKey() {
        return "Altitude";
    }

}
