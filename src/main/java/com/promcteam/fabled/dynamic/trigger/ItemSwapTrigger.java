package com.promcteam.fabled.dynamic.trigger;

import com.promcteam.fabled.api.CastData;
import com.promcteam.fabled.api.Settings;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class ItemSwapTrigger implements Trigger<PlayerSwapHandItemsEvent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "ITEM_SWAP";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<PlayerSwapHandItemsEvent> getEvent() {
        return PlayerSwapHandItemsEvent.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldTrigger(final PlayerSwapHandItemsEvent event, int level, Settings settings) {

        boolean cancelEvent = settings.getBool("cancel");
        if (cancelEvent) {
            event.setCancelled(true);
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValues(final PlayerSwapHandItemsEvent event, final CastData data) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(final PlayerSwapHandItemsEvent event) {
        return event.getPlayer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(PlayerSwapHandItemsEvent event, Settings settings) {
        return event.getPlayer();
    }

}
