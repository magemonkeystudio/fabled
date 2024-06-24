package studio.magemonkey.fabled.dynamic.mechanic;

import org.bukkit.entity.LivingEntity;

import java.util.List;

public class AbortSkillMechanic extends MechanicComponent {

    @Override
    public String getKey() {
        return "abort skill";
    }

    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        skill.stopEffects(caster, level);
        skill.initialize(caster, level);
        return true;
    }
}
