package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.api.event.SignalEmitEvent;
import com.sucy.skill.dynamic.DynamicSkill;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

import java.util.List;
import java.util.stream.Collectors;

public class SignalEmitMechanic extends MechanicComponent {
    private static final String SIGNAL   = "signal";
    private static final String ARGUMENT = "argument";
    private static final String HANDLER  = "handler";

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
        String       signal       = settings.getString(SIGNAL);
        List<String> arguments    = settings.getStringList(ARGUMENT);
        boolean      selfHandling = settings.getBool(HANDLER);
        targets.forEach(target -> {
            List<Object> filtered = arguments.parallelStream().map(arg -> {
                Object value = DynamicSkill.getCastData(caster).getRaw(arg);
                return value == null ? filter(caster, target, arg) : value;
            }).collect(Collectors.toList());
            Bukkit.getPluginManager()
                    .callEvent(new SignalEmitEvent(skill, caster, target, signal, filtered, selfHandling));
        });
        return true;
    }
}
