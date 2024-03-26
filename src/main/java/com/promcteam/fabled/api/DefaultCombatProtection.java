package com.promcteam.fabled.api;

import com.promcteam.fabled.Fabled;
import com.promcteam.fabled.api.player.PlayerClass;
import com.promcteam.fabled.hook.NoCheatHook;
import com.promcteam.fabled.hook.PluginChecker;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Fabled © 2024
 * com.promcteam.fabled.data.DefaultCombatProtection
 */
public class DefaultCombatProtection implements CombatProtection {
    public static final Set<EntityDamageByEntityEvent>          fakeDamageEvents    = new HashSet<>();
    public static final Map<EntityDamageByEntityEvent, Boolean> externallyCancelled = new HashMap<>();

    public static boolean isFakeDamageEvent(EntityDamageByEntityEvent event) {
        return fakeDamageEvents.contains(event);
    }

    public static boolean isExternallyCancelled(EntityDamageByEntityEvent event) {
        return externallyCancelled.getOrDefault(event, false);
    }

    @Override
    public boolean canAttack(final LivingEntity attacker,
                             final LivingEntity defender,
                             EntityDamageEvent.DamageCause cause) {
        if (attacker instanceof Player && defender instanceof Player) {
            PlayerClass attackerClass = Fabled.getPlayerData(((Player) attacker)).getMainClass();
            PlayerClass defenderClass = Fabled.getPlayerData(((Player) defender)).getMainClass();
            int         attackerLevel = attackerClass == null ? 0 : attackerClass.getLevel();
            int         defenderLevel = defenderClass == null ? 0 : defenderClass.getLevel();
            int         minLevel      = Fabled.getSettings().getPvpMinLevel();
            if (attackerLevel < minLevel || defenderLevel < minLevel) {
                return false;
            }
            int levelRange = Fabled.getSettings().getPvpLevelRange();
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
