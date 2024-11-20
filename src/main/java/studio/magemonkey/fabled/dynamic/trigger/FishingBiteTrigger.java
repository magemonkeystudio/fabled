package studio.magemonkey.fabled.dynamic.trigger;

import org.bukkit.event.player.PlayerFishEvent;
import studio.magemonkey.fabled.api.Settings;

public class FishingBiteTrigger extends FishingTrigger {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "FISHING_BITE";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldTrigger(PlayerFishEvent event, int level, Settings settings) {

        if (event.getState() == PlayerFishEvent.State.BITE) {
            return true;
        }
        return false;
    }

}
