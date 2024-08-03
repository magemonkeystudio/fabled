package studio.magemonkey.fabled.dynamic.condition;

import studio.magemonkey.fabled.dynamic.DynamicSkill;
import studio.magemonkey.fabled.hook.MythicMobsHook;
import studio.magemonkey.fabled.hook.PluginChecker;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import org.bukkit.entity.LivingEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MythicMobTypeCondition extends ConditionComponent {

    private static final String TYPES = "types";

    private Set<String> types;

    @Override
    public void load(DynamicSkill skill, DataSection config) {
        super.load(skill, config);
        types = new HashSet<>();
        List<String> stringList = settings.getStringList(TYPES);
        if (stringList.stream().anyMatch(str -> str.equalsIgnoreCase("any"))) {
            types.add("any");
        } else {
            for (String string : stringList) {
                if (!string.isEmpty()) {
                    types.add(string);
                }
            }
        }
    }

    @Override
    public String getKey() {return "mythicmob type";}

    @Override
    boolean test(LivingEntity caster, int level, LivingEntity target) {
        if (!PluginChecker.isMythicMobsActive()) {
            return false;
        }
        if (MythicMobsHook.isMonster(target)) {
            if (types.contains("any")) {
                return true;
            }
            String type = MythicMobsHook.getMythicMobId(target);
            for (String string : types) {
                if (string.equals(type)) {
                    return true;
                }
            }
            return false;
        } else {
            return types.isEmpty();
        }
    }
}
