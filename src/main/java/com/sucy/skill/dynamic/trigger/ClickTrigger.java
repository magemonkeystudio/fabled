package com.sucy.skill.dynamic.trigger;

import java.util.Map;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerInteractEvent;

import com.sucy.skill.api.Settings;

public abstract class ClickTrigger implements Trigger<PlayerInteractEvent>{

	/** {@inheritDoc} */
	@Override
	public abstract String getKey();

	/** {@inheritDoc} */
	@Override
	public Class<PlayerInteractEvent> getEvent() {
		return PlayerInteractEvent.class;
	}

	/** {@inheritDoc} */
	@Override
	public abstract boolean shouldTrigger(PlayerInteractEvent event, int level, Settings settings);

	/** {@inheritDoc} */
	@Override
	public void setValues(PlayerInteractEvent event, Map<String, Object> data) {}

	/** {@inheritDoc} */
	@Override
	public LivingEntity getCaster(PlayerInteractEvent event) {
		return event.getPlayer();
	}

	/** {@inheritDoc} */
	@Override
	public LivingEntity getTarget(PlayerInteractEvent event, Settings settings) {
		return event.getPlayer();
	}

}
