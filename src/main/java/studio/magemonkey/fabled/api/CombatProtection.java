package studio.magemonkey.fabled.api;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Tameable;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import studio.magemonkey.codex.api.VersionManager;
import studio.magemonkey.fabled.Fabled;

/**
 * Fabled © 2024
 * studio.magemonkey.fabled.api.AllyChecker
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

            return canAttackExternally(attacker, target, cause);
        }
    }

    static boolean canAttack(final LivingEntity attacker, final LivingEntity defender) {
        return canAttack(attacker, defender, false);
    }

    static boolean canAttackExternally(@NotNull Entity damager,
                                       @NotNull Entity entity,
                                       @NotNull EntityDamageEvent.DamageCause cause) {
        EntityDamageByEntityEvent event = VersionManager.getNms().createEntityDamageEvent(damager, entity, cause, 5);

        DefaultCombatProtection.fakeDamageEvents.add(event);
        boolean externallyCancelled = false;
        try {
            Bukkit.getPluginManager().callEvent(event);
            externallyCancelled = DefaultCombatProtection.isExternallyCancelled(event);
        } catch (Exception e) {
            Fabled.inst().getLogger().warning("Failed to process EntityDamageByEntityEvent");
            e.printStackTrace();
        } finally {
            DefaultCombatProtection.fakeDamageEvents.remove(event);
            DefaultCombatProtection.externallyCancelled.remove(event);
        }

        return !externallyCancelled;
    }

    boolean canAttack(final LivingEntity attacker, final LivingEntity defender, EntityDamageEvent.DamageCause cause);

}
