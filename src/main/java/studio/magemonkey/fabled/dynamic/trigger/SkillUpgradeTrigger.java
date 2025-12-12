package studio.magemonkey.fabled.dynamic.trigger;

import org.bukkit.entity.LivingEntity;
import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.event.PlayerSkillUpgradeEvent;
import studio.magemonkey.fabled.api.Settings;

/**
 * A trigger for when a player levels up a skill.
 */
public class SkillUpgradeTrigger implements Trigger<PlayerSkillUpgradeEvent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "SKILL_UPGRADE";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<PlayerSkillUpgradeEvent> getEvent() {
        return PlayerSkillUpgradeEvent.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldTrigger(final PlayerSkillUpgradeEvent event, final int level, final Settings settings) {
        // Always trigger when a skill is upgraded
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(final PlayerSkillUpgradeEvent event) {
        return event.getPlayerData().getPlayer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(final PlayerSkillUpgradeEvent event, final Settings settings) {
        return event.getPlayerData().getPlayer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValues(final PlayerSkillUpgradeEvent event, final CastData data) {
        data.put("api-cost", event.getCost());
        data.put("api-skill", event.getUpgradedSkill().getData().getName());
        data.put("api-level", event.getUpgradedSkill().getLevel());
    }
}
