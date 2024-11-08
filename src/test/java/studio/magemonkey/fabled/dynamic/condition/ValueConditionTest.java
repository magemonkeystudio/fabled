package studio.magemonkey.fabled.dynamic.condition;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.DyeColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Sheep;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.dynamic.DynamicSkill;
import studio.magemonkey.fabled.testutil.MockedTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ValueConditionTest extends MockedTest {
    private       PlayerMock     player;
    private       ValueCondition condition;
    private final DataSection    settings = new DataSection();
    private       CastData       castData;

    @BeforeEach
    public void setup() {
        player = genPlayer("Travja");
        DataSection data = new DataSection();
        data.set("key", "foobar");
        data.set("min-value-base", "0");
        data.set("min-value-scale", "0");
        data.set("max-value-base", "9.7");
        data.set("max-value-scale", "0");
        settings.set("data", data);

        condition = new ValueCondition();
        condition.load(null, settings);

        castData = DynamicSkill.getCastData(player);
    }

    @Test
    void test_valueWellBelowPasses() {
        castData.put("foobar", 5.0);
        assertTrue(condition.test(player, 1, null));
    }

    @Test
    void test_equalTopPasses() {
        castData.put("foobar", 9.7);
        assertTrue(condition.test(player, 1, null));
    }

    @Test
    void test_equalTopPassesAfterMutation() {
        castData.put("foobar", 10.0);
        for (int i = 0; i < 3; i++) {
            castData.put("foobar", castData.getDouble("foobar") - 0.1);
        }
        assertTrue(condition.test(player, 1, null));
    }

    @Test
    void test_equalBottomPassesAfterMutation() {
        castData.put("foobar", -0.3);
        for (int i = 0; i < 3; i++) {
            castData.put("foobar", castData.getDouble("foobar") + 0.1);
        }
        assertTrue(condition.test(player, 1, null));
    }

    @Test
    void test_equalBottomPasses() {
        castData.put("foobar", 0.0);
        assertTrue(condition.test(player, 1, null));
    }

    @Test
    void test_valueAboveFails() {
        castData.put("foobar", 9.8);
        assertFalse(condition.test(player, 1, null));
    }

    @Test
    void test_valueBelowFails() {
        castData.put("foobar", -1.0);
        assertFalse(condition.test(player, 1, null));
    }
}