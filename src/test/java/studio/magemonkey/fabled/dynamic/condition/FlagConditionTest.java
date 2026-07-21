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
}
