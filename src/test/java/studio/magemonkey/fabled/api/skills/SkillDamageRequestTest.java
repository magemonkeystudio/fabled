package studio.magemonkey.fabled.api.skills;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import studio.magemonkey.codex.api.damage.DamageContexts;
import studio.magemonkey.codex.api.damage.DamageOrigin;
import studio.magemonkey.codex.api.damage.DamageRequest;
import studio.magemonkey.codex.api.damage.WeaponStatsMode;
import studio.magemonkey.codex.registry.DamageRegistry;
import studio.magemonkey.fabled.api.event.SkillDamageEvent;
import studio.magemonkey.fabled.testutil.MockedTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Skill#damage describes the hit with a Codex damage request, kept open for exactly the damage call.
 * <p>
 * MockBukkit's damage(amount, source) fires no damage event, so the tests ask Codex the way Divinity
 * does: they build an EntityDamageByEntityEvent themselves from inside the damage call and claim it.
 */
public class SkillDamageRequestTest extends MockedTest {
    private static final EntityDamageEvent.DamageCause CAUSE = EntityDamageEvent.DamageCause.MAGIC;

    private Player caster;
    private Zombie target;
    private Skill  skill;

    /** What was open for the target at the moment the damage call ran */
    private final List<Boolean>            announcedInDamageCall = new ArrayList<>();
    private final List<DamageRequest>      claimed               = new ArrayList<>();
    private final List<Double>             registryAmounts       = new ArrayList<>();
    /** What was open at the moment the throwaway events and the SkillDamageEvent were fired */
    private final List<Boolean>            announcedOnFakeEvent  = new ArrayList<>();
    private final List<Boolean>            announcedOnSkillEvent = new ArrayList<>();
    private final AtomicReference<Boolean> cancelSkillEvent      = new AtomicReference<>(false);

    private final Listener probe = new Listener() {
        @EventHandler(priority = EventPriority.MONITOR)
        public void onFake(EntityDamageByEntityEvent event) {
            // Only canAttack's throwaway events are dispatched; the test's own events never are
            announcedOnFakeEvent.add(DamageContexts.isAnnounced(event.getEntity()));
        }

        @EventHandler
        public void onSkillDamage(SkillDamageEvent event) {
            announcedOnSkillEvent.add(DamageContexts.isAnnounced(event.getTarget()));
            if (cancelSkillEvent.get()) event.setCancelled(true);
        }
    };

    @BeforeEach
    void setup() {
        caster = genPlayer("Caster");
        target = spy(world.spawn(new Location(world, 0, 0, 0), Zombie.class));
        skill = new Skill("test", "test", Material.APPLE, 5) {
        };
        server.getPluginManager().registerEvents(probe, plugin);
        // The Bukkit fallback: look at what is open while the entity is being damaged
        doAnswer(inv -> {
            observe();
            return null;
        }).when(target).damage(anyDouble(), any(Entity.class));
    }

    @AfterEach
    void cleanup() {
        HandlerList.unregisterAll(probe);
        announcedInDamageCall.clear();
        claimed.clear();
        registryAmounts.clear();
        announcedOnFakeEvent.clear();
        announcedOnSkillEvent.clear();
        cancelSkillEvent.set(false);
    }

    /** What Divinity does when the damage event for the target arrives */
    private void observe() {
        announcedInDamageCall.add(DamageContexts.isAnnounced(target));
        DamageRequest request = DamageContexts.claim(new EntityDamageByEntityEvent(caster, target, CAUSE, 1));
        if (request != null) claimed.add(request);
    }

    /** DamageRegistry hands the damage over to Bukkit (returns false), like it does for unknown classifications */
    private MockedStatic<DamageRegistry> registryFallsBack() {
        MockedStatic<DamageRegistry> registry = mockStatic(DamageRegistry.class);
        registry.when(() -> DamageRegistry.dealDamage(any(), anyDouble(), any(), any())).thenAnswer(inv -> {
            registryAmounts.add(inv.getArgument(1));
            return false;
        });
        return registry;
    }

