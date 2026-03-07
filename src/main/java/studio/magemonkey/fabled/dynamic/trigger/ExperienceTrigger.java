package studio.magemonkey.fabled.dynamic.trigger;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerExpChangeEvent;
import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.Settings;

/**
 * © 2026 VoidEdge
 * studio.magemonkey.fabled.dynamic.trigger.BlockBreakTrigger
 */
public class ExperienceTrigger implements Trigger<PlayerExpChangeEvent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "EXPERIENCE";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<PlayerExpChangeEvent> getEvent() {
        return PlayerExpChangeEvent.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldTrigger(final PlayerExpChangeEvent event, final int level, final Settings settings) {
        final double minExperience = settings.getDouble("min-experience", 0);
        return event.getAmount() >= minExperience;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValues(final PlayerExpChangeEvent event, final CastData data) {
        final int experience = event.getAmount();
        data.put("api-experience", experience);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(final PlayerExpChangeEvent event) {
        return event.getPlayer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(final PlayerExpChangeEvent event, final Settings settings) {
        return event.getPlayer();
    }
}
