package studio.magemonkey.fabled.listener;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffectType;
import studio.magemonkey.fabled.api.event.BuffExpiredEvent;
import studio.magemonkey.fabled.api.event.PhysicalDamageEvent;
import studio.magemonkey.fabled.api.event.SkillDamageEvent;
import studio.magemonkey.fabled.api.event.SkillHealEvent;
import studio.magemonkey.fabled.api.util.BuffManager;
import studio.magemonkey.fabled.api.util.BuffType;
import studio.magemonkey.fabled.hook.PluginChecker;

import static studio.magemonkey.fabled.listener.attribute.AttributeListener.PHYSICAL;

/**
 * Fabled © 2024
 * studio.magemonkey.fabled.listener.BuffListener
 */
public class BuffListener extends FabledListener {

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPhysical(final PhysicalDamageEvent event) {
        final double withDamageBuffs = BuffManager.apply(
                event.getDamager(),
                BuffType.DAMAGE,
                event.getDamage());
        final double withDefenseBuffs = BuffManager.apply(
                event.getTarget(),
                BuffType.DEFENSE,
                withDamageBuffs);

        event.setDamage(withDefenseBuffs);
        if (withDefenseBuffs <= 0) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onSkill(final SkillDamageEvent event) {
        if (event.getClassification().equalsIgnoreCase(PHYSICAL)) {
            final double withDamageBuffs = BuffManager.apply(
                    event.getDamager(),
                    BuffType.DAMAGE,
                    event.getDamage());
            final double withDefenseBuffs = BuffManager.apply(
                    event.getTarget(),
                    BuffType.DEFENSE,
                    withDamageBuffs);

            event.setDamage(withDefenseBuffs);
            if (withDefenseBuffs <= 0) {
                event.setCancelled(true);
            }
        } else {
            final double withDamageBuffs = BuffManager.apply(
                    event.getDamager(),
                    BuffType.SKILL_DAMAGE,
                    event.getClassification(),
                    event.getDamage());
            final double withDefenseBuffs = BuffManager.apply(
                    event.getTarget(),
                    BuffType.SKILL_DEFENSE,
                    event.getClassification(),
                    withDamageBuffs);

            event.setDamage(withDefenseBuffs);
            if (withDefenseBuffs <= 0) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onHeal(final SkillHealEvent event) {
        final double withBuff = BuffManager.apply(
                event.getTarget(),
                BuffType.HEALING,
                event.getAmount());

        event.setAmount(withBuff);
        if (withBuff <= 0) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBuffExpire(final BuffExpiredEvent event) {
        if (event.getType().equals(BuffType.INVISIBILITY)) {
            if (PluginChecker.isProtocolLibActive())
                PacketListener.updateEquipment((Player) event.getEntity());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEffectExpire(final EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player) || !event.getModifiedType().equals(PotionEffectType.INVISIBILITY))
            return;
        if (event.getAction().equals(EntityPotionEffectEvent.Action.CLEARED) || event.getAction()
                .equals(EntityPotionEffectEvent.Action.REMOVED)) {
            BuffManager.getBuffData((LivingEntity) event.getEntity()).clearByType(BuffType.INVISIBILITY);
            if (PluginChecker.isProtocolLibActive()) PacketListener.updateEquipment((Player) event.getEntity());
        }
    }
}
