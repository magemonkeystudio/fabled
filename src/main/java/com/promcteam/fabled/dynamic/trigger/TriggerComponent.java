package com.promcteam.fabled.dynamic.trigger;

import com.promcteam.fabled.dynamic.ComponentType;
import com.promcteam.fabled.dynamic.EffectComponent;
import com.promcteam.fabled.util.Lists;
import org.bukkit.entity.LivingEntity;

import java.util.List;

/**
 * Fabled © 2024
 * com.promcteam.fabled.dynamic.trigger.TriggerComponent
 */
public class TriggerComponent extends EffectComponent {

    private boolean running = false;

    public boolean isRunning() {
        return running;
    }

    public boolean trigger(final LivingEntity caster, final LivingEntity target, final int level) {
        return trigger(caster, target, level, false);
    }

    public boolean trigger(final LivingEntity caster, final LivingEntity target, final int level, boolean force) {
        return execute(caster, level, Lists.asList(target), force);
    }

    @Override
    public String getKey() {
        return "trigger";
    }

    @Override
    public ComponentType getType() {
        return ComponentType.TRIGGER;
    }

    @Override
    public boolean execute(final LivingEntity caster,
                           final int level,
                           final List<LivingEntity> targets,
                           boolean force) {
        try {
            running = true;
            return executeChildren(caster, level, targets, force);
        } finally {
            running = false;
        }
    }
}
