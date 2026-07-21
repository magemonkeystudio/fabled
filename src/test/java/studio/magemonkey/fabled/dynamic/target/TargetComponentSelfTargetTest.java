package studio.magemonkey.fabled.dynamic.target;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import studio.magemonkey.fabled.dynamic.DynamicSkill;
import studio.magemonkey.fabled.testutil.MockedTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Covers TargetComponent#isValidTarget's self-targeting clause: previously self-targeting
 * was excluded unconditionally, regardless of the "caster" (IncludeCaster) setting.
 */
public class TargetComponentSelfTargetTest extends MockedTest {
    private Player caster;

    private static class StubTargetComponent extends TargetComponent {
        @Override
        public List<LivingEntity> getTargets(LivingEntity caster, int level, List<LivingEntity> targets) {
            return targets;
        }
    }

    @BeforeEach
    void setup() {
        caster = genPlayer("Travja");
    }

    private TargetComponent getComponent(String includeCaster) {
        TargetComponent component = new StubTargetComponent();
        DynamicSkill    skill     = new DynamicSkill("Test");
        DataSection     config    = new DataSection();
        DataSection     data      = new DataSection();
        // Use "both" so the ally/enemy grouping check never independently excludes the
        // caster as their own target - isolates the self-targeting clause under test.
        data.set("group", "both");
        if (includeCaster != null) data.set("caster", includeCaster);
        config.set("data", data);
        component.load(skill, config);
        return component;
    }

    @Test
    void isValidTarget_includeCasterFalse_excludesSelf() {
        TargetComponent component = getComponent("false");
        assertFalse(component.isValidTarget(caster, caster, caster));
    }

    @Test
    void isValidTarget_includeCasterTrue_allowsSelf() {
        TargetComponent component = getComponent("true");
        assertTrue(component.isValidTarget(caster, caster, caster));
    }

    @Test
    void isValidTarget_includeCasterInArea_allowsSelf() {
        TargetComponent component = getComponent("in area");
        assertTrue(component.isValidTarget(caster, caster, caster));
    }

    @Test
    void isValidTarget_default_stillExcludesSelf() {
        // "caster" unset defaults to FALSE, preserving prior default behavior.
        TargetComponent component = getComponent(null);
        assertFalse(component.isValidTarget(caster, caster, caster));
    }

    @Test
    void isValidTarget_includeCasterTrue_stillAllowsOtherTargets() {
        Player other = genPlayer("Other");
        TargetComponent component = getComponent("true");
        assertTrue(component.isValidTarget(caster, caster, other));
    }
}
