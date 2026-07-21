package studio.magemonkey.fabled.dynamic.mechanic;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import studio.magemonkey.fabled.api.util.FlagManager;
import studio.magemonkey.fabled.dynamic.DynamicSkill;
import studio.magemonkey.fabled.hook.PluginChecker;
import studio.magemonkey.fabled.testutil.MockedTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Covers StatusMechanic's Divinity CC-duration/CC-resistance integration is gated behind
 * PluginChecker.isDivinityActive() - see HealMechanicTest for why an ungated call is a
 * real crash risk on servers without Divinity installed.
 */
public class StatusMechanicTest extends MockedTest {
    private Player caster;
    private Player target;

    @BeforeEach
    void setup() {
        caster = genPlayer("Caster");
        target = genPlayer("Target");
    }

    private StatusMechanic getMechanic(String key, double durationSeconds) {
        StatusMechanic mechanic = new StatusMechanic();
        DynamicSkill   skill    = new DynamicSkill("Status");
        DataSection    config   = new DataSection();
        DataSection    data     = new DataSection();
        data.set("status", key);
        data.set("duration-base", durationSeconds);
        data.set("duration-scale", 0);
        config.set("data", data);
        mechanic.load(skill, config);
        return mechanic;
    }

    @Test
    void execute_divinityInactive_stillAppliesFlag() {
        assertFalse(PluginChecker.isDivinityActive());

        StatusMechanic mechanic = getMechanic("stunned", 5.0);
        boolean        result   = mechanic.execute(caster, 1, List.of(target), false);

        assertTrue(result);
        assertTrue(FlagManager.hasFlag(target, "stunned"));
    }

    @Test
    void execute_divinityInactive_doesNotThrow() {
        StatusMechanic mechanic = getMechanic("stunned", 5.0);

        // Regression guard: previously an ungated Divinity API call here would throw
        // NoClassDefFoundError when Divinity isn't installed.
        mechanic.execute(caster, 1, List.of(target), false);
    }
}