    /** A claimed request still counts as open until its scope is closed, which isAnnounced/claim can't tell */
    private void assertNoScopeRegistered() {
        try {
            java.lang.reflect.Field field = DamageContexts.class.getDeclaredField("OPEN");
            field.setAccessible(true);
            assertFalse(((java.util.Map<?, ?>) field.get(null)).containsKey(target.getUniqueId()),
                    "a scope was never closed");
        } catch (ReflectiveOperationException e) {
            fail("Codex no longer keeps open scopes in DamageContexts.OPEN, update this check: " + e);
        }
    }

    private void assertNothingOpen() {
        assertNoScopeRegistered();
        assertFalse(DamageContexts.isAnnounced(target));
        assertNull(DamageContexts.claim(new EntityDamageByEntityEvent(caster, target, CAUSE, 1)),
                "a request was left open for the target");
    }

    @Test
    void damage_describesTheHit_addMode() {
        try (MockedStatic<DamageRegistry> ignored = registryFallsBack()) {
            skill.damage(target, 7.5, caster, "physical", true, false, CAUSE);
        }

        assertEquals(List.of(true), announcedInDamageCall);
        assertEquals(1, claimed.size());
        DamageRequest request = claimed.get(0);
        assertEquals(DamageOrigin.SKILL, request.getOrigin());
        assertSame(caster, request.getSource());
        assertEquals(CAUSE, request.getCause());
        assertEquals(WeaponStatsMode.ADD, request.getWeaponStats());
        assertTrue(request.hasKnockback());
        assertEquals(java.util.Map.of("physical", 7.5), request.getAmounts());
        assertNothingOpen();
    }

    @Test
    void damage_ignoreDivinity_usesIgnoreMode() {
        try (MockedStatic<DamageRegistry> ignored = registryFallsBack()) {
            skill.damage(target, 3, caster, "DIVINITY_fire", false, true, CAUSE);
        }

        assertEquals(1, claimed.size());
        DamageRequest request = claimed.get(0);
        assertEquals(WeaponStatsMode.IGNORE, request.getWeaponStats());
        assertFalse(request.hasKnockback());
        assertEquals(java.util.Map.of("DIVINITY_fire", 3.0), request.getAmounts());
        assertNothingOpen();
    }

    @Test
    void damage_requestIsOpenWhileTheRegistryDealsTheDamage() {
        AtomicReference<DamageRequest> seen = new AtomicReference<>();
        try (MockedStatic<DamageRegistry> registry = mockStatic(DamageRegistry.class)) {
            registry.when(() -> DamageRegistry.dealDamage(any(), anyDouble(), any(), any())).thenAnswer(inv -> {
                seen.set(DamageContexts.claim(new EntityDamageByEntityEvent(caster, target, CAUSE, 1)));
                return true;
            });
            skill.damage(target, 4, caster, "DIVINITY_fire", true, false, CAUSE);
        }

        assertNotNull(seen.get());
        assertEquals(java.util.Map.of("DIVINITY_fire", 4.0), seen.get().getAmounts());
        assertNothingOpen();
    }

    @Test
    void damage_nothingOpenWhileCanAttackAndSkillDamageEventRun() {
        try (MockedStatic<DamageRegistry> ignored = registryFallsBack()) {
            skill.damage(target, 5, caster, "physical", true, false, CAUSE);
        }

        // Guard against the probes silently never running
        assertFalse(announcedOnFakeEvent.isEmpty(), "canAttack fired no throwaway event");
        assertEquals(1, announcedOnSkillEvent.size());
        assertTrue(announcedOnFakeEvent.stream().noneMatch(b -> b), "request open during canAttack");
        assertTrue(announcedOnSkillEvent.stream().noneMatch(b -> b), "request open during SkillDamageEvent");
        // and the real hit still got its request
        assertEquals(1, claimed.size());
    }

    @Test
    void damage_closesTheRequestWhenTheDamageCallThrows() {
        try (MockedStatic<DamageRegistry> registry = mockStatic(DamageRegistry.class)) {
            registry.when(() -> DamageRegistry.dealDamage(any(), anyDouble(), any(), any()))
                    .thenThrow(new RuntimeException("boom"));

            assertThrows(RuntimeException.class,
                    () -> skill.damage(target, 5, caster, "physical", true, false, CAUSE));
        }

        assertNothingOpen();
        assertFalse(Skill.isSkillDamage());
    }

