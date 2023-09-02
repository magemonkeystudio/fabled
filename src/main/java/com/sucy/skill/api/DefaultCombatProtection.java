package com.sucy.skill.api;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.hook.NoCheatHook;
import com.sucy.skill.hook.PluginChecker;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

/**
 * ProSkillAPI © 2023
 * com.sucy.skill.data.DefaultCombatProtection
 */
public class DefaultCombatProtection implements CombatProtection {
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

    public static class FakeEntityDamageByEntityEvent extends EntityDamageByEntityEvent {
        @Getter
        @Setter
        public boolean externallyCancelled = false;

        public FakeEntityDamageByEntityEvent(@NotNull Entity damager,
                                             @NotNull Entity damagee,
                                             @NotNull DamageCause cause,
                                             double damage) {
            super(damager, damagee, cause, damage);
        }
    }
}
