package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.api.event.SignalEmitEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

import java.util.List;

public class SignalEmitMechanic extends MechanicComponent{
    private static final String SIGNAL = "signal";
    private static final String ARGUMENT = "argument";
    private static final String HANDLER = "handler";

    @Override
    public String getKey() {
        return "signal emit";
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
        String signal = settings.getString(SIGNAL);
        List<String> arguments = settings.getStringList(ARGUMENT);
        boolean selfHandling = settings.getBool(HANDLER);
        targets.forEach(target -> Bukkit.getPluginManager().callEvent(new SignalEmitEvent(skill, caster, target, signal, arguments, selfHandling)));
        return true;
    }
}
