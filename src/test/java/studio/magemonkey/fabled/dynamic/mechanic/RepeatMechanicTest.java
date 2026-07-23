package studio.magemonkey.fabled.dynamic.mechanic;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import studio.magemonkey.fabled.dynamic.DynamicSkill;
import studio.magemonkey.fabled.testutil.MockedTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RepeatMechanicTest extends MockedTest {
    private Player player;

    @BeforeEach
    public void setup() {
        player = this.genPlayer("Travja");
        // The caster must be considered "active" and not cancelled for the repeat's
        // per-tick execute() (and thus task scheduling) to keep going; DynamicSkill's
        // default skill/level lookups need real class/skill data behind them, so route
        // through Fabled's dynamic skill registration like other mechanic tests do.
    }

    private RepeatMechanic getMechanic(int repetitions, double delay, double period, boolean singleInstance) {
        RepeatMechanic mechanic = new RepeatMechanic();

        DynamicSkill skill  = new DynamicSkill("Repeat");
        DataSection  config = new DataSection();
        DataSection  data   = new DataSection();
        data.set("repetitions-base", (double) repetitions);
        data.set("repetitions-scale", 0);
        data.set("delay", delay);
        data.set("period", period);
        data.set("single-instance", singleInstance);
        config.set("data", data);

        mechanic.load(skill, config);
        return mechanic;
    }

    @Test
    void execute_withoutSingleInstance_stacksMultipleTasks() {
        RepeatMechanic mechanic = getMechanic(3, 0, 1, false);

        mechanic.execute(player, 1, List.of(player), true);
        mechanic.execute(player, 1, List.of(player), true);

        assertEquals(2, mechanic.tasks.get(player.getEntityId()).size());
    }

    @Test
    void execute_singleInstance_cancelsPreviousTaskBeforeStartingNew() {
        RepeatMechanic mechanic = getMechanic(3, 0, 1, true);

        mechanic.execute(player, 1, List.of(player), true);
        assertEquals(1, mechanic.tasks.get(player.getEntityId()).size());

        // Without single-instance this would grow to 2 (see the test above); with it,
        // the previous task is cancelled and removed before the new one is added.
        mechanic.execute(player, 1, List.of(player), true);
        assertEquals(1, mechanic.tasks.get(player.getEntityId()).size());
    }

    @Test
    void execute_singleInstance_firstInvocationBehavesNormally() {
        RepeatMechanic mechanic = getMechanic(3, 0, 1, true);

        boolean result = mechanic.execute(player, 1, List.of(player), true);

        assertTrue(result);
        assertEquals(1, mechanic.tasks.get(player.getEntityId()).size());
    }

    @Test
    void execute_noTargets_returnsFalse() {
        RepeatMechanic mechanic = getMechanic(3, 0, 1, false);

        boolean result = mechanic.execute(player, 1, List.of(), true);

        assertFalse(result);
    }
}
