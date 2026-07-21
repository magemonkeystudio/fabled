package studio.magemonkey.fabled.dynamic.condition;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import studio.magemonkey.fabled.testutil.MockedTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Covers the weapon-mode targeting and range check in AttackIndicatorCondition.
 * <p>
 * Not covered here: the ShootListener metadata-capture path (needs a real
 * EntityShootBowEvent, whose constructor isn't exercised anywhere else in this codebase
 * to model against) and the Paper-only reflection fallbacks for getActiveItem /
 * CrossbowMeta#isCharged - those should get manual/in-game verification since MockBukkit's
 * support for them is unverified here.
 */
public class AttackIndicatorConditionTest extends MockedTest {
    private Player player;

    @BeforeEach
    void setup() {
        player = genPlayer("Travja");
    }

    private AttackIndicatorCondition getCondition(String weapon, double min, double max) {
        AttackIndicatorCondition condition = new AttackIndicatorCondition();
        DataSection              config    = new DataSection();
        DataSection              data      = new DataSection();
        if (weapon != null) data.set("weapon", weapon);
        data.set("min-base", min);
        data.set("min-scale", 0);
        data.set("max-base", max);
        data.set("max-scale", 0);
        config.set("data", data);
        condition.load(null, config);
        return condition;
    }

    @Test
    void test_chargedCrossbow_reportsFullValue() {
        ItemStack    crossbow = new ItemStack(Material.CROSSBOW);
        CrossbowMeta meta     = (CrossbowMeta) crossbow.getItemMeta();
        meta.setChargedProjectiles(List.of(new ItemStack(Material.ARROW)));
        crossbow.setItemMeta(meta);
        player.getInventory().setItemInMainHand(crossbow);

        AttackIndicatorCondition condition = getCondition("crossbow", 0.9, 1.0);
        assertTrue(condition.test(player, 1, player));
    }

    @Test
    void test_unchargedCrossbow_notWithinHighRange() {
        player.getInventory().setItemInMainHand(new ItemStack(Material.CROSSBOW));

        AttackIndicatorCondition condition = getCondition("crossbow", 0.9, 1.0);
        assertFalse(condition.test(player, 1, player));
    }

    @Test
    void test_nonCasterTarget_alwaysFalse() {
        AttackIndicatorCondition condition = getCondition(null, 0.0, 1.0);
        // AttackIndicatorCondition only evaluates against a Player caster; a non-player
        // caster short-circuits to false regardless of range.
        assertFalse(condition.test(null, 1, player));
    }
}
