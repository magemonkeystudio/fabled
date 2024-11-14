package studio.magemonkey.fabled.dynamic.mechanic;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import studio.magemonkey.fabled.dynamic.DynamicSkill;
import studio.magemonkey.fabled.testutil.MockedTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AirModifyMechanicTest extends MockedTest {
    private Player player;

    @BeforeEach
    public void setup() {
        player = this.genPlayer("Travja");
    }

    private AirModifyMechanic getMechanic(double air) {
        AirModifyMechanic mechanic = new AirModifyMechanic();
        DynamicSkill skill  = new DynamicSkill("AirModify");

        DataSection  config = new DataSection();
        DataSection  data   = new DataSection();
        data.set("air-base", air);
        data.set("air-scale", 0);
        config.set("data", data);

        mechanic.load(skill, config);
        return mechanic;
    }

    @Test
    void execute_addToMaxValue() {
        AirModifyMechanic mechanic = getMechanic(20);

        mechanic.execute(player, 1, List.of(player), true);

        assertEquals(player.getMaximumAir(), player.getRemainingAir());

    }

    void execute_addToMinValue() {
        AirModifyMechanic mechanic = getMechanic(-30);

        mechanic.execute(player, 1, List.of(player), true);

        assertEquals(-20, player.getRemainingAir());

    }

    void execute_addOne() {
        AirModifyMechanic mechanic = getMechanic(1);

        player.setRemainingAir(100);

        mechanic.execute(player, 1, List.of(player), true);

        assertEquals(120, player.getRemainingAir());

    }

    void execute_SubtractOne() {
        AirModifyMechanic mechanic = getMechanic(-1);

        player.setRemainingAir(100);

        mechanic.execute(player, 1, List.of(player), true);

        assertEquals(80, player.getRemainingAir());

    }
}