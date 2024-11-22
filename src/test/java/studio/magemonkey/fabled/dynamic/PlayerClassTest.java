package studio.magemonkey.fabled.dynamic;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.mockbukkit.mockbukkit.world.Coordinate;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.classes.FabledClass;
import studio.magemonkey.fabled.api.event.DynamicTriggerEvent;
import studio.magemonkey.fabled.api.player.PlayerClass;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.api.player.PlayerSkill;
import studio.magemonkey.fabled.testutil.MockedTest;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerClassTest extends MockedTest {
    private PlayerMock  player;
    private PlayerData  playerData;
    private PlayerSkill skill;

    @Override
    public void preInit() {
        loadClasses("Honor Guard", "Mage");
        loadSkills("NonCastSkill");
    }

    @BeforeEach
    public void setup() {
        player = genPlayer("Travja");
        playerData = Fabled.getData(player);

        assertNotNull(Fabled.getClass("Honor Guard"));
        assertNotNull(Fabled.getClass("Mage"));
        assertNotNull(Fabled.getSkill("NonCastSkill"));


        FabledClass pClass = Fabled.getClass("Honor Guard");
        playerData.setClass(null, pClass, true);
        skill = playerData.getSkill("NonCastSkill");
        assertNotNull(skill);

        playerData.setPoints(1);
        playerData.upgradeSkill(skill.getData());
        assertEquals(1, skill.getLevel());

        player.setOp(true);
    }

    @Test
    public void changeClassRemovesBoundSkillsAndUnregistersListenersForSkills() {
        Block block = world.createBlock(new Coordinate(0, 0, 0));
        block.setType(Material.DIAMOND_BLOCK);

        player.simulateBlockBreak(block);
        assertEquals(Material.AIR, block.getType());

        assertEventFired(BlockBreakEvent.class);
        assertEventFired(DynamicTriggerEvent.class, event -> event.getSkill().equals(skill.getData()));

        assertFalse(playerData.getSkills().isEmpty());
        player.performCommand("class changeclass Travja class Mage");

        PlayerClass pClass = playerData.getMainClass();
        assertNotNull(pClass);
        assertEquals("Mage", pClass.getData().getName());

        server.getPluginManager().clearEvents();
        block.setType(Material.DIAMOND_BLOCK);
        player.simulateBlockBreak(block);
        assertEquals(Material.AIR, block.getType());

        assertEventFired(BlockBreakEvent.class);
        assertEventNotFired(DynamicTriggerEvent.class);
    }
}
