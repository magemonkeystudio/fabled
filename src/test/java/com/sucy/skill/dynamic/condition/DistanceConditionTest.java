package com.sucy.skill.dynamic.condition;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.sucy.skill.testutil.MockedTest;
import mc.promcteam.engine.mccore.config.parse.DataSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DistanceConditionTest extends MockedTest {
    private PlayerMock        player;
    private LivingEntity      target;
    private DistanceCondition condition;
    private DataSection       settings = new DataSection();

    @BeforeEach
    public void setup() {
        player = genPlayer("Travja");
        target = (LivingEntity) player.getWorld()
                .spawnEntity(player.getLocation(), EntityType.ZOMBIE);

        DataSection data = new DataSection();
        data.set("min-value-base", 1);
        data.set("min-value-scale", 0);
        data.set("max-value-base", 10);
        data.set("max-value-scale", 0);
        settings.set("data", data);

        condition = new DistanceCondition();
        condition.load(null, settings);
    }

    @Test
    void test_withinBounds() {
        target.teleport(player.getLocation().add(5, 0, 0));
        assertTrue(condition.test(player, 1, target));
    }

    @Test
    void test_topOfBounds() {
        target.teleport(player.getLocation().add(10, 0, 0));
        assertTrue(condition.test(player, 1, target));
    }

    @Test
    void test_bottomOfBounds() {
        target.teleport(player.getLocation().add(1, 0, 0));
        assertTrue(condition.test(player, 1, target));
    }

    @Test
    void test_outOfBounds() {
        target.teleport(player.getLocation().add(11, 0, 0));
        assertFalse(condition.test(player, 1, target));
        target.teleport(player.getLocation());
        assertFalse(condition.test(player, 1, target));
    }
}