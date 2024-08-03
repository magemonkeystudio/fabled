package studio.magemonkey.fabled.dynamic.trigger;

import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.Settings;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Fabled © 2024
 * studio.magemonkey.fabled.dynamic.trigger.BlockBreakTrigger
 */
public class ChatTrigger implements Trigger<AsyncPlayerChatEvent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "CHAT";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<AsyncPlayerChatEvent> getEvent() {
        return AsyncPlayerChatEvent.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldTrigger(final AsyncPlayerChatEvent event, final int level, final Settings settings) {
        boolean cancelMessage = settings.getBool("cancel", false);
        boolean regex         = settings.getBool("regex", false);
        String  format        = settings.getString("format", "");
        if (regex) {
            if (!event.getMessage().matches(format)) {
                return false;
            }
        } else if (!event.getMessage().contains(format)) return false;

        event.setCancelled(cancelMessage);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValues(final AsyncPlayerChatEvent event, final CastData data) {
        data.put("api-message", event.getMessage());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(final AsyncPlayerChatEvent event) {
        return event.getPlayer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(final AsyncPlayerChatEvent event, final Settings settings) {
        return event.getPlayer();
    }
}
