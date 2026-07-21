package studio.magemonkey.fabled.api.skills;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import studio.magemonkey.codex.registry.DamageRegistry;
import studio.magemonkey.fabled.testutil.MockedTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.mockStatic;

/**
 * Covers Skill#damage's try-finally: the static skillDamage flag must always be reset,
 * even if applying the damage throws, so a failure mid-damage doesn't leave the API
 * reporting every subsequent damage event as skill-caused.
 */
public class SkillDamageResetTest extends MockedTest {
    private Player caster;
    private Skill  skill;

    @BeforeEach
    void setup() {
        caster = genPlayer("Caster");
        skill = new Skill("test", "test", Material.APPLE, 5) {
        };
    }

    @Test
    void damage_exceptionDuringApplication_stillResetsSkillDamageFlag() {
        try (MockedStatic<DamageRegistry> damageRegistry = mockStatic(DamageRegistry.class)) {
            damageRegistry.when(() -> DamageRegistry.dealDamage(any(), anyDouble(), any(), any()))
                    .thenThrow(new RuntimeException("boom"));

            // Self-damage keeps Fabled's canAttack check trivially true (attacker == target)
            // so the failure being tested is isolated to the try block itself.
            assertThrows(RuntimeException.class,
                    () -> skill.damage(caster, 5, caster, "physical", true, true));
        }

        assertFalse(Skill.isSkillDamage());
    }

    @Test
    void damage_normalCase_resetsSkillDamageFlagAfterward() {
        skill.damage(caster, 5, caster, "physical", true, true);

        assertFalse(Skill.isSkillDamage());
    }
}
