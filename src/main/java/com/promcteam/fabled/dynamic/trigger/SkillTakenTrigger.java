package com.promcteam.fabled.dynamic.trigger;

import com.promcteam.fabled.api.CastData;
import com.promcteam.fabled.api.Settings;
import com.promcteam.fabled.api.event.SkillDamageEvent;
import org.bukkit.entity.LivingEntity;

/**
 * Fabled © 2024
 * com.promcteam.fabled.dynamic.trigger.BlockBreakTrigger
 */
public class SkillTakenTrigger extends SkillTrigger {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "TOOK_SKILL_DAMAGE";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(final SkillDamageEvent event) {
        return event.getTarget();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(final SkillDamageEvent event, final Settings settings) {
        return isUsingTarget(settings) ? event.getDamager() : event.getTarget();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValues(final SkillDamageEvent event, final CastData data) {
        data.put("api-taken", event.getDamage());
    }
}
