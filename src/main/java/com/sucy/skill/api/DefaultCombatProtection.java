package com.sucy.skill.api;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.hook.NoCheatHook;
import com.sucy.skill.hook.PluginChecker;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * ProSkillAPI © 2023
 * com.sucy.skill.data.DefaultCombatProtection
 */
public class DefaultCombatProtection implements CombatProtection {
    public static final Set<EntityDamageByEntityEvent> fakeDamageEvents = new HashSet<>();

    public static boolean isFakeDamageEvent(EntityDamageByEntityEvent event) {
        return fakeDamageEvents.contains(event);
    }

    @Override
    public boolean canAttack(final LivingEntity attacker,
                             final LivingEntity defender,
                             EntityDamageEvent.DamageCause cause) {
        if (attacker instanceof Player && defender instanceof Player) {
            PlayerClass attackerClass = SkillAPI.getPlayerData(((Player) attacker)).getMainClass();
            PlayerClass defenderClass = SkillAPI.getPlayerData(((Player) defender)).getMainClass();
            int         attackerLevel = attackerClass == null ? 0 : attackerClass.getLevel();
            int         defenderLevel = defenderClass == null ? 0 : defenderClass.getLevel();
            int         minLevel      = SkillAPI.getSettings().getPvpMinLevel();
            if (attackerLevel < minLevel || defenderLevel < minLevel) {
                return false;
            }
            int levelRange = SkillAPI.getSettings().getPvpLevelRange();
            if (levelRange > -1 && Math.abs(attackerLevel - defenderLevel) > levelRange) {
                return false;
            }
        }
        boolean canAttack;
        if (PluginChecker.isNoCheatActive() && attacker instanceof Player) {
            Player player = (Player) attacker;
            NoCheatHook.exempt(player);
            canAttack = CombatProtection.canAttack(attacker, defender);
            NoCheatHook.unexempt(player);
        } else {
            canAttack = CombatProtection.canAttack(attacker, defender, false, cause);
        }

        return canAttack;
    }
}
