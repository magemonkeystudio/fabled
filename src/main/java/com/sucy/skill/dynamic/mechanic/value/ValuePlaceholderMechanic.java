package com.sucy.skill.dynamic.mechanic.value;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.dynamic.DynamicSkill;
import com.sucy.skill.dynamic.mechanic.MechanicComponent;
import com.sucy.skill.hook.PlaceholderAPIHook;
import com.sucy.skill.hook.PluginChecker;
import com.sucy.skill.log.Logger;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;

/**
 * ProSkillAPI © 2023
 * com.sucy.skill.dynamic.mechanic.value.ValuePlaceholderMechanic
 */
public class ValuePlaceholderMechanic extends MechanicComponent {
    private static final String KEY         = "key";
    private static final String TYPE        = "type";
    private static final String PLACEHOLDER = "placeholder";
    private static final String SAVE   = "save";

    @Override
    public String getKey() {
        return "value placeholder";
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
        final String key         = settings.getString(KEY);
        final String placeholder = filter(caster, targets.get(0), settings.getString(PLACEHOLDER));
        final String type        = settings.getString(TYPE).toUpperCase(Locale.US);

        String value = placeholder;
        if (PluginChecker.isPlaceholderAPIActive() && targets.get(0) instanceof Player) {
            value = PlaceholderAPIHook.format(placeholder, (Player) targets.get(0));
        }

        switch (type.charAt(0)) {
            case 'S': // STRING
                DynamicSkill.getCastData(caster).put(key, value);
                break;
            default: // NUMBER
                try {
                    DynamicSkill.getCastData(caster).put(key, Double.parseDouble(value));
                } catch (final Exception ex) {
                    Logger.invalid(
                            placeholder + " is not a valid numeric placeholder - PlaceholderAPI returned " + value);
                    return false;
                }
        }
        if (settings.getBool(SAVE, false))
            SkillAPI.getPlayerData((OfflinePlayer) caster).setPersistentData(key,DynamicSkill.getCastData(caster).getRaw(key));
        return true;
    }
}
