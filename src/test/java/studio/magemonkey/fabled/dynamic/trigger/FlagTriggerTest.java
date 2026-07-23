package studio.magemonkey.fabled.dynamic.trigger;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.Settings;
import studio.magemonkey.fabled.api.event.FlagApplyEvent;
import studio.magemonkey.fabled.dynamic.DynamicSkill;
import studio.magemonkey.fabled.testutil.MockedTest;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mockStatic;

class FlagTriggerTest extends MockedTest {
    private FlagTrigger                trigger;
    private Player                     entity;
    private MockedStatic<DynamicSkill> dynamicSkill;
    private CastData                   data;

    @BeforeEach
    void setup() {
        trigger = new FlagTrigger();
        entity = genPlayer("Travja");
        dynamicSkill = mockStatic(DynamicSkill.class);
        data = new CastData(entity);
        dynamicSkill.when(() -> DynamicSkill.getCastData(any())).thenReturn(data);
    }

    @AfterEach
    void tearDown() {
        dynamicSkill.close();
        dynamicSkill = null;
    }

    private Settings settingsWithFlags(List<String> flags) {
        Settings settings = new Settings();
        settings.set("flags", flags);
        return settings;
    }

    @Test
    void shouldTrigger_staticFlagMatch() {
        FlagApplyEvent event = new FlagApplyEvent(entity, "stunned", 100);
        assertTrue(trigger.shouldTrigger(event, 1, settingsWithFlags(List.of("stunned"))));
    }

    @Test
    void shouldTrigger_dynamicPlayerPlaceholder_resolvesAgainstEntityName() {
        FlagApplyEvent event = new FlagApplyEvent(entity, "marked-" + entity.getName(), 100);
        assertTrue(trigger.shouldTrigger(event, 1, settingsWithFlags(List.of("marked-{player}"))));
    }

    @Test
    void shouldTrigger_dynamicCastDataPlaceholder() {
        data.put("element", "fire");
        FlagApplyEvent event = new FlagApplyEvent(entity, "burning-fire", 100);
        assertTrue(trigger.shouldTrigger(event, 1, settingsWithFlags(List.of("burning-{element}"))));
    }

    @Test
    void shouldTrigger_dynamicPlaceholder_noMatchWhenResolvedDiffers() {
        FlagApplyEvent event = new FlagApplyEvent(entity, "marked-someoneElse", 100);
        assertFalse(trigger.shouldTrigger(event, 1, settingsWithFlags(List.of("marked-{player}"))));
    }

    @Test
    void shouldTrigger_emptyFlagsList_matchesAny() {
        FlagApplyEvent event = new FlagApplyEvent(entity, "anything", 100);
        assertTrue(trigger.shouldTrigger(event, 1, settingsWithFlags(Collections.emptyList())));
    }

    @Test
    void shouldTrigger_minDurationNotMet_returnsFalse() {
        FlagApplyEvent event = new FlagApplyEvent(entity, "stunned", 10);
        Settings settings = settingsWithFlags(List.of("stunned"));
        settings.set("min-duration", 5.0);
        assertFalse(trigger.shouldTrigger(event, 1, settings));
    }
}
