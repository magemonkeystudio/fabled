package studio.magemonkey.fabled.dynamic.mechanic;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import studio.magemonkey.fabled.api.util.FlagManager;
import studio.magemonkey.fabled.dynamic.DynamicSkill;
import studio.magemonkey.fabled.testutil.MockedTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FlagClearMechanicTest extends MockedTest {
    private Player player;

    @BeforeEach
    public void setup() {
        player = this.genPlayer("Travja");
    }

    private FlagClearMechanic getMechanic(String key, boolean regex) {
        FlagClearMechanic mechanic = new FlagClearMechanic();
        DynamicSkill      skill    = new DynamicSkill("FlagClear");

        DataSection config = new DataSection();
        DataSection data   = new DataSection();
        data.set("key", key);
        data.set("regex", regex);
        config.set("data", data);

        mechanic.load(skill, config);
        return mechanic;
    }

    @Test
    void execute_exactKey_removesMatchingFlag() {
        FlagManager.addFlag(player, "stunned", 100);
        FlagManager.addFlag(player, "burning", 100);
        assertTrue(FlagManager.hasFlag(player, "stunned"));
        assertTrue(FlagManager.hasFlag(player, "burning"));

        FlagClearMechanic mechanic = getMechanic("stunned", false);
        boolean result = mechanic.execute(player, 1, List.of(player), false);

        assertTrue(result);
        assertFalse(FlagManager.hasFlag(player, "stunned"));
        assertTrue(FlagManager.hasFlag(player, "burning"));
    }

    @Test
    void execute_regex_removesAllMatchingFlags() {
        FlagManager.addFlag(player, "effect_fire", 100);
        FlagManager.addFlag(player, "effect_ice", 100);
        FlagManager.addFlag(player, "stunned", 100);

        FlagClearMechanic mechanic = getMechanic("effect_.*", true);
        boolean result = mechanic.execute(player, 1, List.of(player), false);

        assertTrue(result);
        assertFalse(FlagManager.hasFlag(player, "effect_fire"));
        assertFalse(FlagManager.hasFlag(player, "effect_ice"));
        assertTrue(FlagManager.hasFlag(player, "stunned"));
    }

    @Test
    void execute_regex_removesAllFlags_whenPatternMatchesAll() {
        FlagManager.addFlag(player, "flag_a", 100);
        FlagManager.addFlag(player, "flag_b", 100);
        FlagManager.addFlag(player, "flag_c", 100);

        FlagClearMechanic mechanic = getMechanic("flag_.*", true);
        boolean result = mechanic.execute(player, 1, List.of(player), false);

        assertTrue(result);
        assertFalse(FlagManager.hasFlag(player, "flag_a"));
        assertFalse(FlagManager.hasFlag(player, "flag_b"));
        assertFalse(FlagManager.hasFlag(player, "flag_c"));
    }

    @Test
    void execute_regex_noMatchingFlags_doesNothing() {
        FlagManager.addFlag(player, "stunned", 100);

        FlagClearMechanic mechanic = getMechanic("effect_.*", true);
        boolean result = mechanic.execute(player, 1, List.of(player), false);

        assertTrue(result);
        assertTrue(FlagManager.hasFlag(player, "stunned"));
    }

    @Test
    void execute_invalidRegex_returnsFalse() {
        FlagManager.addFlag(player, "stunned", 100);

        FlagClearMechanic mechanic = getMechanic("[invalid(regex", true);
        boolean result = mechanic.execute(player, 1, List.of(player), false);

        assertFalse(result);
        assertTrue(FlagManager.hasFlag(player, "stunned"));
    }

    @Test
    void execute_noTargets_returnsFalse() {
        FlagClearMechanic mechanic = getMechanic("stunned", false);
        boolean result = mechanic.execute(player, 1, List.of(), false);

        assertFalse(result);
    }
}
