package studio.magemonkey.fabled.dynamic.mechanic;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.util.FlagManager;
import studio.magemonkey.fabled.dynamic.DynamicSkill;
import studio.magemonkey.fabled.testutil.MockedTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mockStatic;

public class FlagMechanicTest extends MockedTest {
    private Player                     caster;
    private Player                     target;
    private MockedStatic<DynamicSkill> dynamicSkill;
    private CastData                   data;

    @BeforeEach
    public void setup() {
        caster = genPlayer("Travja");
        target = genPlayer("goflish");
        dynamicSkill = mockStatic(DynamicSkill.class);
        data = new CastData(caster);
        dynamicSkill.when(() -> DynamicSkill.getCastData(any())).thenReturn(data);
    }

    @AfterEach
    public void tearDown() {
        dynamicSkill.close();
        dynamicSkill = null;
    }

    private FlagMechanic getMechanic(String key) {
        FlagMechanic  mechanic = new FlagMechanic();
        DynamicSkill  skill    = new DynamicSkill("Flag");
        DataSection   config   = new DataSection();
        DataSection   settings = new DataSection();
        settings.set("key", key);
        settings.set("seconds", 5.0);
        config.set("data", settings);
        mechanic.load(skill, config);
        return mechanic;
    }

    @Test
    void execute_staticKey_appliesLiteralFlag() {
        FlagMechanic mechanic = getMechanic("stunned");
        boolean      result   = mechanic.execute(caster, 1, List.of(target), false);

        assertTrue(result);
        assertTrue(FlagManager.hasFlag(target, "stunned"));
    }

    @Test
    void execute_dynamicKey_resolvesPerTargetPlaceholder() {
        Player secondTarget = genPlayer("Secondary");

        FlagMechanic mechanic = getMechanic("marked-{target}");
        mechanic.execute(caster, 1, List.of(target, secondTarget), false);

        assertTrue(FlagManager.hasFlag(target, "marked-" + target.getName()));
        assertTrue(FlagManager.hasFlag(secondTarget, "marked-" + secondTarget.getName()));
        assertFalse(FlagManager.hasFlag(target, "marked-" + secondTarget.getName()));
    }

    @Test
    void execute_dynamicKey_resolvesCastDataPlaceholder() {
        data.put("element", "fire");

        FlagMechanic mechanic = getMechanic("burning-{element}");
        mechanic.execute(caster, 1, List.of(target), false);

        assertTrue(FlagManager.hasFlag(target, "burning-fire"));
    }
}
