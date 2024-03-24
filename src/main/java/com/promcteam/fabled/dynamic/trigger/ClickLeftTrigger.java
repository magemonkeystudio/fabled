package com.promcteam.fabled.dynamic.trigger;

import com.promcteam.fabled.api.Settings;
import com.promcteam.fabled.api.event.KeyPressEvent;

public class ClickLeftTrigger extends ClickTrigger {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "LEFT_CLICK";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldTrigger(KeyPressEvent event, int level, Settings settings) {
        if (event.getKey() == KeyPressEvent.Key.LEFT) {
            return settings.getString("crouch").equalsIgnoreCase("both") ||
                    event.getPlayer().isSneaking() != settings.getString("crouch").equalsIgnoreCase("Dont crouch");
        }
        return false;
    }


}
