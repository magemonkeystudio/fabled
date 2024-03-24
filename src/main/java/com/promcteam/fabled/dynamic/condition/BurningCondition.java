package com.promcteam.fabled.dynamic.condition;

import com.promcteam.fabled.dynamic.DynamicSkill;
import com.promcteam.codex.mccore.config.parse.DataSection;
import org.bukkit.entity.LivingEntity;

public class BurningCondition extends ConditionComponent {

    private static final String BURN = "burn";

    private String burn;

    @Override
    boolean test(LivingEntity caster, int level, LivingEntity target) {

        return (caster.getFireTicks() > 0) == burn.equalsIgnoreCase("Burn");
    }

    @Override
    public void load(DynamicSkill skill, DataSection config) {
        super.load(skill, config);

        burn = settings.getString(BURN);
    }

    @Override
    public String getKey() {
        return "Burning";
    }

}
