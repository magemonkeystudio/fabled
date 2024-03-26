package com.promcteam.fabled.dynamic.trigger;

import com.promcteam.fabled.api.CastData;
import com.promcteam.fabled.api.DefaultCombatProtection;
import com.promcteam.fabled.api.Settings;
import com.promcteam.fabled.dynamic.DynamicSkill;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Fabled © 2024
 * com.promcteam.fabled.dynamic.trigger.BlockBreakTrigger
 */
public class EnvironmentalTrigger implements Trigger<EntityDamageEvent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "ENVIRONMENT_DAMAGE";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<EntityDamageEvent> getEvent() {
        return EntityDamageEvent.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldTrigger(final EntityDamageEvent event, final int level, final Settings settings) {
        if (event instanceof EntityDamageByEntityEvent
                && DefaultCombatProtection.isFakeDamageEvent((EntityDamageByEntityEvent) event)) {
            return false;
        }

        List<String> types = settings.getStringList("type").stream()
                .map(str -> str.replace(' ', '_').toUpperCase(Locale.US))
                .collect(Collectors.toList());

        boolean isAny = types.contains("ANY");

        return isAny || types.contains(event.getCause().name());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValues(final EntityDamageEvent event, final CastData data) {
        data.put("api-taken", event.getDamage());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(final EntityDamageEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            return (LivingEntity) event.getEntity();
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(final EntityDamageEvent event, final Settings settings) {
        return getCaster(event);
    }

    /**
     * Handles applying other effects after the skill resolves
     *
     * @param event event details
     * @param skill skill to resolve
     */
    @Override
    public void postProcess(final EntityDamageEvent event, final DynamicSkill skill) {
        final double damage = skill.applyImmediateBuff(event.getDamage());
        event.setDamage(damage);
    }
}
