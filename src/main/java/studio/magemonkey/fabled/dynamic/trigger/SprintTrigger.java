package studio.magemonkey.fabled.dynamic.trigger;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.Settings;

/**
 * © 2026 VoidEdge
 * studio.magemonkey.fabled.dynamic.trigger.BlockBreakTrigger
 */
public class SprintTrigger implements Trigger<PlayerToggleSprintEvent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "SPRINT";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<PlayerToggleSprintEvent> getEvent() {
        return PlayerToggleSprintEvent.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldTrigger(final PlayerToggleSprintEvent event, final int level, final Settings settings) {
        final String type = settings.getString("type", "start sprinting");
        return type.equalsIgnoreCase("both") || event.isSprinting() != type.equalsIgnoreCase("stop sprinting");
    }

    @Override
    public void setValues(PlayerToggleSprintEvent event, final CastData data) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(final PlayerToggleSprintEvent event) {
        return event.getPlayer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(final PlayerToggleSprintEvent event, final Settings settings) {
        return event.getPlayer();
    }
}
