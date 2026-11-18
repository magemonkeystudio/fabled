package studio.magemonkey.fabled.listener;

import org.bukkit.Location;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import studio.magemonkey.codex.api.damage.DamageOrigin;
import studio.magemonkey.codex.api.damage.FinalDamageEvent;
import studio.magemonkey.codex.api.damage.WeaponStatsMode;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.DefaultCombatProtection;
import studio.magemonkey.fabled.api.event.PhysicalDamageEvent;
import studio.magemonkey.fabled.api.skills.Skill;
import studio.magemonkey.fabled.api.util.FlagManager;
import studio.magemonkey.fabled.dynamic.mechanic.ImmunityMechanic;
import studio.magemonkey.fabled.testutil.MockedTest;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Fabled's damage handling with and without a {@link FinalDamageEvent} provider (a damage plugin such as
 * Divinity). The provider here is a mock plugin, and the final damage event is fired by hand, as a provider
 * would fire it from its own Bukkit damage handler.
 */
public class FinalDamageListenerTest extends MockedTest {
    private Zombie           attacker;
    private Zombie           victim;
    private Plugin           provider;
    private PhysicalRecorder recorder;

    @BeforeEach
    public void setup() {
        attacker = world.spawn(new Location(world, 0, 0, 0), Zombie.class);
        victim = world.spawn(new Location(world, 1, 0, 0), Zombie.class);
        recorder = new PhysicalRecorder();
        server.getPluginManager().registerEvents(recorder, plugin);
    }

    @AfterEach
    public void teardown() {
        HandlerList.unregisterAll(recorder);
        if (provider != null) {
            FinalDamageEvent.unregisterProvider(provider);
            provider = null;
        }
        attacker.remove();
        victim.remove();
    }

    private void registerProvider() {
        provider = MockBukkit.createMockPlugin("TestDamageProvider");
        FinalDamageEvent.registerProvider(provider);
    }

    private EntityDamageByEntityEvent hit(double damage) {
        EntityDamageByEntityEvent event =
                new EntityDamageByEntityEvent(attacker, victim, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage);
        server.getPluginManager().callEvent(event);
        return event;
    }

    private FinalDamageEvent finalDamage(EntityDamageEvent hit, double damage) {
        Map<String, Double> damages = new LinkedHashMap<>();
        damages.put("physical", damage * 0.75);
        damages.put("fire", damage * 0.25);
        FinalDamageEvent event = new FinalDamageEvent(victim, attacker, null, hit.getCause(), hit, null,
                DamageOrigin.WEAPON_MELEE, WeaponStatsMode.ADD, damages);
        server.getPluginManager().callEvent(event);
        return event;
    }

    private static void setSkillDamage(boolean value) throws ReflectiveOperationException {
        Field field = Skill.class.getDeclaredField("skillDamage");
        field.setAccessible(true);
        field.setBoolean(null, value);
    }

    // ---- Without a provider: the old path ----

    @Test
    void noProvider_physicalDamageEventChangesBukkitDamage() {
        recorder.action = e -> e.setDamage(e.getDamage() * 2);

        EntityDamageByEntityEvent event = hit(5);

        assertEquals(1, recorder.calls);
        assertEquals(5, recorder.seen);
        assertEquals(10, event.getDamage());
        assertFalse(event.isCancelled());
    }

    @Test
    void noProvider_cancelCancelsBukkitEvent() {
        recorder.action = e -> e.setCancelled(true);

        EntityDamageByEntityEvent event = hit(5);

        assertEquals(1, recorder.calls);
        assertTrue(event.isCancelled());
    }

    @Test
    void noProvider_immunityScalesBukkitDamage() {
        FlagManager.addFlag(victim, "immune:ENTITY_ATTACK", 100);
        Fabled.setMeta(victim, ImmunityMechanic.META_KEY, 0.5);

        EntityDamageByEntityEvent event = hit(10);

        assertEquals(5, event.getDamage());
        assertEquals(5, recorder.seen);
    }

    // ---- With a provider: Fabled acts on the final damage ----

    @Test
    void provider_bukkitDamageUntouched_triggerFiresOnceOnFinalDamage() {
        registerProvider();
        recorder.action = e -> e.setDamage(e.getDamage() + 3);

        EntityDamageByEntityEvent event = hit(5);
        assertEquals(0, recorder.calls, "Physical damage must not fire from the Bukkit event");
        assertEquals(5, event.getDamage(), "The Bukkit event's damage must be left to the provider");

        FinalDamageEvent fin = finalDamage(event, 20);

        assertEquals(1, recorder.calls, "Physical damage fires once for the hit");
        assertEquals(20, recorder.seen, 1e-9);
        assertFalse(recorder.projectile);
        assertSame(attacker, recorder.damager);
        assertSame(victim, recorder.target);
        assertEquals(23, fin.getTotalDamage(), 1e-9);
        // Written back proportionally across the damage types
        assertEquals(23 * 0.75, fin.getDamage("physical"), 1e-9);
        assertEquals(23 * 0.25, fin.getDamage("fire"), 1e-9);
        assertEquals(5, event.getDamage(), "Fabled still leaves the Bukkit damage to the provider");
    }

    @Test
    void provider_scalingAppliesOnceToFinalDamage() {
        registerProvider();
        recorder.action = e -> e.setDamage(e.getDamage() * 2);

        EntityDamageByEntityEvent event = hit(5);
        FinalDamageEvent          fin   = finalDamage(event, 20);

        assertEquals(1, recorder.calls);
        assertEquals(40, fin.getTotalDamage(), 1e-9);
    }

