package com.sucy.skill.api;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Animals;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Tameable;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * ProSkillAPI © 2023
 * com.sucy.skill.api.AllyChecker
 */
public interface CombatProtection {
    static boolean canAttack(LivingEntity attacker, LivingEntity target, boolean passiveAlly) {
        return CombatProtection.canAttack(attacker, target, passiveAlly, EntityDamageEvent.DamageCause.CUSTOM);
    }

    static boolean canAttack(LivingEntity attacker,
                             LivingEntity target,
                             boolean passiveAlly,
                             EntityDamageEvent.DamageCause cause) {
        if (attacker == target) {
            return false;
        } else {
            // If the attacking entity is owned by another player, use that player for logic instead
            if (attacker instanceof Tameable) {
                Tameable entity = (Tameable) target;
                if (entity.isTamed() && entity.getOwner() instanceof OfflinePlayer) {
                    OfflinePlayer owner = (OfflinePlayer) entity.getOwner();
                    if (owner.isOnline()) {
                        attacker = owner.getPlayer();
                    }
                }
            }

            if (target instanceof Tameable) {
                Tameable entity = (Tameable) target;
                if (entity.isTamed() && entity.getOwner() instanceof OfflinePlayer) {
                    OfflinePlayer owner = (OfflinePlayer) entity.getOwner();
                    if (owner.isOnline()) {
                        return canAttack(attacker, owner.getPlayer(), false);
                    }
                }
            } else if (passiveAlly && target instanceof Animals) {
                return false;
            }

            DefaultCombatProtection.FakeEntityDamageByEntityEvent event =
                    new DefaultCombatProtection.FakeEntityDamageByEntityEvent(attacker, target, cause, 0D);
            Bukkit.getPluginManager().callEvent(event);
            boolean attackable = !event.isExternallyCancelled();

            event.setCancelled(true);

            return attackable;
        }
    }

    static boolean canAttack(final LivingEntity attacker, final LivingEntity defender) {
        return canAttack(attacker, defender, false);
    }

    boolean canAttack(final LivingEntity attacker, final LivingEntity defender, EntityDamageEvent.DamageCause cause);

}
