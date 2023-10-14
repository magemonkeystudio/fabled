package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.dynamic.DynamicSkill;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

/**
 * ProSkillAPI © 2023
 * com.sucy.skill.dynamic.mechanic.ValueDistanceMechanic
 */
public class ValueDistanceMechanic extends MechanicComponent {
    private static final String KEY = "key";
    private static final String SAVE   = "save";

    @Override
    public String getKey() {
        return "value distance";
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
    public boolean execute(final LivingEntity caster,
                           final int level,
                           final List<LivingEntity> targets,
                           boolean force) {
        if (!settings.has(KEY) || !(caster instanceof Player)) {
            return false;
        }

        final String              key  = settings.getString(KEY);
        final Map<String, Object> data = DynamicSkill.getCastData(caster);
        data.put(key, targets.get(0).getLocation().distance(caster.getLocation()));
        if (settings.getBool(SAVE, false))
            SkillAPI.getPlayerData((OfflinePlayer) caster).setPersistentData(key,data.get(key));
        return true;
    }
}