    @Test
    void provider_unchangedDamageIsNotRewritten() {
        registerProvider();

        EntityDamageByEntityEvent event = hit(5);
        FinalDamageEvent          fin   = finalDamage(event, 20);

        assertEquals(1, recorder.calls);
        assertEquals(15, fin.getDamage("physical"), 1e-9);
        assertEquals(5, fin.getDamage("fire"), 1e-9);
    }

    @Test
    void provider_cancelCancelsFinalDamage() {
        registerProvider();
        recorder.action = e -> e.setCancelled(true);

        EntityDamageByEntityEvent event = hit(5);
        assertFalse(event.isCancelled());

        FinalDamageEvent fin = finalDamage(event, 20);

        assertEquals(1, recorder.calls);
        assertTrue(fin.isCancelled());
    }

    @Test
    void provider_negativeDamageIsClampedToZero() {
        registerProvider();
        recorder.action = e -> e.setDamage(-4);

        FinalDamageEvent fin = finalDamage(hit(5), 20);

        assertEquals(0, fin.getTotalDamage(), 1e-9);
    }

    @Test
    void provider_skillDamageExcluded() throws ReflectiveOperationException {
        registerProvider();

        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(attacker, victim,
                EntityDamageEvent.DamageCause.ENTITY_ATTACK, 5);
        setSkillDamage(true);
        try {
            server.getPluginManager().callEvent(event);
            FinalDamageEvent fin = finalDamage(event, 20);
            assertEquals(20, fin.getTotalDamage(), 1e-9);
        } finally {
            setSkillDamage(false);
        }

        assertEquals(0, recorder.calls);
    }

    @Test
    void provider_fakeDamageEventExcluded() {
        registerProvider();

        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(attacker, victim,
                EntityDamageEvent.DamageCause.ENTITY_ATTACK, 5);
        DefaultCombatProtection.fakeDamageEvents.add(event);
        try {
            FinalDamageEvent fin = finalDamage(event, 20);
            assertEquals(20, fin.getTotalDamage(), 1e-9);
        } finally {
            DefaultCombatProtection.fakeDamageEvents.remove(event);
        }

        assertEquals(0, recorder.calls);
    }

    @Test
    void provider_customCauseExcluded() {
        registerProvider();

        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(attacker, victim,
                EntityDamageEvent.DamageCause.CUSTOM, 5);
        server.getPluginManager().callEvent(event);
        finalDamage(event, 20);

        assertEquals(0, recorder.calls);
    }

    @Test
    void provider_zeroDamageExcluded() {
        registerProvider();

        finalDamage(hit(5), 0);

        assertEquals(0, recorder.calls);
    }

    @Test
    void provider_nonEntityDamageDoesNotFirePhysical() {
        registerProvider();

        EntityDamageEvent fall = new EntityDamageEvent(victim, EntityDamageEvent.DamageCause.FALL, 5);
        server.getPluginManager().callEvent(fall);
        finalDamage(fall, 20);

        assertEquals(0, recorder.calls);
    }

    @Test
    void provider_immunityAppliesOnceToFinalDamage() {
        registerProvider();
        FlagManager.addFlag(victim, "immune:ENTITY_ATTACK", 100);
        Fabled.setMeta(victim, ImmunityMechanic.META_KEY, 0.5);

        EntityDamageByEntityEvent event = hit(10);
        assertEquals(10, event.getDamage(), "Immunity must not scale the provider's input");

        FinalDamageEvent fin = finalDamage(event, 20);

        assertEquals(10, fin.getTotalDamage(), 1e-9);
        // Physical damage sees the damage after immunity, as it does without a provider
        assertEquals(10, recorder.seen, 1e-9);
        assertEquals(1, recorder.calls);
    }

    @Test
    void provider_queuedScalingIsUsedOnlyOnce() {
        registerProvider();
        FlagManager.addFlag(victim, "immune:ENTITY_ATTACK", 100);
        Fabled.setMeta(victim, ImmunityMechanic.META_KEY, 0.5);

        EntityDamageByEntityEvent event = hit(10);
        finalDamage(event, 20);
        // A second final damage event for the same hit must not apply the immunity again
        FinalDamageEvent again = finalDamage(event, 20);

        assertEquals(20, again.getTotalDamage(), 1e-9);
    }

    @Test
    void provider_disabledProviderUsesOldPath() {
        registerProvider();
        server.getPluginManager().disablePlugin(provider);
        recorder.action = e -> e.setDamage(e.getDamage() * 2);

        EntityDamageByEntityEvent event = hit(5);

        assertEquals(1, recorder.calls);
        assertEquals(10, event.getDamage());
    }

    @Test
    void provider_predicateDecidesPerHit() {
        provider = MockBukkit.createMockPlugin("TestDamageProvider");
        FinalDamageEvent.registerProvider(provider, e -> e.getEntity() != victim);
        recorder.action = e -> e.setDamage(e.getDamage() * 2);

        EntityDamageByEntityEvent event = hit(5);

        assertEquals(1, recorder.calls);
        assertEquals(10, event.getDamage());
    }

    public static class PhysicalRecorder implements Listener {
        int                           calls;
        double                        seen = Double.NaN;
        boolean                       projectile;
        Object                        damager;
        Object                        target;
        Consumer<PhysicalDamageEvent> action = e -> {};

        @EventHandler
        public void onPhysical(PhysicalDamageEvent event) {
            calls++;
            seen = event.getDamage();
            projectile = event.isProjectile();
            damager = event.getDamager();
            target = event.getTarget();
            action.accept(event);
        }
    }
}
