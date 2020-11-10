package com.sucy.skill.dynamic.condition;

import com.rit.sucy.config.parse.DataSection;
import com.sucy.skill.dynamic.DynamicSkill;
import org.bukkit.entity.LivingEntity;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class MountedCondition extends ConditionComponent {

    private static final String TYPE = "types";

    private Set<String> types;

    @Override
    public String getKey() {
        return "mounted";
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
        Set<String> passengers = target.getPassengers().stream()
                .map(e -> e.getType().name())
                .collect(Collectors.toSet());
        return !passengers.isEmpty() && (types.isEmpty() || types.contains("ANY") || !Collections.disjoint(passengers,types));
    }
}
