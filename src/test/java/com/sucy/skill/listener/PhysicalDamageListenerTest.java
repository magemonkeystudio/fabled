package com.sucy.skill.listener;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.sucy.skill.api.DefaultCombatProtection;
import com.sucy.skill.api.event.PhysicalDamageEvent;
import com.sucy.skill.testutil.MockedTest;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PhysicalDamageListenerTest extends MockedTest {

    private PlayerMock attacker;
    private PlayerMock damager;
    private MainListener mainListener;

    @BeforeEach
    public void setup() {
        attacker = genPlayer("Travja");
        damager = genPlayer("goflish");
        mainListener = new MainListener();
    }

    @Test
    void fakeEntityDamageByEntityEventDoesNotCallPhysicalDamageEvent() {
        DefaultCombatProtection.FakeEntityDamageByEntityEvent event =
                new DefaultCombatProtection.FakeEntityDamageByEntityEvent(attacker, damager, EntityDamageEvent.DamageCause.CUSTOM, 0D);

        mainListener.onPhysicalDamage(event);

        server.getPluginManager().assertEventNotFired(PhysicalDamageEvent.class);
    }

    @Test
    void fakeEntityDamageByEntityEventIsCalledOnAttack() {
        EntityDamageByEntityEvent event =
                                  new EntityDamageByEntityEvent(attacker, damager, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 5D);

        mainListener.onPhysicalDamage(event);

        server.getPluginManager().assertEventFired(DefaultCombatProtection.FakeEntityDamageByEntityEvent.class);
    }

}
