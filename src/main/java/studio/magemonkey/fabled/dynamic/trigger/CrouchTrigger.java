package studio.magemonkey.fabled.dynamic.trigger;

import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.Settings;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerToggleSneakEvent;

/**
 * Fabled © 2024
 * studio.magemonkey.fabled.dynamic.trigger.BlockBreakTrigger
 */
public class CrouchTrigger implements Trigger<PlayerToggleSneakEvent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "CROUCH";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<PlayerToggleSneakEvent> getEvent() {
        return PlayerToggleSneakEvent.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldTrigger(final PlayerToggleSneakEvent event, final int level, final Settings settings) {
        final String type = settings.getString("type", "start crouching");
        return type.equalsIgnoreCase("both") || event.isSneaking() != type.equalsIgnoreCase("stop crouching");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValues(final PlayerToggleSneakEvent event, final CastData data) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(final PlayerToggleSneakEvent event) {
        return event.getPlayer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(final PlayerToggleSneakEvent event, final Settings settings) {
        return event.getPlayer();
    }
}
