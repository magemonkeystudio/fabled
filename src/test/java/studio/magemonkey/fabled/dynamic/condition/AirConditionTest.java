package studio.magemonkey.fabled.dynamic.condition;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import studio.magemonkey.fabled.testutil.MockedTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AirConditionTest extends MockedTest {
    private PlayerMock   player;
    private LivingEntity target;
    private AirCondition condition;
    private DataSection  settings = new DataSection();

    @BeforeEach
    public void setup() {
        player = genPlayer("Travja");
        target = (LivingEntity) player.getWorld()
                .spawnEntity(player.getLocation(), EntityType.ZOMBIE);

        DataSection data = new DataSection();
        data.set("min-value-base", 20);
        data.set("min-value-scale", 0);
        data.set("max-value-base", 50);
        data.set("max-value-scale", 0);
        settings.set("data", data);

        condition = new AirCondition();
        condition.load(null, settings);
    }

    @Test
    void test_outOfBounds() {
        player.setRemainingAir(300);
        target.setRemainingAir(300);
        assertFalse(condition.test(player, 1, target));
    }

    void test_inBounds() {
        player.setRemainingAir(30);
        target.setRemainingAir(30);
        assertTrue(condition.test(player, 1, target));
    }

    void test_topOfBounds() {
        player.setRemainingAir(50);
        target.setRemainingAir(50);
        assertTrue(condition.test(player, 1, target));
    }

    void test_BottomOfBounds() {
        player.setRemainingAir(20);
        target.setRemainingAir(20);
        assertTrue(condition.test(player, 1, target));
    }

}