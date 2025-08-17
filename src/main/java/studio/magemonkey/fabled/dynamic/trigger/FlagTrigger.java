package studio.magemonkey.fabled.dynamic.trigger;

import org.bukkit.entity.LivingEntity;
import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.Settings;
import studio.magemonkey.fabled.api.event.FlagApplyEvent;

import java.util.List;

public class FlagTrigger implements Trigger<FlagApplyEvent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "FLAG";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<FlagApplyEvent> getEvent() {
        return FlagApplyEvent.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldTrigger(final FlagApplyEvent event, final int level, final Settings settings) {
        final List<String> flags = settings.getStringList("flags");
        final boolean inverted  = settings.getBool("inverted", false);
        final double minDuration = settings.getDouble("min-duration", 0);
        final int ticks = (int) minDuration * 20;

        // Return False if the Flaf 
        if (event.getTicks() < ticks){
            return false;
        };
        return (flags.isEmpty() || flags.contains("Any") || flags.contains(event.getFlag())) != inverted;
    }

    /**
     * {@inheritDoc}
     */
    // Adds flag duration as a usable Value
    @Override
    public void setValues(final FlagApplyEvent event, final CastData data) {
        data.put("api-"+event.getFlag()+"-duration", event.getTicks());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(final FlagApplyEvent event) {
        return event.getEntity();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(final FlagApplyEvent event, final Settings settings) {
        return event.getEntity();
    }

}
