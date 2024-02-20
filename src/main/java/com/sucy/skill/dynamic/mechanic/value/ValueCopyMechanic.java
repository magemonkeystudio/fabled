package com.sucy.skill.dynamic.mechanic.value;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.dynamic.DynamicSkill;
import com.sucy.skill.dynamic.mechanic.MechanicComponent;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;

import java.util.List;

/**
 * ProSkillAPI © 2023
 * com.sucy.skill.dynamic.mechanic.value.ValueCopyMechanic
 */
public class ValueCopyMechanic extends MechanicComponent {
    private static final String KEY       = "key";
    private static final String TARGET    = "destination";
    private static final String TO_TARGET = "to-target";
    private static final String SAVE      = "save";

    @Override
    public String getKey() {
        return "value copy";
    }

    @Override
    public boolean execute(
            final LivingEntity caster, final int level, final List<LivingEntity> targets, boolean force) {

        if (targets.size() == 0 || !settings.has(KEY)) {
            return false;
        }

        final String  key         = settings.getString(KEY);
        final String  destination = settings.getString(TARGET, key);
        final boolean toTarget    = settings.getString(TO_TARGET, "true").equalsIgnoreCase("true");

        if (toTarget) {
            targets.forEach(target -> apply(caster, target, key, destination));
        } else {
            apply(targets.get(0), caster, key, destination);
        }

        if (settings.getBool(SAVE, false))
            SkillAPI.getPlayerData((OfflinePlayer) caster)
                    .setPersistentData(key, DynamicSkill.getCastData(caster).getRaw(key));
        return true;
    }

    private boolean apply(final LivingEntity from, final LivingEntity to, final String key, final String destination) {
        final Object value = DynamicSkill.getCastData(from).getRaw(key);
        if (value == null) return false;
        DynamicSkill.getCastData(to).put(destination, value);
        return true;
    }
}
