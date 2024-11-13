package studio.magemonkey.fabled.dynamic.condition;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.DyeColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Shulker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import studio.magemonkey.fabled.testutil.MockedTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ColorConditionTest extends MockedTest {
    private PlayerMock     player;
    private LivingEntity   target;
    private ColorCondition condition;
    private DataSection    settings = new DataSection();

    @BeforeEach
    public void setup() {
        player = genPlayer("Travja");
        target = (LivingEntity) player.getWorld()
                .spawnEntity(player.getLocation(), EntityType.SHEEP);
        ((Sheep) target).setColor(DyeColor.CYAN);

        DataSection data = new DataSection();
        data.set("color", "gray");
        settings.set("data", data);

        condition = new ColorCondition();
        condition.load(null, settings);
    }

    @Test
    void test_sheepIncorrectColor() {
        assertFalse(condition.test(player, 1, target));
    }

    @Test
    void test_sheepCorrectColor() {
        ((Sheep) target).setColor(DyeColor.GRAY);
        assertTrue(condition.test(player, 1, target));
    }

    @Test
    void test_shulkerIncorrectColor() {
        target.remove();
        target = (LivingEntity) player.getWorld()
                .spawnEntity(player.getLocation(), EntityType.SHULKER);
        ((Shulker) target).setColor(DyeColor.CYAN);
        assertFalse(condition.test(player, 1, target));
    }

    @Test
    void test_shulkerCorrectColor() {
        target.remove();
        target = (LivingEntity) player.getWorld()
                .spawnEntity(player.getLocation(), EntityType.SHULKER);
        ((Shulker) target).setColor(DyeColor.GRAY);
        assertTrue(condition.test(player, 1, target));
    }
}