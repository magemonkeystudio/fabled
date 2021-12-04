package com.sucy.skill.dynamic.trigger;

import org.bukkit.event.player.PlayerFishEvent;

import com.sucy.skill.api.Settings;

public class FishingFailTrigger extends FishingTrigger{

	@Override
	public String getKey() {
		return "FISHING_FAIL";
	}

	@Override
	public boolean shouldTrigger(PlayerFishEvent event, int level, Settings settings) {
		
		if(event.getState() == PlayerFishEvent.State.FAILED_ATTEMPT) {
			return true;
		}
		return false;
	}

}
