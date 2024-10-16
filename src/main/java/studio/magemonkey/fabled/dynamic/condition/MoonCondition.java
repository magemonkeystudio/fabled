package studio.magemonkey.fabled.dynamic.condition;

import studio.magemonkey.fabled.dynamic.DynamicSkill;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import org.bukkit.entity.LivingEntity;

import java.util.Set;

/**
 * A condition for dynamic skills to apply when the moon phase matches.
 */
public class MoonCondition extends ConditionComponent {
    private static final String PHASES = "phases";
    private static final String BLACKLIST = "blacklist";

    private Set<String> phases;
    
    @Override
    public String getKey(){
        return "moon";
    }

    @Override
    public void load(DynamicSkill skill, DataSection config){
        super.load(skill, config);
    }

    @Override
    boolean test(final LivingEntity caster, final int level, final LivingEntity target){
        return settings.getBool(BLACKLIST, false);
    }
}
