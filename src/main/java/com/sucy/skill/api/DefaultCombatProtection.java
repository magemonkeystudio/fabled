package com.sucy.skill.api;

import com.sucy.skill.hook.NoCheatHook;
import com.sucy.skill.hook.PluginChecker;
import lombok.Getter;
import lombok.Setter;
import mc.promcteam.engine.mccore.util.Protection;
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
    public boolean canAttack(final Player attacker, final Player defender) {
        return canAttack(attacker, defender, EntityDamageEvent.DamageCause.CUSTOM);
    }

    @Override
    public boolean canAttack(final Player attacker, final LivingEntity defender) {
        return canAttack(attacker, defender, EntityDamageEvent.DamageCause.CUSTOM);
    }

    @Override
    public boolean canAttack(final LivingEntity attacker, final LivingEntity defender) {
        return canAttack(attacker, defender, EntityDamageEvent.DamageCause.CUSTOM);
    }

    @Override
    public boolean canAttack(final Player attacker, final Player defender, EntityDamageEvent.DamageCause cause) {
        return canAttack((LivingEntity) attacker, defender, cause);
    }

    @Override
    public boolean canAttack(final Player attacker, final LivingEntity defender, EntityDamageEvent.DamageCause cause) {
        return canAttack((LivingEntity) attacker, defender, cause);
    }

    @Override
    public boolean canAttack(final LivingEntity attacker, final LivingEntity defender, EntityDamageEvent.DamageCause cause) {
        boolean canAttack;
        if (PluginChecker.isNoCheatActive() && attacker instanceof Player) {
            Player player = (Player) attacker;
            NoCheatHook.exempt(player);
            canAttack = Protection.canAttack(attacker, defender);
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

        public FakeEntityDamageByEntityEvent(@NotNull Entity damager, @NotNull Entity damagee, @NotNull DamageCause cause, double damage) {
            super(damager, damagee, cause, damage);
        }
    }
}
