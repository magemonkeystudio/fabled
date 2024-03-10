package com.sucy.skill.listener;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.sucy.skill.api.CombatProtection;
import com.sucy.skill.api.DefaultCombatProtection;
import com.sucy.skill.api.event.PhysicalDamageEvent;
import com.sucy.skill.testutil.MockedTest;
import org.bukkit.Location;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class PhysicalDamageListenerTest extends MockedTest {

    private PlayerMock   attacker;
    private PlayerMock   damager;
    private MainListener mainListener;

    @BeforeEach
    public void setup() {
        attacker = genPlayer("Travja");
        damager = genPlayer("goflish");
        mainListener = new MainListener();
    }

//    @Test
//    void canAttackExternallyDoesNotCallPhysicalDamageEvent() {
//        CombatProtection.canAttackExternally(attacker, damager, EntityDamageEvent.DamageCause.ENTITY_ATTACK);
//
//        mainListener.onPhysicalDamage_allyCheck(event);
//
//        server.getPluginManager().assertEventNotFired(PhysicalDamageEvent.class);
//    }
//
//    @Test
//    void fakeEntityDamageByEntityEventIsCalledOnAttack() {
//        EntityDamageByEntityEvent event =
//                new EntityDamageByEntityEvent(attacker,
//                        damager,
//                        EntityDamageEvent.DamageCause.ENTITY_ATTACK,
//                        DamageSource.builder(DamageType.MOB_ATTACK).build(),
//                        5D);
//
//        mainListener.onPhysicalDamage_allyCheck(event);
//
//        server.getPluginManager().assertEventFired(DefaultCombatProtection.FakeEntityDamageByEntityEvent.class);
//    }

    @Test
    void passiveMobsCanAttackEachOther() {
        Sheep sheep  = world.spawn(new Location(world, 0, 0, 0), Sheep.class);
        Sheep sheep2 = world.spawn(new Location(world, 0, 0, 0), Sheep.class);

        EntityDamageByEntityEvent event =
                new EntityDamageByEntityEvent(sheep, sheep2, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 5D);
        server.getPluginManager().callEvent(event);

        assertFalse(event.isCancelled());
        assertEquals(5D, event.getDamage());
    }

    @Test
    void hostileMobsCanAttackEachOther() {
        Zombie zombie  = world.spawn(new Location(world, 0, 0, 0), Zombie.class);
        Zombie zombie2 = world.spawn(new Location(world, 0, 0, 0), Zombie.class);

        EntityDamageByEntityEvent event =
                new EntityDamageByEntityEvent(zombie, zombie2, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 5D);
        server.getPluginManager().callEvent(event);

        assertFalse(event.isCancelled());
        assertEquals(5D, event.getDamage());
    }

    @Test
    void hostileAndPassiveCanAttackEachOther() {
        Zombie zombie = world.spawn(new Location(world, 0, 0, 0), Zombie.class);
        Sheep  sheep  = world.spawn(new Location(world, 0, 0, 0), Sheep.class);

        EntityDamageByEntityEvent event =
                new EntityDamageByEntityEvent(zombie, sheep, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 5D);
        server.getPluginManager().callEvent(event);

        assertFalse(event.isCancelled());
        assertEquals(5D, event.getDamage());

        event = new EntityDamageByEntityEvent(sheep, zombie, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 10D);
        server.getPluginManager().callEvent(event);

        assertFalse(event.isCancelled());
        assertEquals(10D, event.getDamage());
    }

}
