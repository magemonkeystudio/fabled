package com.sucy.skill.api;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

/**
 * SkillAPI © 2018
 * com.sucy.skill.api.AllyChecker
 */
public interface CombatProtection {
    boolean canAttack(final Player attacker, final Player defender);
    boolean canAttack(final Player attacker, final LivingEntity defender);
    boolean canAttack(final LivingEntity attacker, final LivingEntity defender);

    static boolean canAttack(LivingEntity attacker, LivingEntity target, boolean passiveAlly) {
        if (attacker == target) {
            return false;
        } else {
            if (target instanceof Tameable) {
                Tameable entity = (Tameable)target;
                if (entity.isTamed() && entity.getOwner() instanceof OfflinePlayer) {
                    OfflinePlayer owner = (OfflinePlayer)entity.getOwner();
                    if (owner.isOnline()) {
                        return canAttack(attacker, owner.getPlayer(), false);
                    }
                }
            } else if (passiveAlly && target instanceof Animals) {
                return false;
            }

            DefaultCombatProtection.FakeEntityDamageByEntityEvent event = new DefaultCombatProtection.FakeEntityDamageByEntityEvent(attacker, target, EntityDamageEvent.DamageCause.CUSTOM, 1.0D);
            Bukkit.getPluginManager().callEvent(event);
            return !event.isCancelled();
        }
    }

}
