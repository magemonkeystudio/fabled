package com.sucy.skill.dynamic.trigger;

import java.util.Map;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerFishEvent;

import com.sucy.skill.api.Settings;

public abstract class FishingTrigger implements Trigger<PlayerFishEvent>{

	/** {@inheritDoc} */
	@Override
	public Class<PlayerFishEvent> getEvent(){
		return PlayerFishEvent.class;
	}
	
	/** {@inheritDoc} */
	@Override
	public void setValues(final PlayerFishEvent event, final Map<String, Object> data) { }

	/** {@inheritDoc} */
	@Override
	public LivingEntity getCaster(final PlayerFishEvent event) {
		return event.getPlayer();
	}

	/** {@inheritDoc} */
	@Override
	public LivingEntity getTarget(PlayerFishEvent event, Settings settings) {
		return event.getPlayer();
	}
	
}
