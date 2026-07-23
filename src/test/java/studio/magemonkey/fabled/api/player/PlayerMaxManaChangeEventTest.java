package studio.magemonkey.fabled.api.player;

import org.bukkit.Material;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.classes.FabledClass;
import studio.magemonkey.fabled.api.event.PlayerMaxManaChangeEvent;
import studio.magemonkey.fabled.testutil.MockedTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerMaxManaChangeEventTest extends MockedTest implements Listener {
    private PlayerData playerData;
    private double     bonus;
    private Double     overrideValue;

    @BeforeEach
    public void setup() {
        bonus = 0;
        overrideValue = null;

        PlayerMock player = genPlayer("Travja");
        FabledClass fabledClass = new FabledClass("test", new ItemStack(Material.APPLE), 50) {
        };
        playerData = Fabled.getData(player);
        playerData.setClass(null, fabledClass, true);

        server.getPluginManager().registerEvent(PlayerMaxManaChangeEvent.class, this, EventPriority.NORMAL,
                (listener, event) -> {
                    PlayerMaxManaChangeEvent e = (PlayerMaxManaChangeEvent) event;
                    if (overrideValue != null) {
                        e.setMaxMana(overrideValue);
                    } else if (bonus != 0) {
                        e.setMaxMana(e.getMaxMana() + bonus);
                    }
                }, plugin, true);
    }

    @Test
    void updatePlayerStat_firesMaxManaChangeEvent() {
        playerData.updatePlayerStat(playerData.getPlayer());

        assertEventFired(PlayerMaxManaChangeEvent.class);
    }

    @Test
    void updatePlayerStat_noListenerChange_leavesBaseMaxManaIntact() {
        playerData.updatePlayerStat(playerData.getPlayer());

        double baseMana = playerData.getMaxMana();

        // Re-running without a listener adjustment should be stable/idempotent.
        playerData.updatePlayerStat(playerData.getPlayer());
        assertEquals(baseMana, playerData.getMaxMana());
    }

    @Test
    void updatePlayerStat_listenerBonus_isAppliedToMaxMana() {
        playerData.updatePlayerStat(playerData.getPlayer());
        double baseMana = playerData.getMaxMana();

        bonus = 50;
        playerData.updatePlayerStat(playerData.getPlayer());

        assertEquals(baseMana + 50, playerData.getMaxMana());
    }

    @Test
    void updatePlayerStat_negativeListenerOverride_clampsToZero() {
        overrideValue = -100.0;
        playerData.updatePlayerStat(playerData.getPlayer());

        assertEquals(0, playerData.getMaxMana());
    }
}
