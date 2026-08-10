package studio.magemonkey.fabled.dynamic.condition;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.util.FlagManager;
import studio.magemonkey.fabled.dynamic.DynamicSkill;
import studio.magemonkey.fabled.testutil.MockedTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mockStatic;

public class FlagConditionTest extends MockedTest {
    private Player                     caster;
    private Player                     target;
    private MockedStatic<DynamicSkill> dynamicSkill;
    private CastData                   data;

    @BeforeEach
    public void setup() {
        caster = genPlayer("Travja");
        target = genPlayer("goflish");
        dynamicSkill = mockStatic(DynamicSkill.class);
        data = new CastData(caster);
        dynamicSkill.when(() -> DynamicSkill.getCastData(any())).thenReturn(data);
    }

    @AfterEach
    public void tearDown() {
        dynamicSkill.close();
        dynamicSkill = null;
    }

    private FlagCondition getCondition(String key, String type) {
        FlagCondition condition = new FlagCondition();
        DataSection   config    = new DataSection();
        DataSection   settings  = new DataSection();
        settings.set("key", key);
        if (type != null) settings.set("type", type);
        config.set("data", settings);
        condition.load(null, config);
        return condition;
    }

    private FlagCondition getCondition(List<String> keys, String type, String match) {
        FlagCondition condition = new FlagCondition();
        DataSection   config    = new DataSection();
        DataSection   settings  = new DataSection();
        settings.set("key", keys);
        if (type != null) settings.set("type", type);
        if (match != null) settings.set("match", match);
        config.set("data", settings);
        condition.load(null, config);
        return condition;
    }

    @Test
    void test_staticKey_matchesFlagOnTarget() {
        FlagManager.addFlag(target, "stunned", 100);

        FlagCondition condition = getCondition("stunned", "set");
        assertTrue(condition.test(caster, 1, target));
    }

    @Test
    void test_dynamicTargetPlaceholder_resolvesAgainstTargetFlag() {
        FlagManager.addFlag(target, "marked-" + target.getName(), 100);

        FlagCondition condition = getCondition("marked-{target}", "set");
        assertTrue(condition.test(caster, 1, target));
    }

    @Test
    void test_notSet_returnsTrueWhenFlagAbsent() {
        FlagCondition condition = getCondition("stunned", "not set");
        assertTrue(condition.test(caster, 1, target));

        FlagManager.addFlag(target, "stunned", 100);
        assertFalse(condition.test(caster, 1, target));
    }

    @Test
    void test_matchAll_set_requiresEveryKeySet() {
        FlagCondition condition = getCondition(List.of("stunned", "silenced"), "set", "all");

        FlagManager.addFlag(target, "stunned", 100);
        assertFalse(condition.test(caster, 1, target));

        FlagManager.addFlag(target, "silenced", 100);
        assertTrue(condition.test(caster, 1, target));
    }

    @Test
    void test_matchAny_set_requiresAtLeastOneKeySet() {
        FlagCondition condition = getCondition(List.of("stunned", "silenced"), "set", "any");

        assertFalse(condition.test(caster, 1, target));

        FlagManager.addFlag(target, "silenced", 100);
        assertTrue(condition.test(caster, 1, target));
    }

    @Test
    void test_matchAll_notSet_requiresEveryKeyUnset() {
        FlagCondition condition = getCondition(List.of("stunned", "silenced"), "not set", "all");

        assertTrue(condition.test(caster, 1, target));

        FlagManager.addFlag(target, "stunned", 100);
        assertFalse(condition.test(caster, 1, target));
    }

    @Test
    void test_matchAny_notSet_requiresAtLeastOneKeyUnset() {
        FlagCondition condition = getCondition(List.of("stunned", "silenced"), "not set", "any");

        FlagManager.addFlag(target, "stunned", 100);
        assertTrue(condition.test(caster, 1, target)); // "silenced" is still unset

        FlagManager.addFlag(target, "silenced", 100);
        assertFalse(condition.test(caster, 1, target));
    }

    @Test
    void test_missingMatch_defaultsToAll_backwardCompatible() {
        FlagCondition condition = getCondition(List.of("stunned", "silenced"), "set", null);

        FlagManager.addFlag(target, "stunned", 100);
        assertFalse(condition.test(caster, 1, target));

        FlagManager.addFlag(target, "silenced", 100);
        assertTrue(condition.test(caster, 1, target));
    }

    @Test
    void test_legacyScalarKey_stillTreatedAsSingleEntryList() {
        // Older configs stored `key` as a plain string rather than a list;
        // Settings#getStringList wraps scalars into a single-element list automatically.
        FlagCondition condition = getCondition("stunned", "set");
        assertFalse(condition.test(caster, 1, target));

        FlagManager.addFlag(target, "stunned", 100);
        assertTrue(condition.test(caster, 1, target));
    }
}
