package studio.magemonkey.fabled.api.util;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import studio.magemonkey.fabled.api.event.FlagExpireEvent;
import studio.magemonkey.fabled.testutil.MockedTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Covers the infinite-recursion fix in FlagManager.clearFlags: a FlagExpireTrigger-driven
 * skill can add a new flag to an entity while that entity's flags are being cleared (e.g.
 * on death), which previously re-entered clearFlags for the same entity and recursed forever.
 */
public class FlagManagerReentrancyTest extends MockedTest implements Listener {
    private LivingEntity entity;

    @BeforeEach
    void setup() {
        entity = genPlayer("ReentrancyTester");
    }

    private void reflagOnExpire(String expiredFlag, String newFlag) {
        server.getPluginManager().registerEvent(FlagExpireEvent.class, this, EventPriority.NORMAL,
                (listener, event) -> {
                    FlagExpireEvent e = (FlagExpireEvent) event;
                    if (e.getEntity().equals(entity) && e.getFlag().equals(expiredFlag)) {
                        FlagManager.addFlag(entity, newFlag, 100);
                    }
                }, plugin, true);
    }

    @Test
    void clearFlags_reentrantAddDuringExpire_doesNotRecurseForever() {
        reflagOnExpire("initial", "reflag");

        FlagManager.addFlag(entity, "initial", 100);

        assertDoesNotThrow(() -> FlagManager.clearFlags(entity));
    }

    @Test
    void clearFlags_reentrantAdd_leavesNewlyAddedFlagIntact() {
        reflagOnExpire("initial", "reflag");

        FlagManager.addFlag(entity, "initial", 100);
        FlagManager.clearFlags(entity);

        assertFalse(FlagManager.hasFlag(entity, "initial"));
        assertTrue(FlagManager.hasFlag(entity, "reflag"));
    }

    @Test
    void clearFlags_selfReflagLoop_doesNotRecurseForever() {
        // A skill that keeps re-adding the same flag on every expire would, without the
        // guard, cause clear() -> removeFlag() -> expire event -> addFlag() -> (flags now
        // empty again) -> clearFlags() -> clear() -> ... indefinitely for the same entity.
        reflagOnExpire("loopy", "loopy");

        FlagManager.addFlag(entity, "loopy", 100);

        assertDoesNotThrow(() -> FlagManager.clearFlags(entity));
        // The flag re-added mid-clear should survive the clear rather than being dropped.
        assertTrue(FlagManager.hasFlag(entity, "loopy"));
    }

    @Test
    void clearFlags_normalCase_clearsAllFlags() {
        FlagManager.addFlag(entity, "a", 100);
        FlagManager.addFlag(entity, "b", 100);

        FlagManager.clearFlags(entity);

        assertFalse(FlagManager.hasFlag(entity, "a"));
        assertFalse(FlagManager.hasFlag(entity, "b"));
    }

    @Test
    void clearFlags_nullEntity_doesNotThrow() {
        assertDoesNotThrow(() -> FlagManager.clearFlags(null));
    }
}
