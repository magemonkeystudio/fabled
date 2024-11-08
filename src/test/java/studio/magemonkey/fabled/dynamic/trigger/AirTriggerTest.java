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

//     @Test
//     void shouldTrigger_toWorldAnyTriggers() {
//         Player   player   = mock(Player.class);
//         World toWorld = mock(World.class);
//         when(toWorld.getName()).thenReturn("to_world");
//         when(player.getWorld()).thenReturn(toWorld);

//         World    fromWorld    = mock(World.class);
//         when(fromWorld.getName()).thenReturn("world");

//         Settings settings = mock(Settings.class);
//         when(settings.getStringList("worlds")).thenReturn(List.of("Any"));
//         when(settings.getBool("inverted", false)).thenReturn(false);
//         when(settings.getString("direction", "to")).thenReturn("to");

//         WorldChangeTrigger trigger = new WorldChangeTrigger();
//         PlayerChangedWorldEvent event = new PlayerChangedWorldEvent(player, fromWorld);

//         assertTrue(trigger.shouldTrigger(event, 1, settings));
//     }

//     @Test
//     void shouldTrigger_fromWorldNamedTriggers() {
//         Player   player   = mock(Player.class);
//         World toWorld = mock(World.class);
//         when(toWorld.getName()).thenReturn("to_world");
//         when(player.getWorld()).thenReturn(toWorld);

//         World    fromWorld    = mock(World.class);
//         when(fromWorld.getName()).thenReturn("world");

//         Settings settings = mock(Settings.class);
//         when(settings.getStringList("worlds")).thenReturn(List.of("world"));
//         when(settings.getBool("inverted", false)).thenReturn(false);
//         when(settings.getString("direction", "to")).thenReturn("from");

//         WorldChangeTrigger trigger = new WorldChangeTrigger();
//         PlayerChangedWorldEvent event = new PlayerChangedWorldEvent(player, fromWorld);

//         assertTrue(trigger.shouldTrigger(event, 1, settings));
//     }

//     @Test
//     void shouldTrigger_fromWorldAnyTriggers() {
//         Player   player   = mock(Player.class);
//         World toWorld = mock(World.class);
//         when(toWorld.getName()).thenReturn("to_world");
//         when(player.getWorld()).thenReturn(toWorld);

//         World    fromWorld    = mock(World.class);
//         when(fromWorld.getName()).thenReturn("world");

//         Settings settings = mock(Settings.class);
//         when(settings.getStringList("worlds")).thenReturn(List.of("Any"));
//         when(settings.getBool("inverted", false)).thenReturn(false);
//         when(settings.getString("direction", "to")).thenReturn("from");

//         WorldChangeTrigger trigger = new WorldChangeTrigger();
//         PlayerChangedWorldEvent event = new PlayerChangedWorldEvent(player, fromWorld);

//         assertTrue(trigger.shouldTrigger(event, 1, settings));
//     }

//     @Test
//     void shouldTrigger_bothWorldNamedTriggers() {
//         Player   player   = mock(Player.class);
//         World toWorld = mock(World.class);
//         when(toWorld.getName()).thenReturn("to_world");
//         when(player.getWorld()).thenReturn(toWorld);

//         World    fromWorld    = mock(World.class);
//         when(fromWorld.getName()).thenReturn("world");

//         Settings settings = mock(Settings.class);
//         when(settings.getStringList("worlds")).thenReturn(List.of("world"));
//         when(settings.getBool("inverted", false)).thenReturn(false);
//         when(settings.getString("direction", "to")).thenReturn("both");

//         WorldChangeTrigger trigger = new WorldChangeTrigger();
//         PlayerChangedWorldEvent event = new PlayerChangedWorldEvent(player, fromWorld);

//         assertTrue(trigger.shouldTrigger(event, 1, settings));

//         when(fromWorld.getName()).thenReturn("to_world");
//         when(toWorld.getName()).thenReturn("world");

//         assertTrue(trigger.shouldTrigger(event, 1, settings));
//     }

