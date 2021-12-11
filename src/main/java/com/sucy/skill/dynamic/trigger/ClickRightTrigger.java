package com.sucy.skill.dynamic.trigger;

import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.sucy.skill.api.Settings;

public class ClickRightTrigger extends ClickTrigger{

	/** {@inheritDoc} */
	@Override
	public String getKey() {
		return "RIGHT_CLICK";
	}

	/** {@inheritDoc} */
	@Override
	public boolean shouldTrigger(PlayerInteractEvent event, int level, Settings settings) {
		
		if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			return true;
		}
		return false;
	}

}
