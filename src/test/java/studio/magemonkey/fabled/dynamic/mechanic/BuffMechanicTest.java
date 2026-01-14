package studio.magemonkey.fabled.dynamic.mechanic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import studio.magemonkey.fabled.api.Settings;
import studio.magemonkey.fabled.api.util.BuffData;
import studio.magemonkey.fabled.api.util.BuffManager;
import studio.magemonkey.fabled.dynamic.DynamicSkill;
import studio.magemonkey.fabled.dynamic.EffectComponent;
import studio.magemonkey.fabled.testutil.MockedTest;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BuffMechanicTest extends MockedTest {
    private Settings     settings;
    private BuffMechanic buffMechanic;
    private PlayerMock   caster;
    private PlayerMock   target;

    @BeforeEach
    void setUp() throws Exception {
        caster = genPlayer("Caster");
        target = genPlayer("Target");

        settings = mock(Settings.class);
        buffMechanic = new BuffMechanic();

        // Inject settings
        Field settingsField = EffectComponent.class.getDeclaredField("settings");
        settingsField.setAccessible(true);
        settingsField.set(buffMechanic, settings);

        // Inject skill
        Field skillField = EffectComponent.class.getDeclaredField("skill");
        skillField.setAccessible(true);
        DynamicSkill skill = mock(DynamicSkill.class);
        when(skill.getName()).thenReturn("TestSkill");
        skillField.set(buffMechanic, skill);
    }

    @Test
    void testExecute_SkillDamage_AddsBuffWithCorrectKey() throws Exception {
        // Setup settings for a SKILL_DAMAGE buff with a category
        when(settings.getString("type", "DAMAGE")).thenReturn("SKILL_DAMAGE");
        when(settings.getString("category", null)).thenReturn("mycategory");
        when(settings.getString("value")).thenReturn("10");
        when(settings.getString("seconds")).thenReturn("5");
        when(settings.getString("immediate", "false")).thenReturn("false");
        when(settings.getString("modifier", "flat")).thenReturn("flat");

        // Execute
        buffMechanic.execute(caster, 1, List.of(target), false);

        // Verify BuffData
        BuffData buffData   = BuffManager.getBuffData(target);
        Field    buffsField = BuffData.class.getDeclaredField("buffs");
        buffsField.setAccessible(true);
        @SuppressWarnings("unchecked") Map<String, Map<String, Object>> buffs =
                (Map<String, Map<String, Object>>) buffsField.get(buffData);

        // Check keys
        System.out.println("Registered Buff Keys: " + buffs.keySet());

        // We expect "FABLED_skill_damage_mycategory"
        assertTrue(buffs.containsKey("FABLED_skill_damage_mycategory"),
                "Should contain correct key FABLED_skill_damage_mycategory");
        assertFalse(buffs.containsKey("FABLED_skill_damage_mycategory_mycategory"),
                "Should NOT contain double-appended category key FABLED_skill_damage_mycategory_mycategory");
    }

    @Test
    void testExecute_Damage_IgnoresCategory() throws Exception {
        // Setup settings for a DAMAGE buff with a category
        when(settings.getString("type", "DAMAGE")).thenReturn("DAMAGE");
        when(settings.getString("category", null)).thenReturn("mycategory");
        when(settings.getString("value")).thenReturn("10");
        when(settings.getString("seconds")).thenReturn("5");
        when(settings.getString("immediate", "false")).thenReturn("false");
        when(settings.getString("modifier", "flat")).thenReturn("flat");

        // Execute
        buffMechanic.execute(caster, 1, List.of(target), false);

        // Verify BuffData
        BuffData buffData   = BuffManager.getBuffData(target);
        Field    buffsField = BuffData.class.getDeclaredField("buffs");
        buffsField.setAccessible(true);
        @SuppressWarnings("unchecked") Map<String, Map<String, Object>> buffs =
                (Map<String, Map<String, Object>>) buffsField.get(buffData);

        // Check keys
        System.out.println("Registered Buff Keys: " + buffs.keySet());

        // We expect "FABLED_damage" (category ignored)
        assertTrue(buffs.containsKey("FABLED_damage"), "Should contain correct key FABLED_damage");
        assertFalse(buffs.containsKey("FABLED_damage_mycategory"),
                "Should NOT contain category key FABLED_damage_mycategory");
    }
}
