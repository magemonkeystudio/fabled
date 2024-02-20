package com.sucy.skill.dynamic.trigger;

import com.sucy.skill.api.CastData;
import com.sucy.skill.api.Settings;
import com.sucy.skill.api.event.SkillDamageEvent;
import org.bukkit.entity.LivingEntity;

import java.util.Map;

/**
 * ProSkillAPI © 2023
 * com.sucy.skill.dynamic.trigger.BlockBreakTrigger
 */
public class SkillDealtTrigger extends SkillTrigger {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "SKILL_DAMAGE";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(final SkillDamageEvent event) {
        return event.getDamager();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(final SkillDamageEvent event, final Settings settings) {
        return isUsingTarget(settings) ? event.getTarget() : event.getDamager();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValues(final SkillDamageEvent event, final CastData data) {
        data.put("api-dealt", event.getDamage());
    }
}
