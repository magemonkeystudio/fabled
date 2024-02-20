package com.sucy.skill.dynamic.trigger;

import com.sucy.skill.api.CastData;
import com.sucy.skill.api.Settings;
import com.sucy.skill.api.event.PlayerBlockDamageEvent;
import org.bukkit.entity.LivingEntity;

import java.util.Map;

public class ShieldTrigger implements Trigger<PlayerBlockDamageEvent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {return "SHIELD";}

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<PlayerBlockDamageEvent> getEvent() {
        return PlayerBlockDamageEvent.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldTrigger(PlayerBlockDamageEvent event, int level, Settings settings) {
        String type = settings.getString("type", "Both").toLowerCase();
        if (!type.equals("both") && !type.equals(event.getType())) return false;
        double min = settings.getDouble("dmg-min", 0);
        double max = settings.getDouble("dmg-max", 9999);
        return event.getDamage() >= min && event.getDamage() <= max;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValues(PlayerBlockDamageEvent event, final CastData data) {
        data.put("api-blocked", event.getDamage());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(PlayerBlockDamageEvent event) {
        return event.getPlayer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(PlayerBlockDamageEvent event, Settings settings) {
        return settings.getBool("target", false) ? event.getPlayer() : (LivingEntity) event.getSource();
    }
}
