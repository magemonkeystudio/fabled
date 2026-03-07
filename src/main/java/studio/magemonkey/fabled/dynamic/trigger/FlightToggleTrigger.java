package studio.magemonkey.fabled.dynamic.trigger;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.Settings;

/**
 * © 2026 VoidEdge
 * studio.magemonkey.fabled.dynamic.trigger.ToggleFlightTrigger
 */
public class FlightToggleTrigger implements Trigger<PlayerToggleFlightEvent> {

    @Override
    public String getKey() {
        return "FLIGHT TOGGLE";
    }

    @Override
    public Class<PlayerToggleFlightEvent> getEvent() {
        return PlayerToggleFlightEvent.class;
    }

    @Override
    public boolean shouldTrigger(final PlayerToggleFlightEvent event, final int level, final Settings settings) {
        final String type = settings.getString("type", "start flying");
        return type.equalsIgnoreCase("both") || event.isFlying() != type.equalsIgnoreCase("stop flying");
    }

    @Override
    public void setValues(PlayerToggleFlightEvent event, final CastData data) {
    }

    @Override
    public LivingEntity getCaster(final PlayerToggleFlightEvent event) {
        return event.getPlayer();
    }

    @Override
    public LivingEntity getTarget(final PlayerToggleFlightEvent event, final Settings settings) {
        return event.getPlayer();
    }
}
