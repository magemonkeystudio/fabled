package studio.magemonkey.fabled.dynamic.trigger;

import studio.magemonkey.fabled.api.Settings;
import org.bukkit.event.player.PlayerFishEvent;

public class FishingFailTrigger extends FishingTrigger {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "FISHING_FAIL";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldTrigger(PlayerFishEvent event, int level, Settings settings) {

        if (event.getState() == PlayerFishEvent.State.FAILED_ATTEMPT) {
            return true;
        }
        return false;
    }

}
