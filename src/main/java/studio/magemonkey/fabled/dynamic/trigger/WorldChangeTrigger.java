package studio.magemonkey.fabled.dynamic.trigger;

import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.Settings;

import java.util.List;

/**
 * Fabled © 2024
 * studio.magemonkey.fabled.dynamic.trigger.WorldChangeTrigger
 */
public class WorldChangeTrigger implements Trigger<PlayerChangedWorldEvent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "WORLD_CHANGE";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<PlayerChangedWorldEvent> getEvent() {
        return PlayerChangedWorldEvent.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldTrigger(final PlayerChangedWorldEvent event, final int level, final Settings settings) {
        World from = event.getFrom();
        World to = event.getPlayer().getWorld();

        List<String> worlds = settings.getStringList("worlds");
        boolean inverted = settings.getBool("inverted", false);
        String direction = settings.getString("direction", "to"); // Directions are to, from, or both

        boolean correctWorld = false;
        if (direction.equalsIgnoreCase("to")) {
            correctWorld = worlds.contains("Any") || worlds.contains(to.getName());
        } else if (direction.equalsIgnoreCase("from")) {
            correctWorld = worlds.contains("Any") || worlds.contains(from.getName());
        } else if (direction.equalsIgnoreCase("both")) {
            correctWorld = worlds.contains("Any") || worlds.contains(to.getName()) || worlds.contains(from.getName());
        }

        return correctWorld != inverted;
    }

    @Override
    public void setValues(PlayerChangedWorldEvent event, CastData data) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(final PlayerChangedWorldEvent event) {
        return event.getPlayer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(final PlayerChangedWorldEvent event, final Settings settings) {
        return event.getPlayer();
    }
}
