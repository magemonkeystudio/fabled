package com.sucy.skill.dynamic.trigger;

import com.sucy.skill.api.Settings;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import java.util.List;
import java.util.Map;

public class ConsumeTrigger implements Trigger<PlayerItemConsumeEvent>{

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "CONSUME";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<PlayerItemConsumeEvent> getEvent() {return PlayerItemConsumeEvent.class;}

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldTrigger(PlayerItemConsumeEvent event, int level, Settings settings) {
        final String item_name = event.getItem().getType().name();
        final String req = settings.getString("material", "Any").replace(" ","_");
        return req.equalsIgnoreCase(item_name) || req.equalsIgnoreCase("Any");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValues(PlayerItemConsumeEvent event, Map<String, Object> data) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(PlayerItemConsumeEvent event) {return event.getPlayer();}

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(PlayerItemConsumeEvent event, Settings settings) {return event.getPlayer();}
}
