package studio.magemonkey.fabled.dynamic.trigger;

import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.Settings;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerFishEvent;

public abstract class FishingTrigger implements Trigger<PlayerFishEvent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<PlayerFishEvent> getEvent() {
        return PlayerFishEvent.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValues(final PlayerFishEvent event, final CastData data) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(final PlayerFishEvent event) {
        return event.getPlayer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(PlayerFishEvent event, Settings settings) {
        return event.getPlayer();
    }

}
