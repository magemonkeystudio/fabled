package com.sucy.skill.listener;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.sucy.skill.testutil.MockedTest;
import org.bukkit.event.entity.EntityDeathEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;

public class DeathListenerTest extends MockedTest {

    private PlayerMock                 player;
    private PlayerMock                 target;
    private DeathListener              deathListener;
    private MockedStatic<KillListener> killListener;

    @BeforeEach
    public void setup() {
        player = genPlayer("Travja");
        target = genPlayer("goflish");
        killListener = mockStatic(KillListener.class);
        deathListener = spy(new DeathListener());
    }

    @AfterEach
    public void teardown() {
        killListener.close();
        killListener = null;
    }
    
    @Test
    public void onSpell() {
    }

    @Test
    public void onTrueDamage() {
    }

    @Test
    public void onDeath() {
        EntityDeathEvent event = new EntityDeathEvent(target, new ArrayList<>());
        event.callEvent();

       killListener
               .verify(() -> KillListener.giveExp(any(), any(), anyInt()));
    }
}