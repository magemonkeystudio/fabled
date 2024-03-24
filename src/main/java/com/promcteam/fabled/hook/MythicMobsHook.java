package com.promcteam.fabled.hook;

import com.promcteam.codex.CodexEngine;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

/**
 * Fabled © 2023
 * com.promcteam.fabled.hook.MythicMobsHook
 */
public class MythicMobsHook {

    public static void taunt(final LivingEntity target, final LivingEntity source, final double amount) {
        CodexEngine.get().getMythicMobs().taunt(target, source, amount);
    }

    public static boolean isMonster(final LivingEntity target) {
        return CodexEngine.get().getMythicMobs().isMythicMob(target);
    }

    public static String getMythicMobId(Entity entity) {
        return CodexEngine.get().getMythicMobs().getMythicNameByEntity(entity);
    }
}