    @Test
    void damage_closesTheRequestWhenTheBukkitCallThrows() {
        doThrow(new IllegalStateException("boom")).when(target).damage(anyDouble(), any(Entity.class));
        try (MockedStatic<DamageRegistry> ignored = registryFallsBack()) {
            assertThrows(IllegalStateException.class,
                    () -> skill.damage(target, 5, caster, "physical", true, false, CAUSE));
        }

        assertNothingOpen();
    }

    @Test
    void damage_cancelledSkillDamageEvent_opensNothing() {
        cancelSkillEvent.set(true);
        try (MockedStatic<DamageRegistry> registry = registryFallsBack()) {
            skill.damage(target, 5, caster, "physical", true, false, CAUSE);
            registry.verifyNoInteractions();
        }

        assertTrue(announcedInDamageCall.isEmpty());
        assertTrue(claimed.isEmpty());
        assertNothingOpen();
    }

    @Test
    void damage_negativeDamage_doesNotThrowAndReachesBukkitUnchanged() {
        // BuffListener cancels damage <= 0 at LOW, so a negative amount can only get here from a later handler
        Listener negate = new Listener() {
            @EventHandler(priority = EventPriority.HIGHEST)
            public void onSkillDamage(SkillDamageEvent event) {
                event.setDamage(-5);
            }
        };
        server.getPluginManager().registerEvents(negate, plugin);
        try (MockedStatic<DamageRegistry> ignored = registryFallsBack()) {
            assertDoesNotThrow(() -> skill.damage(target, 5, caster, "physical", true, false, CAUSE));
        } finally {
            HandlerList.unregisterAll(negate);
        }

        assertEquals(List.of(-5.0), registryAmounts);
        verify(target).damage(-5.0, caster);
        assertEquals(java.util.Map.of("physical", 0.0), claimed.get(0).getAmounts());
        assertNothingOpen();
    }

    @Test
    void damage_nanDamage_doesNotThrowAndRequestHoldsZero() {
        try (MockedStatic<DamageRegistry> ignored = registryFallsBack()) {
            assertDoesNotThrow(() -> skill.damage(target, Double.NaN, caster, "physical", true, false, CAUSE));
        }

        assertEquals(java.util.Map.of("physical", 0.0), claimed.get(0).getAmounts());
        verify(target).damage(eq(Double.NaN), eq(caster));
        assertNothingOpen();
    }

    @Test
    void damage_bukkitCallReceivesTheOriginalAmountAndSource() {
        try (MockedStatic<DamageRegistry> ignored = registryFallsBack()) {
            skill.damage(target, 6.25, caster, "physical", true, false, CAUSE);
        }

        assertEquals(List.of(6.25), registryAmounts);
        verify(target, times(1)).damage(6.25, caster);
    }

    @Test
    void damage_nullClassification_stillDamagesWithoutARequest() {
        // A player caster fails earlier, in AttributeListener, so use a mob as the caster
        Zombie mob = world.spawn(new Location(world, 3, 0, 3), Zombie.class);
        try (MockedStatic<DamageRegistry> ignored = registryFallsBack()) {
            assertDoesNotThrow(() -> skill.damage(target, 5, mob, null, true, false, CAUSE));
        }

        assertEquals(List.of(5.0), registryAmounts);
        verify(target).damage(5.0, mob);
        assertEquals(List.of(false), announcedInDamageCall);
        assertTrue(claimed.isEmpty());
        assertNothingOpen();
    }

    @Test
    void damage_requestUsesTheKnockbackTheEventSetsAndTheFinalDamage() {
        Listener tweak = new Listener() {
            @EventHandler
            public void onSkillDamage(SkillDamageEvent event) {
                event.setDamage(9);
                event.setKnockback(false);
            }
        };
        server.getPluginManager().registerEvents(tweak, plugin);
        try (MockedStatic<DamageRegistry> ignored = registryFallsBack()) {
            skill.damage(target, 5, caster, "physical", true, false, CAUSE);
        } finally {
            HandlerList.unregisterAll(tweak);
        }

        DamageRequest request = claimed.get(0);
        assertFalse(request.hasKnockback());
        assertEquals(java.util.Map.of("physical", 9.0), request.getAmounts());
        verify(target).damage(9.0, caster);
    }
}
