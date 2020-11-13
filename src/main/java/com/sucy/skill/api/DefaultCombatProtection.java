package com.sucy.skill.api;

import com.rit.sucy.player.Protection;
import com.sucy.skill.hook.NoCheatHook;
import com.sucy.skill.hook.PluginChecker;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

/**
 * SkillAPI © 2018
 * com.sucy.skill.data.DefaultCombatProtection
 */
public class DefaultCombatProtection implements CombatProtection {
    @Override
    public boolean canAttack(final Player attacker, final Player defender) {
        return canAttack((LivingEntity) attacker, defender);
    }

    @Override
    public boolean canAttack(final Player attacker, final LivingEntity defender) {
        return canAttack((LivingEntity) attacker, defender);
    }

    @Override
    public boolean canAttack(final LivingEntity attacker, final LivingEntity defender) {
        boolean canAttack;
        if (PluginChecker.isNoCheatActive() && attacker instanceof Player) {
            Player player = (Player) attacker;
            NoCheatHook.exempt(player);
            canAttack = Protection.canAttack(attacker, defender);
            NoCheatHook.unexempt(player);
        } else { canAttack = CombatProtection.canAttack(attacker, defender, false); }

        return canAttack;
    }

    public static class FakeEntityDamageByEntityEvent extends EntityDamageByEntityEvent {

        public FakeEntityDamageByEntityEvent(@NotNull Entity damager, @NotNull Entity damagee, @NotNull DamageCause cause, double damage) {
            super(damager, damagee, cause, damage);
        }
    }
}
