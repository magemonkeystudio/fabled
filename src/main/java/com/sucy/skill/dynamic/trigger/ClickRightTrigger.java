package com.sucy.skill.dynamic.trigger;

import com.sucy.skill.api.Settings;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

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
    public boolean shouldTrigger(PlayerInteractEvent event, int level, Settings settings) {
        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                && event.getHand() == EquipmentSlot.HAND) {
            return settings.getString("crouch").equalsIgnoreCase("both") ||
                    event.getPlayer().isSneaking() != settings.getString("crouch").equalsIgnoreCase("Dont crouch");
        }
        return false;
    }

}
