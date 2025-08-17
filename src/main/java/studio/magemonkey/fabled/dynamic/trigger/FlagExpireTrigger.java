package studio.magemonkey.fabled.dynamic.trigger;

import org.bukkit.entity.LivingEntity;
import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.Settings;
import studio.magemonkey.fabled.api.event.FlagExpireEvent;

import java.util.List;

public class FlagExpireTrigger implements Trigger<FlagExpireEvent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "FLAG_EXPIRE";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<FlagExpireEvent> getEvent() {
        return FlagExpireEvent.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldTrigger(final FlagExpireEvent event, final int level, final Settings settings) {
        final List<String> flags = settings.getStringList("flags");
        final boolean inverted  = settings.getBool("inverted", false);
        return (flags.isEmpty() || flags.contains("Any") || flags.contains(event.getFlag())) != inverted;
    }

    /**
     * {@inheritDoc}
     */
    // Removes flag duration as a usable Value if it is set
    @Override
    public void setValues(final FlagExpireEvent event, final CastData data) {
        data.remove("api-"+event.getFlag()+"-duration");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(final FlagExpireEvent event) {
        return event.getEntity();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(final FlagExpireEvent event, final Settings settings) {
        return event.getEntity();
    }

}
