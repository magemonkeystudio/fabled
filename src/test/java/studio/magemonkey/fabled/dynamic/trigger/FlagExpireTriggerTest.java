package studio.magemonkey.fabled.dynamic.trigger;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.Settings;
import studio.magemonkey.fabled.api.event.FlagExpireEvent;
import studio.magemonkey.fabled.dynamic.DynamicSkill;
import studio.magemonkey.fabled.testutil.MockedTest;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mockStatic;

class FlagExpireTriggerTest extends MockedTest {
    private FlagExpireTrigger          trigger;
    private Player                     entity;
    private MockedStatic<DynamicSkill> dynamicSkill;
    private CastData                   data;

    @BeforeEach
    void setup() {
        trigger = new FlagExpireTrigger();
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
        FlagExpireEvent event =
                new FlagExpireEvent(entity, "stunned", FlagExpireEvent.ExpireReason.TIME);
        assertTrue(trigger.shouldTrigger(event, 1, settingsWithFlags(List.of("stunned"))));
    }

    @Test
    void shouldTrigger_dynamicPlayerPlaceholder_resolvesAgainstEntityName() {
        FlagExpireEvent event = new FlagExpireEvent(entity, "marked-" + entity.getName(),
                FlagExpireEvent.ExpireReason.REMOVED);
        assertTrue(trigger.shouldTrigger(event, 1, settingsWithFlags(List.of("marked-{player}"))));
    }

    @Test
    void shouldTrigger_dynamicCastDataPlaceholder() {
        data.put("element", "fire");
        FlagExpireEvent event =
                new FlagExpireEvent(entity, "burning-fire", FlagExpireEvent.ExpireReason.TIME);
        assertTrue(trigger.shouldTrigger(event, 1, settingsWithFlags(List.of("burning-{element}"))));
    }

    @Test
    void shouldTrigger_dynamicPlaceholder_noMatchWhenResolvedDiffers() {
        FlagExpireEvent event =
                new FlagExpireEvent(entity, "marked-someoneElse", FlagExpireEvent.ExpireReason.TIME);
        assertFalse(trigger.shouldTrigger(event, 1, settingsWithFlags(List.of("marked-{player}"))));
    }

    @Test
    void shouldTrigger_emptyFlagsList_matchesAny() {
        FlagExpireEvent event =
                new FlagExpireEvent(entity, "anything", FlagExpireEvent.ExpireReason.TIME);
        assertTrue(trigger.shouldTrigger(event, 1, settingsWithFlags(Collections.emptyList())));
    }

    @Test
    void shouldTrigger_inverted_flipsMatch() {
        FlagExpireEvent event =
                new FlagExpireEvent(entity, "stunned", FlagExpireEvent.ExpireReason.TIME);
        Settings settings = settingsWithFlags(List.of("stunned"));
        settings.set("inverted", true);
        assertFalse(trigger.shouldTrigger(event, 1, settings));
    }
}
