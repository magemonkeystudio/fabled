package com.sucy.skill.dynamic.trigger;

import java.util.Map;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerDropItemEvent;

import com.sucy.skill.api.Settings;

public class DropItemTrigger implements Trigger<PlayerDropItemEvent>{

	/** {@inheritDoc} */
	@Override
	public String getKey() {
		return "DROP_ITEM";
	}

	/** {@inheritDoc} */
	@Override
	public Class<PlayerDropItemEvent> getEvent() {
		return PlayerDropItemEvent.class;
	}

	/** {@inheritDoc} */
	@Override
	public boolean shouldTrigger(PlayerDropItemEvent event, int level, Settings settings) {
		
		return settings.getString("drop multiple").equalsIgnoreCase("Ignore") || 
				(event.getItemDrop().getItemStack().getAmount() > 1) == settings.getString("drop multiple").equalsIgnoreCase("True");
	}

	/** {@inheritDoc} */
	@Override
	public void setValues(PlayerDropItemEvent event, Map<String, Object> data) {}

	/** {@inheritDoc} */
	@Override
	public LivingEntity getCaster(PlayerDropItemEvent event) {
		return event.getPlayer();
	}

	/** {@inheritDoc} */
	@Override
	public LivingEntity getTarget(PlayerDropItemEvent event, Settings settings) {
		return event.getPlayer();
	}

}
