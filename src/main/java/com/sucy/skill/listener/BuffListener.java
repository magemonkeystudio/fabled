package com.sucy.skill.listener;

import com.sucy.skill.api.event.BuffExpiredEvent;
import com.sucy.skill.api.event.PhysicalDamageEvent;
import com.sucy.skill.api.event.SkillDamageEvent;
import com.sucy.skill.api.event.SkillHealEvent;
import com.sucy.skill.api.util.BuffManager;
import com.sucy.skill.api.util.BuffType;
import com.sucy.skill.hook.PluginChecker;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffectType;

import static com.sucy.skill.listener.attribute.AttributeListener.PHYSICAL;

/**
 * ProSkillAPI © 2023
 * com.sucy.skill.listener.BuffListener
 */
public class BuffListener extends SkillAPIListener {

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
    public void onBuffExpire(final BuffExpiredEvent event){
        if(event.getType().equals(BuffType.INVISIBILITY)){
            if(PluginChecker.isProtocolLibActive())
                PacketListener.updateEquipment((Player) event.getEntity());
        }
    }
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEffectExpire(final EntityPotionEffectEvent event){
        if(!(event.getEntity() instanceof Player) || !event.getModifiedType().equals(PotionEffectType.INVISIBILITY)) return;
        if(event.getAction().equals(EntityPotionEffectEvent.Action.CLEARED) || event.getAction().equals(EntityPotionEffectEvent.Action.REMOVED)){
            BuffManager.getBuffData((LivingEntity) event.getEntity()).clearByType(BuffType.INVISIBILITY);
            if(PluginChecker.isProtocolLibActive()) PacketListener.updateEquipment((Player) event.getEntity());
        }
    }
}
