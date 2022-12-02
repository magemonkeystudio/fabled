package com.sucy.skill.dynamic.trigger;

import com.sucy.skill.api.Settings;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

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
    public boolean shouldTrigger(PlayerInteractEvent event, int level, Settings settings) {

        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            return settings.getString("crouch").equalsIgnoreCase("both") ||
                    event.getPlayer().isSneaking() != settings.getString("crouch").equalsIgnoreCase("Dont crouch");
        }
        return false;
    }


}
