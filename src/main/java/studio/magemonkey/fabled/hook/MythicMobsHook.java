package studio.magemonkey.fabled.hook;

import studio.magemonkey.codex.CodexEngine;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

/**
 * Fabled © 2024
 * studio.magemonkey.fabled.hook.MythicMobsHook
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
