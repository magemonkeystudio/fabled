package com.promcteam.fabled.dynamic.mechanic;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Fabled © 2024
 * com.promcteam.fabled.dynamic.mechanic.FoodMechanic
 */
public class FoodMechanic extends MechanicComponent {
    private static final String FOOD       = "food";
    private static final String SATURATION = "saturation";

    @Override
    public String getKey() {
        return "food";
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
        double food       = parseValues(caster, FOOD, level, 1.0);
        double saturation = parseValues(caster, SATURATION, level, 1.0);
        for (LivingEntity target : targets) {
            if (target instanceof Player) {
                Player player = (Player) target;
                player.setFoodLevel(Math.min(20, Math.max(0, (int) food + player.getFoodLevel())));
                player.setSaturation(Math.min(
                        player.getFoodLevel(),
                        Math.max(0, player.getSaturation() + (float) saturation)));
            }
        }
        return targets.size() > 0;
    }
}
