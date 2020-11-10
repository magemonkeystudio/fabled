package com.sucy.skill.dynamic.condition;

import com.rit.sucy.config.parse.DataSection;
import com.sucy.skill.dynamic.DynamicSkill;
import org.bukkit.entity.LivingEntity;

import java.util.Set;
import java.util.stream.Collectors;

public class MountingCondition extends ConditionComponent {

    private static final String TYPE = "types";

    private Set<String> types;

    @Override
    public String getKey() {
        return "mounting";
    }

    @Override
    public void load(DynamicSkill skill, DataSection config) {
        super.load(skill, config);
        types = settings.getStringList(TYPE).stream()
                .map(s -> s.toUpperCase().replace(' ', '_'))
                .collect(Collectors.toSet());
    }

    @Override
    boolean test(final LivingEntity caster, final int level, final LivingEntity target) {
        return target.isInsideVehicle() && (types.isEmpty() || types.contains("ANY") || types.contains(target.getVehicle().getType().name()));
    }
}
