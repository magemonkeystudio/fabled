package studio.magemonkey.fabled.dynamic.condition;

import org.bukkit.entity.LivingEntity;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import studio.magemonkey.fabled.dynamic.DynamicSkill;

import java.util.Set;
import java.util.stream.Collectors;

public class WorldCondition extends ConditionComponent {

    private static final String WORLDS    = "worlds";
    private static final String BLACKLIST = "blacklist";

    private Set<String> worlds;
    private boolean     blacklist;

    @Override
    boolean test(LivingEntity caster, int level, LivingEntity target) {
        return worlds.contains(caster.getWorld().getName()) != blacklist;
    }

    @Override
    public void load(DynamicSkill skill, DataSection config) {
        super.load(skill, config);

        blacklist = settings.getString(BLACKLIST).equalsIgnoreCase("True");
        worlds = settings.getStringList(WORLDS).stream().collect(Collectors.toSet());
    }

    @Override
    public String getKey() {
        return "World";
    }

}