//     @Test
//     void shouldTrigger_bothWorldAnyTriggers() {
//         Player   player   = mock(Player.class);
//         World toWorld = mock(World.class);
//         when(toWorld.getName()).thenReturn("to_world");
//         when(player.getWorld()).thenReturn(toWorld);

//         World    fromWorld    = mock(World.class);
//         when(fromWorld.getName()).thenReturn("world");

//         Settings settings = mock(Settings.class);
//         when(settings.getStringList("worlds")).thenReturn(List.of("Any"));
//         when(settings.getBool("inverted", false)).thenReturn(false);
//         when(settings.getString("direction", "to")).thenReturn("both");

//         WorldChangeTrigger trigger = new WorldChangeTrigger();
//         PlayerChangedWorldEvent event = new PlayerChangedWorldEvent(player, fromWorld);

//         assertTrue(trigger.shouldTrigger(event, 1, settings));

//         when(fromWorld.getName()).thenReturn("to_world");
//         when(toWorld.getName()).thenReturn("world");

//         assertTrue(trigger.shouldTrigger(event, 1, settings));
//     }

//     @Test
//     void shouldTrigger_toWorldNamedInvertedDoesNotTrigger() {
//         Player   player   = mock(Player.class);
//         World toWorld = mock(World.class);
//         when(toWorld.getName()).thenReturn("to_world");
//         when(player.getWorld()).thenReturn(toWorld);

//         World    fromWorld    = mock(World.class);
//         when(fromWorld.getName()).thenReturn("world");

//         Settings settings = mock(Settings.class);
//         when(settings.getStringList("worlds")).thenReturn(List.of("to_world"));
//         when(settings.getBool("inverted", false)).thenReturn(true);
//         when(settings.getString("direction", "to")).thenReturn("to");

//         WorldChangeTrigger trigger = new WorldChangeTrigger();
//         PlayerChangedWorldEvent event = new PlayerChangedWorldEvent(player, fromWorld);

//         assertFalse(trigger.shouldTrigger(event, 1, settings));
//     }

//     @Test
//     void shouldTrigger_toWorldAnyInvertedDoesNotTrigger() {
//         Player   player   = mock(Player.class);
//         World toWorld = mock(World.class);
//         when(toWorld.getName()).thenReturn("to_world");
//         when(player.getWorld()).thenReturn(toWorld);

//         World    fromWorld    = mock(World.class);
//         when(fromWorld.getName()).thenReturn("world");

//         Settings settings = mock(Settings.class);
//         when(settings.getStringList("worlds")).thenReturn(List.of("Any"));
//         when(settings.getBool("inverted", false)).thenReturn(true);
//         when(settings.getString("direction", "to")).thenReturn("to");

//         WorldChangeTrigger trigger = new WorldChangeTrigger();
//         PlayerChangedWorldEvent event = new PlayerChangedWorldEvent(player, fromWorld);

//         assertFalse(trigger.shouldTrigger(event, 1, settings));
//     }

//     @Test
//     void shouldTrigger_fromWorldNamedInvertedDoesNotTrigger() {
//         Player   player   = mock(Player.class);
//         World toWorld = mock(World.class);
//         when(toWorld.getName()).thenReturn("to_world");
//         when(player.getWorld()).thenReturn(toWorld);

//         World    fromWorld    = mock(World.class);
//         when(fromWorld.getName()).thenReturn("world");

//         Settings settings = mock(Settings.class);
//         when(settings.getStringList("worlds")).thenReturn(List.of("world"));
//         when(settings.getBool("inverted", false)).thenReturn(true);
//         when(settings.getString("direction", "to")).thenReturn("from");

//         WorldChangeTrigger trigger = new WorldChangeTrigger();
//         PlayerChangedWorldEvent event = new PlayerChangedWorldEvent(player, fromWorld);

//         assertFalse(trigger.shouldTrigger(event, 1, settings));
//     }

