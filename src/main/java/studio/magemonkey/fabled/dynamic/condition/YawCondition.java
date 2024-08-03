package studio.magemonkey.fabled.dynamic.condition;

import org.bukkit.entity.LivingEntity;

public class YawCondition extends ConditionComponent {
    private static String MIN_YAW = "min-yaw";
    private static String MAX_YAW = "max-yaw";

    @Override
    boolean test(LivingEntity caster, int level, LivingEntity target) {
        double yaw = target.getLocation().getYaw();
        if (yaw < 0) {
            yaw += 360;
        }
        double min = settings.getDouble(MIN_YAW, 0);
        double max = settings.getDouble(MAX_YAW, 60);
        if (min < max) {
            return yaw >= min && yaw <= max;
        } else {
            return yaw >= min || yaw <= max;
        }
    }

    @Override
    public String getKey() {
        return "Yaw";
    }

}
