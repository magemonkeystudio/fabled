package studio.magemonkey.fabled.dynamic.mechanic;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import studio.magemonkey.fabled.dynamic.DynamicSkill;
import studio.magemonkey.fabled.testutil.MockedTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AirSetMechanicTest extends MockedTest {
    private Player player;

    @BeforeEach
    public void setup() {
        player = this.genPlayer("Travja");
        player.setRemainingAir(300);
    }

    private AirSetMechanic getMechanic(double air) {
        AirSetMechanic mechanic = new AirSetMechanic();
        DynamicSkill skill  = new DynamicSkill("AirSet");

        DataSection  config = new DataSection();
        DataSection  data   = new DataSection();
        data.set("air", air);
        config.set("data", data);

        mechanic.load(skill, config);
        return mechanic;
    }

    @Test
    void execute_setMaxValue() {
        AirSetMechanic mechanic = getMechanic(20);

        mechanic.execute(player, 1, List.of(player), false);
        DynamicSkill.getCastData(player);

        assertEquals(player.getMaximumAir(), player.getRemainingAir());
    }

    @Test
    void execute_setValue() {
        AirSetMechanic mechanic = getMechanic(10);

        mechanic.execute(player, 1, List.of(player), false);

        assertEquals(10, player.getRemainingAir());
    }
}