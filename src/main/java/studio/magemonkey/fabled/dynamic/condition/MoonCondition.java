package studio.magemonkey.fabled.dynamic.condition;

import studio.magemonkey.fabled.dynamic.DynamicSkill;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import org.bukkit.entity.LivingEntity;

import static studio.magemonkey.fabled.dynamic.EffectComponent.TYPE;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A condition for dynamic skills to apply when the moon phase matches.
 */
public class MoonCondition extends ConditionComponent {
    private static final String PHASE = "phase";
    private static final String BLACKLIST = "blacklist";

    private Set<String> phases;
    
    @Override
    public String getKey(){
        return "moon";
    }

    @Override
    public void load(DynamicSkill skill, DataSection config){
        super.load(skill, config);
        phases = settings.getStringList(PHASE).stream()
                .map(s -> s.toUpperCase(Locale.US).replace(' ', '_'))
                .collect(Collectors.toSet());
    }

    /*
     *
     * Minecraft's 8 Moon Phases
     * 
     * 1. Full Moon
     * 2. Waning Gibbous
     * 3. Last Quarter
     * 4. Waning Crescent
     * 5. New Moon
     * 6. Waxing Crescent
     * 7. First Quarter
     * 8. Waxing Gibbous
     * 
     * Formula: phase = (day % 8) + 1
     * 
     */
    @Override
    boolean test(final LivingEntity caster, final int level, final LivingEntity target){
        return settings.getBool(BLACKLIST, false);
    }
}
