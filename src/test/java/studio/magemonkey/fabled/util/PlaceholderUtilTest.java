package studio.magemonkey.fabled.util;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.MockServer;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.classes.FabledClass;
import studio.magemonkey.fabled.api.player.PlayerAccounts;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.data.GroupSettings;
import studio.magemonkey.fabled.data.Settings;
import studio.magemonkey.fabled.manager.ComboManager;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Tests that PlaceholderAPI replacements are functioning correctly
 */
public class PlaceholderUtilTest {
    private MockedStatic<Fabled> fabledMock;
    private MockedStatic<Bukkit> bukkitMock;
    private Settings             settings;

    private Player     player;
    private PlayerData playerData;

    private FabledClass classClass, raceClass;

    @BeforeEach
    void setup() throws NoSuchFieldException, IllegalAccessException {
        player = mock(Player.class);
        UUID uuid = UUID.randomUUID();
        when(player.getName()).thenReturn("player");
        when(player.getUniqueId()).thenReturn(uuid);
        when(player.getAttribute(any())).thenReturn(mock(AttributeInstance.class));

        fabledMock = mockStatic(Fabled.class);
        settings = mock(Settings.class);
        when(settings.getMainGroup()).thenReturn("class");
        when(settings.getSlots()).thenReturn(new int[]{1, 2});
        fabledMock.when(Fabled::getSettings).thenReturn(settings);
        fabledMock.when(Fabled::getComboManager).thenReturn(mock(ComboManager.class));

        bukkitMock = mockStatic(Bukkit.class);
        bukkitMock.when(() -> Bukkit.getPlayer(uuid)).thenReturn(player);
        Server server = mock(MockServer.class);
        when(server.getBukkitVersion()).thenReturn("1.20.6-R0.1-SNAPSHOT");
        bukkitMock.when(Bukkit::getServer).thenReturn(server);

        PlayerAccounts playerAccounts = new PlayerAccounts(player);
        playerData = playerAccounts.getActiveData();

        GroupSettings classSettings = mock(GroupSettings.class);
        classClass = mock(FabledClass.class);
        when(classClass.getName()).thenReturn("warrior");
        when(classClass.getGroup()).thenReturn("class");
        when(classClass.getPrefix()).thenReturn("&6warrior");
        when(classClass.getMaxLevel()).thenReturn(5);
        when(classClass.getGroupSettings()).thenReturn(classSettings);
        raceClass = mock(FabledClass.class);
        when(raceClass.getName()).thenReturn("human");
        when(raceClass.getGroup()).thenReturn("race");
        when(raceClass.getPrefix()).thenReturn("&6human");
        when(raceClass.getMaxLevel()).thenReturn(10);
        when(raceClass.getGroupSettings()).thenReturn(mock(GroupSettings.class));

        playerData.setClass(null, classClass, false);
        playerData.setClass(null, raceClass, false);

        fabledMock.when(() -> Fabled.getData(player)).thenReturn(playerData);
        fabledMock.when(() -> Fabled.hasPlayerData(player)).thenReturn(true);
    }

    @AfterEach
    void tearDown() {
        fabledMock.close();
        bukkitMock.close();
    }

    @Test
    void replace_mainClass() {
        String replaced = PlaceholderUtil.replace(player, "class");
        assertEquals("warrior", replaced);
    }

    @Test
    void replace_class() {
        String replaced = PlaceholderUtil.replace(player, "class_class");
        assertEquals("warrior", replaced);
    }

    @Test
    void replace_alternate_class() {
        String replaced = PlaceholderUtil.replace(player, "class_race");
        assertEquals("human", replaced);
    }

    @Test
    void replace_race_maxlevel() {
        String replaced = PlaceholderUtil.replace(player, "maxlevel_race");
        assertEquals("10", replaced);
    }

    @Test
    void replace_class_maxlevel() {
        String replaced = PlaceholderUtil.replace(player, "maxlevel");
        assertEquals("5", replaced);
    }

    @Test
    void replace_class_level() {
        String replaced = PlaceholderUtil.replace(player, "level");
        assertEquals("1", replaced);
    }

    @Test
    void replace_race_level() {
        String replaced = PlaceholderUtil.replace(player, "level_race");
        assertEquals("1", replaced);
    }

    @Test
    void replace_class_prefix() {
        String replaced = PlaceholderUtil.replace(player, "prefix");
        assertEquals("&6warrior", replaced);
    }

    @Test
    void replace_race_prefix() {
        String replaced = PlaceholderUtil.replace(player, "prefix_race");
        assertEquals("&6human", replaced);
    }
}
