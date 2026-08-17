package studio.magemonkey.fabled.dynamic.mechanic;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import studio.magemonkey.fabled.dynamic.DynamicSkill;
import studio.magemonkey.fabled.hook.PluginChecker;
import studio.magemonkey.fabled.testutil.MockedTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Covers HealMechanic's Divinity integration is properly gated behind
 * PluginChecker.isDivinityActive() - without that guard, calling Divinity's
 * EntityStats API on a server that doesn't have Divinity installed throws
 * NoClassDefFoundError (a LinkageError, not an Exception), which the mechanic's own
 * catch(Exception) can't catch.
 */
public class HealMechanicTest extends MockedTest {
    private Player caster;

    @BeforeEach
    void setup() {
        caster = genPlayer("Travja");
        caster.setHealth(10);
    }

    private HealMechanic getMechanic(double value, boolean percent) {
        HealMechanic mechanic = new HealMechanic();
        DynamicSkill skill    = new DynamicSkill("Heal");
        DataSection  config   = new DataSection();
        DataSection  data     = new DataSection();
        data.set("type", percent ? "percent" : "health");
        data.set("value-base", value);
        data.set("value-scale", 0);
        config.set("data", data);
        mechanic.load(skill, config);
        return mechanic;
    }

    @Test
    void execute_divinityInactive_appliesUnmodifiedHealAmount() {
        assertFalse(PluginChecker.isDivinityActive());

        HealMechanic mechanic = getMechanic(5, false);
        boolean      result   = mechanic.execute(caster, 1, List.of(caster), false);

        assertTrue(result);
        assertEquals(15, caster.getHealth());
    }

    @Test
    void execute_divinityInactive_doesNotThrow() {
        HealMechanic mechanic = getMechanic(5, false);

        // Regression guard: previously an ungated Divinity API call here would throw
        // NoClassDefFoundError when Divinity isn't installed.
        mechanic.execute(caster, 1, List.of(caster), false);
    }
}
