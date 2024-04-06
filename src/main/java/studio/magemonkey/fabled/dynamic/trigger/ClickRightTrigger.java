package studio.magemonkey.fabled.dynamic.trigger;

import studio.magemonkey.fabled.api.Settings;
import studio.magemonkey.fabled.api.event.KeyPressEvent;

public class ClickRightTrigger extends ClickTrigger {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "RIGHT_CLICK";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldTrigger(KeyPressEvent event, int level, Settings settings) {
        if (event.getKey() == KeyPressEvent.Key.RIGHT) {
            return settings.getString("crouch").equalsIgnoreCase("both") ||
                    event.getPlayer().isSneaking() != settings.getString("crouch").equalsIgnoreCase("Dont crouch");
        }
        return false;
    }

}
