package studio.magemonkey.fabled.listener;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import studio.magemonkey.codex.registry.BuffRegistry;
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

    private static double scaleDamage(@Nullable LivingEntity damager, @NotNull LivingEntity target, double damage,
                                      @Nullable String classification) {
        String damageType  = BuffType.DAMAGE.getLocalizedName();
        String defenseType = BuffType.DEFENSE.getLocalizedName();

        if (classification != null && !classification.equalsIgnoreCase(PHYSICAL)) {
            damageType = BuffType.SKILL_DAMAGE.getLocalizedName() + "_" + classification;
            defenseType = BuffType.SKILL_DEFENSE.getLocalizedName() + "_" + classification;
        }

        double withDamageBuffs = damage;
        if (damager != null) withDamageBuffs = BuffRegistry.scaleValue(damageType, damager, damage);
        // With defense buffs
        return BuffRegistry.scaleValue(defenseType, target, withDamageBuffs);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPhysical(final PhysicalDamageEvent event) {
        double scaledDamage = scaleDamage(event.getDamager(), event.getTarget(), event.getDamage(), null);

        if (scaledDamage <= 0) event.setCancelled(true);
        else event.setDamage(scaledDamage);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onSkill(final SkillDamageEvent event) {
        double scaledDamage =
                scaleDamage(event.getDamager(), event.getTarget(), event.getDamage(), event.getClassification());

        if (scaledDamage <= 0) event.setCancelled(true);
        else event.setDamage(scaledDamage);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onHeal(final SkillHealEvent event) {
        final double withBuff = BuffRegistry.scaleValue(
                BuffType.HEALING.getLocalizedName(),
                event.getTarget(),
                event.getAmount());

        event.setAmount(withBuff);
        if (withBuff <= 0) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBuffExpire(final BuffExpiredEvent event) {
        if (event.getKey().equals(BuffType.INVISIBILITY.getLocalizedName())) {
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
