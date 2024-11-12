package studio.magemonkey.fabled.dynamic.trigger;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.junit.jupiter.api.Test;
import studio.magemonkey.fabled.api.Settings;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AirTriggerTest {
    @Test
    void shouldTrigger_DecreasingAir() {
        Player   player   = mock(Player.class);
        Settings settings = mock(Settings.class);
        AirTrigger trigger = new AirTrigger();
        EntityAirChangeEvent event = new EntityAirChangeEvent(player, -1);
        when(settings.getString("type", "decreasing")).thenReturn("decreasing");

        assertTrue(trigger.shouldTrigger(event, 1, settings));
    }

    @Test
    void shouldTrigger_IncreasingAir() {
        Player   player   = mock(Player.class);
        Settings settings = mock(Settings.class);
        AirTrigger trigger = new AirTrigger();
        EntityAirChangeEvent event = new EntityAirChangeEvent(player, 1);
        when(settings.getString("type", "decreasing")).thenReturn("increasing");

        assertTrue(trigger.shouldTrigger(event, 1, settings));
    }


    @Test
    void noTrigger_DecreasingAir() {
        Player   player   = mock(Player.class);
        Settings settings = mock(Settings.class);
        AirTrigger trigger = new AirTrigger();
        EntityAirChangeEvent event = new EntityAirChangeEvent(player, -1);
        when(settings.getString("type", "decreasing")).thenReturn("increasing");

        assertFalse(trigger.shouldTrigger(event, 1, settings));
    }

    @Test
    void noTrigger_IncreasingAir() {
        Player   player   = mock(Player.class);
        Settings settings = mock(Settings.class);
        AirTrigger trigger = new AirTrigger();
        EntityAirChangeEvent event = new EntityAirChangeEvent(player, 1);
        when(settings.getString("type", "decreasing")).thenReturn("decreasing");

        assertFalse(trigger.shouldTrigger(event, 1, settings));
    }
}