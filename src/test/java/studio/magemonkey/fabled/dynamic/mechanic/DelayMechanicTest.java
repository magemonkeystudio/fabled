package studio.magemonkey.fabled.dynamic.mechanic;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import studio.magemonkey.fabled.dynamic.DynamicSkill;
import studio.magemonkey.fabled.testutil.MockedTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DelayMechanicTest extends MockedTest {
    private Player player;

    @BeforeEach
    public void setup() {
        player = this.genPlayer("Travja");
    }

    private DelayMechanic getMechanic(int delay, boolean cleanup, boolean singleInstance) {
        DelayMechanic mechanic = new DelayMechanic();

        DynamicSkill skill  = new DynamicSkill("Delay");
        DataSection  config = new DataSection();
        DataSection  data   = new DataSection();
        data.set("delay", delay);
        data.set("cleanup", cleanup);
        data.set("single-instance", singleInstance);
        config.set("data", data);

        mechanic.load(skill, config);
        return mechanic;
    }

    @Test
    void execute_cleansItselfUp() {
        DelayMechanic mechanic = getMechanic(2, false, false);

        mechanic.execute(player, 1, List.of(player), false);
        int taskId = mechanic.tasks.get(player.getUniqueId()).get(0);
        assertEquals(1, mechanic.tasks.get(player.getUniqueId()).size());
        assertTrue(server.getScheduler().isQueued(taskId));

        server.getScheduler().performTicks(40);
        List<Integer> tasks = mechanic.tasks.get(player.getUniqueId());
        assertEquals(0, tasks.size());
        assertFalse(server.getScheduler().isQueued(taskId));
    }

    @Test
    void execute_singleInstance() {
        DelayMechanic mechanic = getMechanic(2, false, true);

        mechanic.execute(player, 1, List.of(player), false);
        assertEquals(1, mechanic.tasks.get(player.getUniqueId()).size());
        int taskId = mechanic.tasks.get(player.getUniqueId()).get(0);
        server.getScheduler().performTicks(10);

        mechanic.execute(player, 1, List.of(player), false);
        assertEquals(1, mechanic.tasks.get(player.getUniqueId()).size());
        assertNotEquals(taskId, mechanic.tasks.get(player.getUniqueId()).get(0));
        assertFalse(server.getScheduler().isQueued(taskId));
    }

    @Test
    void execute_multipleCalls() {
        DelayMechanic mechanic = getMechanic(2, false, false);

        mechanic.execute(player, 1, List.of(player), false);
        assertEquals(1, mechanic.tasks.get(player.getUniqueId()).size());
        server.getScheduler().performTicks(20);

        mechanic.execute(player, 1, List.of(player), false);
        assertEquals(2, mechanic.tasks.get(player.getUniqueId()).size());
        server.getScheduler().performTicks(20);
        assertEquals(1, mechanic.tasks.get(player.getUniqueId()).size());
        server.getScheduler().performTicks(20);
        assertEquals(0, mechanic.tasks.get(player.getUniqueId()).size());
    }

    @Test
    void doCleanUp_cleanupFalse_doesNotClearTasks() {
        DelayMechanic mechanic = getMechanic(2, false, false);

        mechanic.execute(player, 1, List.of(player), false);
        assertEquals(1, mechanic.tasks.get(player.getUniqueId()).size());

        mechanic.doCleanUp(player);
        assertNotNull(mechanic.tasks.get(player.getUniqueId()));
        assertEquals(1, mechanic.tasks.get(player.getUniqueId()).size());
    }

    @Test
    void doCleanUp_cleanupTrue_clearsTasks() {
        DelayMechanic mechanic = getMechanic(2, true, false);

        mechanic.execute(player, 1, List.of(player), false);
        mechanic.execute(player, 1, List.of(player), false);
        assertEquals(2, mechanic.tasks.get(player.getUniqueId()).size());

        mechanic.doCleanUp(player);
        assertNull(mechanic.tasks.get(player.getUniqueId()));
    }
}