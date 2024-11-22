package studio.magemonkey.fabled.dynamic.trigger;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;
import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.Settings;

/**
 * Fabled © 2024
 * studio.magemonkey.fabled.dynamic.trigger.BlockBreakTrigger
 */
public class DeathTrigger implements Trigger<EntityDeathEvent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "DEATH";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<EntityDeathEvent> getEvent() {
        return EntityDeathEvent.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldTrigger(final EntityDeathEvent event, final int level, final Settings settings) {
        return !isTargetingKiller(settings) || event.getEntity().getKiller() != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValues(final EntityDeathEvent event, final CastData data) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(final EntityDeathEvent event) {
        return event.getEntity();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(final EntityDeathEvent event, final Settings settings) {
        return isTargetingKiller(settings) ? event.getEntity().getKiller() : event.getEntity();
    }

    private boolean isTargetingKiller(final Settings settings) {
        return settings.getString("killer", "false").equalsIgnoreCase("true");
    }
}
