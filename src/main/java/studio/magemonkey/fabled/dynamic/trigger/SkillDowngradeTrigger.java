package studio.magemonkey.fabled.dynamic.trigger;

import org.bukkit.entity.LivingEntity;
import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.Settings;
import studio.magemonkey.fabled.api.event.PlayerSkillDowngradeEvent;

/**
 * A trigger for when a player levels up a skill.
 */
public class SkillDowngradeTrigger implements Trigger<PlayerSkillDowngradeEvent> {

    @Override
    public String getKey() {
        return "SKILL_DOWNGRADE";
    }

    @Override
    public Class<PlayerSkillDowngradeEvent> getEvent() {
        return PlayerSkillDowngradeEvent.class;
    }

    @Override
    public boolean shouldTrigger(final PlayerSkillDowngradeEvent event, final int level, final Settings settings) {
        // Always trigger when a skill is downgraded
        return true;
    }

    @Override
    public LivingEntity getCaster(final PlayerSkillDowngradeEvent event) {
        return event.getPlayerData().getPlayer();
    }

    @Override
    public LivingEntity getTarget(final PlayerSkillDowngradeEvent event, final Settings settings) {
        return event.getPlayerData().getPlayer();
    }

    @Override
    public void setValues(final PlayerSkillDowngradeEvent event, final CastData data) {
        data.put("api-refund", event.getRefund());
        data.put("api-skill", event.getDowngradedSkill().getData().getName());
        data.put("api-level", event.getDowngradedSkill().getLevel());
    }
}
