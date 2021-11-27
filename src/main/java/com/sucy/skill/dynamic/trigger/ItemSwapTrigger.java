package com.sucy.skill.dynamic.trigger;

import java.util.Map;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import com.sucy.skill.api.Settings;

public class ItemSwapTrigger implements Trigger<PlayerSwapHandItemsEvent>{

	@Override
	public String getKey() {
		return "ITEM_SWAP";
	}

	@Override
	public Class<PlayerSwapHandItemsEvent> getEvent() {
		return PlayerSwapHandItemsEvent.class;
	}

	@Override
	public boolean shouldTrigger(final PlayerSwapHandItemsEvent event, int level, Settings settings) {
		return true;
	}

	@Override
	public void setValues(final PlayerSwapHandItemsEvent event, final Map<String, Object> data) { }

	@Override
	public LivingEntity getCaster(final PlayerSwapHandItemsEvent event) {
		return event.getPlayer();
	}

	@Override
	public LivingEntity getTarget(PlayerSwapHandItemsEvent event, Settings settings) {
		return event.getPlayer();
	}

}
