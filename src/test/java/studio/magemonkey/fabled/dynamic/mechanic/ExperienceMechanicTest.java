package studio.magemonkey.fabled.dynamic.mechanic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.Settings;
import studio.magemonkey.fabled.api.classes.FabledClass;
import studio.magemonkey.fabled.api.enums.ExpSource;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.dynamic.EffectComponent;
import studio.magemonkey.fabled.testutil.MockedTest;

import java.io.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@Slf4j
class ExperienceMechanicTest extends MockedTest {
    private Settings   settings;
    private PlayerMock caster;
    private PlayerData playerData;

    private ExperienceMechanic experienceMechanic;

    @Override
    public void preInit() {
        loadClasses("Honor Guard", "Mage");
    }

    private void initGroupsFile() throws IOException {
        File groupsFile = new File(getPluginFolder(), "groups.yml");
        try (BufferedReader in = new BufferedReader(new InputStreamReader(Objects.requireNonNull(MockedTest.class
                .getClassLoader()
                .getResourceAsStream("groups.yml"))));
             FileWriter writer = new FileWriter(groupsFile)) {
            String str;
            while ((str = in.readLine()) != null) {
                writer.write(str + "\n");
            }
            log.info("Saved groups file");
        }
    }

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException, IOException {
        initGroupsFile();

        caster = genPlayer("Travja");
        playerData = Fabled.getData(caster);
        playerData.resetAll();

        FabledClass clazz = Fabled.getClass("Honor Guard");
        playerData.setClass(null, clazz, true);
        playerData.setExp(100, ExpSource.PLUGIN, false);

        settings = mock(Settings.class);
        when(settings.has("value")).thenReturn(true);
        when(settings.getString("value")).thenReturn("1");
        when(settings.getString("mode", "give")).thenReturn("give");
        when(settings.getString("type", "flat")).thenReturn("flat");
        when(settings.getBool("level-down", true)).thenReturn(true);
        when(settings.getBool("vanilla", false)).thenReturn(false);
        when(settings.getString("group", "class")).thenReturn("class");

        experienceMechanic = spy(new ExperienceMechanic());
        Field settingsField = EffectComponent.class.getDeclaredField("settings");
        settingsField.setAccessible(true);
        settingsField.set(experienceMechanic, settings);

        assertEquals(100,
                Objects.requireNonNull(playerData.getMainClass()).getTotalExp(),
                "Player should start with 100 exp");
    }

    @Test
    void classExperienceMechanic_giveFlat() {
        experienceMechanic.classExperienceMechanic(caster, 1, List.of(), false);

        assertEquals(101, Objects.requireNonNull(playerData.getMainClass()).getTotalExp(),
                "Player should have 101 exp after giving 1 exp");
    }

    @Test
    void classExperienceMechanic_setFlat() {
        when(settings.getString("mode", "give")).thenReturn("set");
        experienceMechanic.classExperienceMechanic(caster, 1, List.of(), false);

        // 62 is 1 additional beyond level 3
        assertEquals(62, Objects.requireNonNull(playerData.getMainClass()).getTotalExp(),
                "Player should have 62 exp after setting to 1 exp");
        assertEquals(3, Objects.requireNonNull(playerData.getMainClass()).getLevel(),
                "Player should be at level 3 after setting to 1 exp");
    }

    @Test
    void classExperienceMechanic_takeFlat() {
        when(settings.getString("mode", "give")).thenReturn("take");
        experienceMechanic.classExperienceMechanic(caster, 1, List.of(), false);

        assertEquals(99, Objects.requireNonNull(playerData.getMainClass()).getTotalExp(),
                "Player should have 99 exp after taking 1 exp");
    }

    @Test
    void classExperienceMechanic_giveLevels() {
        when(settings.getString("type", "flat")).thenReturn("levels");
        experienceMechanic.classExperienceMechanic(caster, 1, List.of(), false);

        assertEquals(149, Objects.requireNonNull(playerData.getMainClass()).getTotalExp(),
                "Player should have 149 exp after giving 1 level");
        assertEquals(4, Objects.requireNonNull(playerData.getMainClass()).getLevel(),
                "Player should be at level 4 after giving 1 level");
    }

    @Test
    void classExperienceMechanic_setLevels() {
        when(settings.getString("type", "flat")).thenReturn("levels");
        when(settings.getString("mode", "give")).thenReturn("set");
        experienceMechanic.classExperienceMechanic(caster, 1, List.of(), false);

        assertEquals(39, Objects.requireNonNull(playerData.getMainClass()).getTotalExp(),
                "Player should have 39 exp after setting to 1 level");
        assertEquals(1, Objects.requireNonNull(playerData.getMainClass()).getLevel(),
                "Player should be at level 1 after setting to 1 level");
    }

    @Test
    void classExperienceMechanic_takeLevels() {
        when(settings.getString("type", "flat")).thenReturn("levels");
        when(settings.getString("mode", "give")).thenReturn("take");
        experienceMechanic.classExperienceMechanic(caster, 1, List.of(), false);

        assertEquals(64, Objects.requireNonNull(playerData.getMainClass()).getTotalExp(),
                "Player should have 99 exp after taking 1 level");
        assertEquals(2, Objects.requireNonNull(playerData.getMainClass()).getLevel(),
                "Player should be at level 2 after taking 1 level");
    }

    @Test
    void classExperienceMechanic_givePercent() {
        int expToLevel = Objects.requireNonNull(playerData.getMainClass()).getData().getRequiredExp(3);

        when(settings.getString("type", "flat")).thenReturn("percent");
        when(settings.getString("value")).thenReturn("10");
        experienceMechanic.classExperienceMechanic(caster, 1, List.of(), false);

        assertEquals(100 + Math.round(expToLevel / 10D), Objects.requireNonNull(playerData.getMainClass()).getTotalExp(),
                "Player should have 10% of exp to level after giving 10% of exp");
        assertEquals(3, Objects.requireNonNull(playerData.getMainClass()).getLevel(),
                "Player should be at level 3 after giving 10% of exp");
    }

    @Test
    void classExperienceMechanic_setPercent() {
        int expToLevel = Objects.requireNonNull(playerData.getMainClass()).getData().getRequiredExp(3);
        int level3TotalExp = 61;

        when(settings.getString("type", "flat")).thenReturn("percent");
        when(settings.getString("mode", "give")).thenReturn("set");
        when(settings.getString("value")).thenReturn("10");
        experienceMechanic.classExperienceMechanic(caster, 1, List.of(), false);

        assertEquals(level3TotalExp + expToLevel / 10D, Objects.requireNonNull(playerData.getMainClass()).getTotalExp(),
                "Player should have 10% of exp to level after setting to 10% of exp");
        assertEquals(3, Objects.requireNonNull(playerData.getMainClass()).getLevel(),
                "Player should be at level 3 after setting to 10% of exp");
    }

    @Test
    void classExperienceMechanic_takePercent() {
        int expToLevel = Objects.requireNonNull(playerData.getMainClass()).getData().getRequiredExp(3);

        when(settings.getString("type", "flat")).thenReturn("percent");
        when(settings.getString("mode", "give")).thenReturn("take");
        when(settings.getString("value")).thenReturn("10");
        experienceMechanic.classExperienceMechanic(caster, 1, List.of(), false);

        assertEquals(100 - expToLevel / 10D, Objects.requireNonNull(playerData.getMainClass()).getTotalExp(),
                "Player should have 10% of exp to level after taking 10% of exp");
        assertEquals(3, Objects.requireNonNull(playerData.getMainClass()).getLevel(),
                "Player should be at level 3 after taking 10% of exp");
    }
}