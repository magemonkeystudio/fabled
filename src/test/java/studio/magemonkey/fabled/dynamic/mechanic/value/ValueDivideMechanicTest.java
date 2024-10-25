package studio.magemonkey.fabled.dynamic.mechanic.value;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.Settings;
import studio.magemonkey.fabled.dynamic.DynamicSkill;
import studio.magemonkey.fabled.dynamic.EffectComponent;
import studio.magemonkey.fabled.manager.IAttributeManager;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ValueDivideMechanicTest {
    private MockedStatic<Fabled> fabled;
    private Fabled               fabledMock;
    private CastData             data;
    private Player               player;

    private ValueDivideMechanic mechanic;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        fabled = mockStatic(Fabled.class);
        fabledMock = mock(Fabled.class);
        when(fabledMock.getLogger()).thenReturn(mock(Logger.class));
        fabled.when(Fabled::inst).thenReturn(fabledMock);
        fabled.when(Fabled::getAttributesManager).thenReturn(mock(IAttributeManager.class));
        UUID uuid = UUID.randomUUID();
        player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(uuid);
        data = DynamicSkill.getCastData(player);

        Settings settings = mock(Settings.class);
        when(settings.has("key")).thenReturn(true);
        when(settings.getString("key")).thenReturn("key");
        when(settings.getBool("save", false)).thenReturn(false);

        mechanic = spy(new ValueDivideMechanic());
        Field settingsField = EffectComponent.class.getDeclaredField("settings");
        settingsField.setAccessible(true);
        settingsField.set(mechanic, settings);

    }

    @AfterEach
    void tearDown() {
        fabled.close();
    }

    @Test
    void execute_dividesLargeNumberCleanly() {
        data.put("key", 100d);
        doReturn(10d).when(mechanic).parseValues(player, "divisor", 1, 1);
        boolean result = mechanic.execute(player, 1, List.of(player), false);
        assertTrue(result);
        assertEquals(10, DynamicSkill.getCastData(player).getDouble("key"));
    }

    @Test
    void execute_dividesSmallNumberCleanly() {
        data.put("key", 1d);
        doReturn(10d).when(mechanic).parseValues(player, "divisor", 1, 1);
        boolean result = mechanic.execute(player, 1, List.of(player), false);
        assertTrue(result);
        assertEquals(0.1d, DynamicSkill.getCastData(player).getDouble("key"));
    }

    @Test
    void execute_dividesZero() {
        data.put("key", 0d);
        doReturn(10d).when(mechanic).parseValues(player, "divisor", 1, 1);
        boolean result = mechanic.execute(player, 1, List.of(player), false);
        assertTrue(result);
        assertEquals(0, DynamicSkill.getCastData(player).getDouble("key"));
    }

    @Test
    void execute_dividesNegativeNumber() {
        data.put("key", -100d);
        doReturn(10d).when(mechanic).parseValues(player, "divisor", 1, 1);
        boolean result = mechanic.execute(player, 1, List.of(player), false);
        assertTrue(result);
        assertEquals(-10, DynamicSkill.getCastData(player).getDouble("key"));
    }

    @Test
    void execute_dividesNegativeNumberWithNegativeDivisor() {
        data.put("key", -100d);
        doReturn(-10d).when(mechanic).parseValues(player, "divisor", 1, 1);
        boolean result = mechanic.execute(player, 1, List.of(player), false);
        assertTrue(result);
        assertEquals(10, DynamicSkill.getCastData(player).getDouble("key"));
    }

    @Test
    void execute_divideByZero() {
        data.put("key", 100d);
        doReturn(0d).when(mechanic).parseValues(player, "divisor", 1, 1);
        boolean result = mechanic.execute(player, 1, List.of(player), false);
        assertFalse(result);
    }
}
