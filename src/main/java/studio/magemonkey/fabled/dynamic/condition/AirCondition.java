package studio.magemonkey.fabled.dynamic.condition;

import org.bukkit.entity.LivingEntity;

public class AirCondition extends ConditionComponent {

    private static final String MIN = "min";
    private static final String MAX = "max";

    // Each Bubble is exactly 1.5 ticks.
    @Override
    boolean test(LivingEntity caster, int level, LivingEntity target) {
        double min      = parseValues(target, MIN, level, settings.getInt(MIN, 0));
        int    minticks = (int) (min * 20);
        double max      = parseValues(target, MAX, level, settings.getInt(MAX, 0));
        int    maxticks = (int) (max * 20);
        return target.getRemainingAir() >= minticks && target.getRemainingAir() <= maxticks;
    }

    @Override
    public String getKey() {
        return "Air";
    }

}
