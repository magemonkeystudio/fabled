package studio.magemonkey.fabled.listener;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import studio.magemonkey.codex.api.damage.DamageOrigin;
import studio.magemonkey.codex.api.damage.FinalDamageEvent;
import studio.magemonkey.codex.api.damage.WeaponStatsMode;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.DefaultCombatProtection;
import studio.magemonkey.fabled.api.Settings;
import studio.magemonkey.fabled.api.enums.Operation;
import studio.magemonkey.fabled.api.event.PhysicalDamageEvent;
import studio.magemonkey.fabled.api.player.PlayerStatModifier;
import studio.magemonkey.fabled.api.skills.Skill;
import studio.magemonkey.fabled.api.util.Buff;
import studio.magemonkey.fabled.api.util.BuffManager;
import studio.magemonkey.fabled.api.util.FlagManager;
import studio.magemonkey.fabled.dynamic.DynamicSkill;
import studio.magemonkey.fabled.dynamic.mechanic.ImmunityMechanic;
import studio.magemonkey.fabled.dynamic.trigger.EnvironmentalTrigger;
import studio.magemonkey.fabled.manager.AttributeManager;
import studio.magemonkey.fabled.testutil.MockedTest;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Fabled applies its damage changes to Codex's {@link FinalDamageEvent}, which Codex fires for every hit.
 * <p>
 * Without a damage plugin, Codex's fallback listener fires it with one {@link FinalDamageEvent#DEFAULT_TYPE}
 * amount holding the Bukkit damage and writes the result back to the Bukkit event; these tests run against
 * that listener in the loaded Codex.
 * The expected numbers are the ones the old path (Fabled writing the Bukkit event's damage) produced.
 */
public class FinalDamageListenerTest extends MockedTest {
    private Zombie           attacker;
    private Zombie           victim;
    private PhysicalRecorder recorder;
    private int              finalEvents;
    private Listener         finalCounter;

    @BeforeEach
    public void setup() {
        attacker = world.spawn(new Location(world, 0, 0, 0), Zombie.class);
        victim = world.spawn(new Location(world, 1, 0, 0), Zombie.class);
        recorder = new PhysicalRecorder();
        server.getPluginManager().registerEvents(recorder, plugin);
        finalEvents = 0;
        finalCounter = new Listener() {
            @EventHandler
            public void onFinal(FinalDamageEvent event) {
                finalEvents++;
            }
        };
        server.getPluginManager().registerEvents(finalCounter, plugin);
    }

    @AfterEach
    public void teardown() {
        HandlerList.unregisterAll(recorder);
        HandlerList.unregisterAll(finalCounter);
        attacker.remove();
        victim.remove();
    }

    /**
     * Calls the Bukkit event. Codex's fallback listener (loaded before Fabled, as on a server) fires the final
     * damage event for it and writes the result back.
     */
    private <T extends EntityDamageEvent> T hit(T event) {
        server.getPluginManager().callEvent(event);
        return event;
    }

    private EntityDamageByEntityEvent melee(org.bukkit.entity.Entity from, LivingEntity to, double damage) {
        return hit(new EntityDamageByEntityEvent(from, to, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage));
    }

    private static void setSkillDamage(boolean value) throws ReflectiveOperationException {
        Field field = Skill.class.getDeclaredField("skillDamage");
        field.setAccessible(true);
        field.setBoolean(null, value);
    }

    // ---- Physical damage ----

    @Test
    void physicalMelee_triggerFiresOnceAndChangesDamage() {
        recorder.action = e -> e.setDamage(e.getDamage() * 2);

        EntityDamageByEntityEvent event = melee(attacker, victim, 5);

        assertEquals(1, recorder.calls, "Physical damage fires once per hit");
        assertEquals(1, finalEvents);
        assertEquals(5, recorder.seen, 1e-9);
        assertFalse(recorder.projectile);
        assertSame(attacker, recorder.damager);
        assertSame(victim, recorder.target);
        assertEquals(10, event.getDamage(), 1e-9, "Scaled exactly once");
        assertFalse(event.isCancelled());
    }

    @Test
    void physicalProjectile_isProjectileAndDamagerIsShooter() {
        Arrow arrow = world.spawn(new Location(world, 0, 1, 0), Arrow.class);
        arrow.setShooter(attacker);
        recorder.action = e -> e.setDamage(e.getDamage() + 3);

        EntityDamageByEntityEvent event = hit(new EntityDamageByEntityEvent(arrow, victim,
                EntityDamageEvent.DamageCause.PROJECTILE, 4));

        assertEquals(1, recorder.calls);
        assertTrue(recorder.projectile);
        assertSame(attacker, recorder.damager);
        assertEquals(7, event.getDamage(), 1e-9);
        arrow.remove();
    }

    @Test
    void physicalCancel_cancelsBukkitEvent() {
        recorder.action = e -> e.setCancelled(true);

        EntityDamageByEntityEvent event = melee(attacker, victim, 5);

        assertEquals(1, recorder.calls);
        assertTrue(event.isCancelled());
    }

    @Test
    void unchangedDamage_isNotRewritten() {
        EntityDamageByEntityEvent event = melee(attacker, victim, 5);

        assertEquals(1, recorder.calls);
        assertEquals(5, event.getDamage(), 1e-9);
    }

    @Test
    void negativeDamage_isClampedToZero() {
        recorder.action = e -> e.setDamage(-4);

        EntityDamageByEntityEvent event = melee(attacker, victim, 5);

        assertEquals(0, event.getDamage(), 1e-9);
    }

    @Test
    void physicalAttribute_scalesDamageOnce() {
        PlayerMock player = genPlayer("Attacker");
        Fabled.getData(player).addStatModifier(AttributeManager.PHYSICAL_DAMAGE,
                new PlayerStatModifier("test", 2, Operation.MULTIPLY_PERCENTAGE, false), false);

        EntityDamageByEntityEvent event = melee(player, victim, 5);

        assertEquals(1, recorder.calls);
        assertEquals(10, event.getDamage(), 1e-9);
    }

    @Test
    void damageBuff_scalesDamageOnce() {
        BuffManager.addDamageBuff(attacker, new Buff("test", 3, true), 100);

        EntityDamageByEntityEvent event = melee(attacker, victim, 5);

        assertEquals(1, recorder.calls);
        assertEquals(15, event.getDamage(), 1e-9);
        assertEquals(15, recorder.seen, 1e-9, "Buffs run before a default-priority listener, as before");
    }

    // ---- Filters ----

    @Test
    void skillDamage_doesNotFirePhysical() throws ReflectiveOperationException {
        recorder.action = e -> e.setDamage(100);
        EntityDamageByEntityEvent event;
        setSkillDamage(true);
        try {
            event = melee(attacker, victim, 5);
        } finally {
            setSkillDamage(false);
        }

        assertEquals(0, recorder.calls);
        assertEquals(1, finalEvents);
        assertEquals(5, event.getDamage(), 1e-9);
    }

    @Test
    void fakeDamageEvent_doesNotFirePhysical() {
        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(attacker, victim,
                EntityDamageEvent.DamageCause.ENTITY_ATTACK, 5);
        DefaultCombatProtection.fakeDamageEvents.add(event);
        try {
            // Codex's fallback runs before Fabled's fakeDamageClear, so it fires the final event for it
            hit(event);
        } finally {
            DefaultCombatProtection.fakeDamageEvents.remove(event);
        }

        assertEquals(1, finalEvents);
        assertEquals(0, recorder.calls);
    }

    @Test
    void customCause_doesNotFirePhysical() {
        hit(new EntityDamageByEntityEvent(attacker, victim, EntityDamageEvent.DamageCause.CUSTOM, 5));

        assertEquals(0, recorder.calls);
    }

    @Test
    void zeroDamage_doesNotFirePhysical() {
        melee(attacker, victim, 0);

        assertEquals(0, recorder.calls);
    }

    @Test
    void nonEntityDamage_doesNotFirePhysical() {
        hit(new EntityDamageEvent(victim, EntityDamageEvent.DamageCause.FALL, 5));

        assertEquals(0, recorder.calls);
        assertEquals(1, finalEvents);
    }

    // ---- Immunity and defense-<cause> ----

    @Test
    void partialImmunity_scalesDamageBeforePhysical() {
        FlagManager.addFlag(victim, "immune:ENTITY_ATTACK", 100);
        Fabled.setMeta(victim, ImmunityMechanic.META_KEY, 0.5);

        EntityDamageByEntityEvent event = melee(attacker, victim, 10);

        assertEquals(5, event.getDamage(), 1e-9);
        assertEquals(5, recorder.seen, 1e-9);
    }

    @Test
    void partialImmunity_appliesToSkillDamage() throws ReflectiveOperationException {
        FlagManager.addFlag(victim, "immune:ENTITY_ATTACK", 100);
        Fabled.setMeta(victim, ImmunityMechanic.META_KEY, 0.5);
        EntityDamageByEntityEvent event;
        setSkillDamage(true);
        try {
            event = melee(attacker, victim, 10);
        } finally {
            setSkillDamage(false);
        }

        assertEquals(5, event.getDamage(), 1e-9);
    }

    @Test
    void fullImmunity_cancelsBeforeFinalDamage() {
        FlagManager.addFlag(victim, "immune:ENTITY_ATTACK", 100);
        Fabled.setMeta(victim, ImmunityMechanic.META_KEY, 0D);

        EntityDamageByEntityEvent event = melee(attacker, victim, 10);

        assertTrue(event.isCancelled());
        assertEquals(0, finalEvents);
        assertEquals(0, recorder.calls);
    }

    @Test
    void defenseCause_scalesEnvironmentalDamage() {
        PlayerMock player = genPlayer("Defender");
        Fabled.getData(player).addStatModifier("defense-fall",
                new PlayerStatModifier("test", 0.5, Operation.MULTIPLY_PERCENTAGE, false),
                false);

        EntityDamageEvent event = hit(new EntityDamageEvent(player, EntityDamageEvent.DamageCause.FALL, 10));

        assertEquals(5, event.getDamage(), 1e-9);
    }

    @Test
    void defenseCause_appliesBeforePhysical() {
        PlayerMock player = genPlayer("Defender2");
        Fabled.getData(player).addStatModifier("defense-entity_attack",
                new PlayerStatModifier("test", 0.5, Operation.MULTIPLY_PERCENTAGE, false),
                false);

        EntityDamageByEntityEvent event = melee(attacker, player, 10);

        assertEquals(5, recorder.seen, 1e-9);
        assertEquals(5, event.getDamage(), 1e-9);
    }

    // ---- Environmental trigger ----

    @Test
    void environmentalTrigger_matchesCauseAndBuffsFinalDamage() {
        EnvironmentalTrigger trigger  = new EnvironmentalTrigger();
        Settings             settings = new Settings();
        settings.set("type", List.of("Fall"));

        EntityDamageEvent fall = new EntityDamageEvent(victim, EntityDamageEvent.DamageCause.FALL, 10);
        Map<String, Double> damages = new LinkedHashMap<>();
        damages.put(FinalDamageEvent.DEFAULT_TYPE, 10D);
        FinalDamageEvent fin = new FinalDamageEvent(victim, null, null, fall.getCause(), fall, null,
                DamageOrigin.ENVIRONMENT, WeaponStatsMode.ADD, damages);

        assertTrue(trigger.shouldTrigger(fin, 1, settings));
        assertSame(victim, trigger.getCaster(fin));

        DynamicSkill skill = new DynamicSkill("Environmental Test");
        skill.setImmediateBuff(2, false);
        skill.setImmediateBuff(1, true);
        trigger.postProcess(fin, skill);

        assertEquals(21, fin.getTotalDamage(), 1e-9);

        EntityDamageEvent lava = new EntityDamageEvent(victim, EntityDamageEvent.DamageCause.LAVA, 10);
        FinalDamageEvent lavaFin = new FinalDamageEvent(victim, null, null, lava.getCause(), lava, null,
                DamageOrigin.ENVIRONMENT, WeaponStatsMode.ADD, new LinkedHashMap<>(damages));
        assertFalse(trigger.shouldTrigger(lavaFin, 1, settings));
    }

    @Test
    void environmentalTrigger_ignoresFakeDamageEvents() {
        EnvironmentalTrigger trigger  = new EnvironmentalTrigger();
        Settings             settings = new Settings();
        settings.set("type", List.of("ANY"));

        EntityDamageByEntityEvent fake = new EntityDamageByEntityEvent(attacker, victim,
                EntityDamageEvent.DamageCause.ENTITY_ATTACK, 5);
        Map<String, Double> damages = new LinkedHashMap<>();
        damages.put(FinalDamageEvent.DEFAULT_TYPE, 5D);
        FinalDamageEvent fin = new FinalDamageEvent(victim, attacker, null, fake.getCause(), fake, null,
                DamageOrigin.EXTERNAL, WeaponStatsMode.ADD, damages);

        DefaultCombatProtection.fakeDamageEvents.add(fake);
        try {
            assertFalse(trigger.shouldTrigger(fin, 1, settings));
        } finally {
            DefaultCombatProtection.fakeDamageEvents.remove(fake);
        }
        assertTrue(trigger.shouldTrigger(fin, 1, settings));
    }

    // ---- A damage plugin's final damage ----

    @Test
    void damagePlugin_writeBackIsProportionalAcrossTypes() {
        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(attacker, victim,
                EntityDamageEvent.DamageCause.ENTITY_ATTACK, 5);
        recorder.action = e -> e.setDamage(e.getDamage() * 2);
        Map<String, Double> damages = new LinkedHashMap<>();
        damages.put("physical", 15D);
        damages.put("fire", 5D);
        FinalDamageEvent fin = new FinalDamageEvent(victim, attacker, null, event.getCause(), event, null,
                DamageOrigin.WEAPON_MELEE, WeaponStatsMode.ADD, damages);
        server.getPluginManager().callEvent(fin);

        assertEquals(1, recorder.calls);
        assertEquals(20, recorder.seen, 1e-9);
        assertEquals(30, fin.getDamage("physical"), 1e-9);
        assertEquals(10, fin.getDamage("fire"), 1e-9);
    }

    @Test
    void rawRequest_stillFiresPhysicalForNonSkillHits() {
        // RAW only changes how a damage plugin treats weapon stats. Fabled's own RAW skill hits are excluded
        // as skill damage; any other hit is physical, as it was before
        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(attacker, victim,
                EntityDamageEvent.DamageCause.ENTITY_ATTACK, 5);
        Map<String, Double> damages = new LinkedHashMap<>();
        damages.put(FinalDamageEvent.DEFAULT_TYPE, 5D);
        server.getPluginManager().callEvent(new FinalDamageEvent(victim, attacker, null, event.getCause(), event,
                null, DamageOrigin.EXTERNAL, WeaponStatsMode.RAW, damages));

        assertEquals(1, recorder.calls);
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
