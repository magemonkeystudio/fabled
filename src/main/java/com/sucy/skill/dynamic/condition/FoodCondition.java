package com.sucy.skill.dynamic.condition;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class FoodCondition extends ConditionComponent {
    private static final String TYPE = "type";
    private static final String MIN  = "min-value";
    private static final String MAX  = "max-value";

    @Override
    boolean test(final LivingEntity caster, final int level, final LivingEntity target) {
        if (!(target instanceof Player)) {
            return false;
        }

        final String type = settings.getString(TYPE).toLowerCase();
        final double min = parseValues(caster, MIN, level, 0);
        final double max = parseValues(caster, MAX, level, 999);

        double value;
        switch (type) {
            case "difference percent":
                value = (((Player) target).getFoodLevel() - ((Player) caster).getFoodLevel()) * 100 / ((Player) caster).getFoodLevel();
                break;
            case "difference":
                value = ((Player) target).getFoodLevel() - ((Player) caster).getFoodLevel();
                break;
            case "percent":
                value = ((Player) target).getFoodLevel() * 100 / 20;
                break;
            default:
                value = ((Player) target).getFoodLevel();
        }
        return value >= min && value <= max;
    }

    @Override
    public String getKey() {
        return "food";
    }
}