//     @Test
//     void shouldTrigger_fromWorldAnyInvertedDoesNotTrigger() {
//         Player   player   = mock(Player.class);
//         World toWorld = mock(World.class);
//         when(toWorld.getName()).thenReturn("to_world");
//         when(player.getWorld()).thenReturn(toWorld);

//         World    fromWorld    = mock(World.class);
//         when(fromWorld.getName()).thenReturn("world");

//         Settings settings = mock(Settings.class);
//         when(settings.getStringList("worlds")).thenReturn(List.of("Any"));
//         when(settings.getBool("inverted", false)).thenReturn(true);
//         when(settings.getString("direction", "to")).thenReturn("from");

//         WorldChangeTrigger trigger = new WorldChangeTrigger();
//         PlayerChangedWorldEvent event = new PlayerChangedWorldEvent(player, fromWorld);

//         assertFalse(trigger.shouldTrigger(event, 1, settings));
//     }

//     @Test
//     void shouldTrigger_bothWorldNamedInvertedDoesNotTrigger() {
//         Player   player   = mock(Player.class);
//         World toWorld = mock(World.class);
//         when(toWorld.getName()).thenReturn("to_world");
//         when(player.getWorld()).thenReturn(toWorld);

//         World    fromWorld    = mock(World.class);
//         when(fromWorld.getName()).thenReturn("world");

//         Settings settings = mock(Settings.class);
//         when(settings.getStringList("worlds")).thenReturn(List.of("world"));
//         when(settings.getBool("inverted", false)).thenReturn(true);
//         when(settings.getString("direction", "to")).thenReturn("both");

//         WorldChangeTrigger trigger = new WorldChangeTrigger();
//         PlayerChangedWorldEvent event = new PlayerChangedWorldEvent(player, fromWorld);

//         assertFalse(trigger.shouldTrigger(event, 1, settings));

//         when(fromWorld.getName()).thenReturn("to_world");
//         when(toWorld.getName()).thenReturn("world");

//         assertFalse(trigger.shouldTrigger(event, 1, settings));
//     }

//     @Test
//     void shouldTrigger_bothWorldAnyInvertedDoesNotTrigger() {
//         Player   player   = mock(Player.class);
//         World toWorld = mock(World.class);
//         when(toWorld.getName()).thenReturn("to_world");
//         when(player.getWorld()).thenReturn(toWorld);

//         World    fromWorld    = mock(World.class);
//         when(fromWorld.getName()).thenReturn("world");

//         Settings settings = mock(Settings.class);
//         when(settings.getStringList("worlds")).thenReturn(List.of("Any"));
//         when(settings.getBool("inverted", false)).thenReturn(true);
//         when(settings.getString("direction", "to")).thenReturn("both");

//         WorldChangeTrigger trigger = new WorldChangeTrigger();
//         PlayerChangedWorldEvent event = new PlayerChangedWorldEvent(player, fromWorld);

//         assertFalse(trigger.shouldTrigger(event, 1, settings));

//         when(fromWorld.getName()).thenReturn("to_world");
//         when(toWorld.getName()).thenReturn("world");

//         assertFalse(trigger.shouldTrigger(event, 1, settings));
//     }

//     @Test
//     void shouldTrigger_toWorldNamedInvertedTriggersWithOtherName() {
//         Player   player   = mock(Player.class);
//         World toWorld = mock(World.class);
//         when(toWorld.getName()).thenReturn("to_world_nether");
//         when(player.getWorld()).thenReturn(toWorld);

//         World    fromWorld    = mock(World.class);
//         when(fromWorld.getName()).thenReturn("world");

//         Settings settings = mock(Settings.class);
//         when(settings.getStringList("worlds")).thenReturn(List.of("to_world"));
//         when(settings.getBool("inverted", false)).thenReturn(true);
//         when(settings.getString("direction", "to")).thenReturn("to");

//         WorldChangeTrigger trigger = new WorldChangeTrigger();
//         PlayerChangedWorldEvent event = new PlayerChangedWorldEvent(player, fromWorld);

//         assertTrue(trigger.shouldTrigger(event, 1, settings));
//     }
// }