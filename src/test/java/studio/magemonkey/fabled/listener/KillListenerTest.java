package studio.magemonkey.fabled.listener;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import studio.magemonkey.fabled.testutil.MockedTest;
import org.bukkit.event.entity.EntityDeathEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class KillListenerTest extends MockedTest {

    private PlayerMock                 player;
    private PlayerMock                 target;
    private KillListener              killListener;

    @BeforeEach
    public void setup() {
        player = genPlayer("Travja");
        target = genPlayer("goflish");
        killListener = spy(new KillListener());
    }

    @AfterEach
    public void teardown() {
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

        verify(killListener, times(1)).giveExp(any(), any(), anyInt());
    }
}