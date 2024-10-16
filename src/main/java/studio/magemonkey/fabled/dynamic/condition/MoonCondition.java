package studio.magemonkey.fabled.dynamic.condition;

import studio.magemonkey.fabled.dynamic.DynamicSkill;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.World;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A condition for dynamic skills to apply when the moon phase matches.
 */
public class MoonCondition extends ConditionComponent {
    private static final String PHASES = "phases";
    private static final String BLACKLIST = "blacklist";

    private Set<String> phases;
    private boolean     blacklist;
    
    // Array containing all Minecraft Moon Phases
    // Can be found here: https://minecraft.fandom.com/wiki/Moon#Effects_on_mobs
    private final String[] phaseNames = {
            "FULL_MOON", 
            "WANING_GIBBOUS", 
            "LAST_QUARTER",
            "WANING_CRESCENT",
            "NEW_MOON",
            "WAXING_CRESCENT",
            "FIRST_QUARTER",
            "WAXING_GIBBOUS"
        };
    
    @Override
    public String getKey(){
        return "moon";
    }

    @Override
    public void load(DynamicSkill skill, DataSection config){
        super.load(skill, config);
        phases = settings.getStringList(PHASES).stream()
                .map(s -> s.toUpperCase(Locale.US).replace(' ', '_'))
                .collect(Collectors.toSet());
        blacklist = settings.getString(BLACKLIST).equalsIgnoreCase("True");
    }

    /*
     *
     * Minecraft's 8 Moon Phases
     * 
     * 0. Full Moon
     * 1. Waning Gibbous
     * 2. Last Quarter
     * 3. Waning Crescent
     * 4. New Moon
     * 5. Waxing Crescent
     * 6. First Quarter
     * 7. Waxing Gibbous
     * 
     * Formula: phase = (day % 8)
     * 
     */
    @Override
    boolean test(final LivingEntity caster, final int level, final LivingEntity target){
        final World world = target.getWorld();
        final int day = ((int) (world.getFullTime() / 24000L) % 8 );
        final String currentPhase = phaseNames[day];
        return phases.contains(currentPhase) != blacklist;
    }
}
