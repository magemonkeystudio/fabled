package studio.magemonkey.fabled.dynamic.trigger;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerRiptideEvent;
import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.Settings;

/**
 * © 2026 VoidEdge
 * studio.magemonkey.fabled.dynamic.trigger.BlockBreakTrigger
 */
public class RiptideTrigger implements Trigger<PlayerRiptideEvent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "RIPTIDE";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<PlayerRiptideEvent> getEvent() {
        return PlayerRiptideEvent.class;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldTrigger(final PlayerRiptideEvent event, final int level, final Settings settings) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValues(PlayerRiptideEvent event, final CastData data) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(final PlayerRiptideEvent event) {
        return (LivingEntity) event.getPlayer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(final PlayerRiptideEvent event, final Settings settings) {
        return (LivingEntity) event.getPlayer();
    }
}
