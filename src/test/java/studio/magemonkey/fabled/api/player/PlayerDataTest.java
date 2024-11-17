package studio.magemonkey.fabled.api.player;

import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.enums.Operation;
import studio.magemonkey.fabled.api.event.PlayerAttributeChangeEvent;
import studio.magemonkey.fabled.manager.FabledAttribute;
import studio.magemonkey.fabled.testutil.MockedTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class PlayerDataTest extends MockedTest implements Listener {
    private PlayerData playerData;

    private String cancelEvent;
    private int    modifier = 0;

    @BeforeEach
    public void setup() {
        cancelEvent = null;
        modifier = 0;
        PlayerMock player = genPlayer("Travja");
        playerData = Fabled.getData(player);
        playerData.resetAll();
        playerData.giveAttribPoints(Integer.MAX_VALUE);

        server.getPluginManager()
                .registerEvent(PlayerAttributeChangeEvent.class, this, EventPriority.NORMAL, (listener, event) -> {
                    PlayerAttributeChangeEvent e = (PlayerAttributeChangeEvent) event;
                    if (e.getAttribute().equals(cancelEvent)) e.setCancelled(true);
                    if (modifier != 0) e.setChange(modifier);
                }, plugin, true);
    }

    @Test
    void giveAttribute_noModifier() {
        playerData.giveAttribute("spirit", 1);
        assertEquals(1, playerData.getAttribute("spirit"));
        assertEquals(1, playerData.getInvestedAttribute("spirit"));

        playerData.giveAttribute("spirit", 2);
        assertEquals(3, playerData.getAttribute("spirit"));
        assertEquals(3, playerData.getInvestedAttribute("spirit"));

        playerData.giveAttribute("spirit", -1);
        assertEquals(2, playerData.getAttribute("spirit"));
        assertEquals(2, playerData.getInvestedAttribute("spirit"));
    }

    @Test
    void giveAttribute_worksWithNoAttributePoints() {
        playerData.setAttribPoints(0);
        playerData.giveAttribute("spirit", 1);
        assertEquals(1, playerData.getAttribute("spirit"));
        assertEquals(1, playerData.getInvestedAttribute("spirit"));
    }

    @Test
    void giveAttributes_costModifierWorksNormally() {
        FabledAttribute attribute = Fabled.getAttributesManager().getAttribute("spirit");
        attribute.setCostModifier(1.5);

        assertEquals(1, playerData.getAttributeUpCost("spirit"));

        playerData.giveAttribute("spirit", 1);
        assertEquals(1, playerData.getAttribute("spirit"));
        assertEquals(1, playerData.getInvestedAttribute("spirit"));
        assertEquals(1, playerData.getInvestedAttributeStage("spirit"));
        assertEquals(2, playerData.getAttributeUpCost("spirit"));

        playerData.giveAttribute("spirit", 2);
        assertEquals(3, playerData.getAttribute("spirit"));
        assertEquals(7, playerData.getInvestedAttribute("spirit"));
        assertEquals(3, playerData.getInvestedAttributeStage("spirit"));
        assertEquals(5, playerData.getAttributeUpCost("spirit"));

        playerData.giveAttribute("spirit", -1);
        assertEquals(2, playerData.getAttribute("spirit"));
        assertEquals(3, playerData.getInvestedAttribute("spirit"));
        assertEquals(2, playerData.getInvestedAttributeStage("spirit"));
        assertEquals(4, playerData.getAttributeUpCost("spirit"));
    }

    @Test
    void giveAttributes_costModifierWorksWithAttributeModifiers() {
        FabledAttribute attribute = Fabled.getAttributesManager().getAttribute("spirit");
        attribute.setCostModifier(1.5);

        playerData.addAttributeModifier("spirit",
                new PlayerAttributeModifier("test-modifier", 1, Operation.ADD_NUMBER, false),
                true);

        assertEquals(1, playerData.getAttributeUpCost("spirit"));

        playerData.giveAttribute("spirit", 1);
        assertEquals(2, playerData.getAttribute("spirit"));
        assertEquals(1, playerData.getInvestedAttribute("spirit"));
        assertEquals(1, playerData.getInvestedAttributeStage("spirit"));
        assertEquals(2, playerData.getAttributeUpCost("spirit"));

        playerData.giveAttribute("spirit", 2);
        assertEquals(4, playerData.getAttribute("spirit"));
        assertEquals(7, playerData.getInvestedAttribute("spirit"));
        assertEquals(3, playerData.getInvestedAttributeStage("spirit"));
        assertEquals(5, playerData.getAttributeUpCost("spirit"));

        playerData.giveAttribute("spirit", -1);
        assertEquals(3, playerData.getAttribute("spirit"));
        assertEquals(3, playerData.getInvestedAttribute("spirit"));
        assertEquals(2, playerData.getInvestedAttributeStage("spirit"));
        assertEquals(4, playerData.getAttributeUpCost("spirit"));
    }

    @Test
    void getAttributeUpCost_worksGoingBackwards() {
        FabledAttribute attribute = Fabled.getAttributesManager().getAttribute("spirit");
        attribute.setCostModifier(1.5);

        // 1, 2, 4, 5, 7
        playerData.giveAttribute("spirit", 5);
        assertEquals(19, playerData.getInvestedAttribute("spirit"));
        assertEquals(5, playerData.getInvestedAttributeStage("spirit"));
        assertEquals(8, playerData.getAttributeUpCost("spirit"));
        assertEquals(-7, playerData.getAttributeUpCost("spirit", -1));
        assertEquals(-12, playerData.getAttributeUpCost("spirit", -2));
    }

    @Test
    void upAttribute_callsEvent() {
        playerData.upAttribute("spirit");
        assertEquals(1, playerData.getAttribute("spirit"));
        assertEventFired(PlayerAttributeChangeEvent.class,
                event -> event.getChange() == 1 && event.getAttribute().equals("spirit") && !event.isCancelled());
    }

    @Test
    void upAttribute_callsEventButDoesNotChangeAttributeWhenCancelled() {
        cancelEvent = "spirit";
        playerData.upAttribute("spirit");
        assertEquals(0, playerData.getAttribute("spirit"));
        assertEventFired(PlayerAttributeChangeEvent.class,
                event -> event.getChange() == 1 && event.getAttribute().equals("spirit") && event.isCancelled());
    }

    @Test
    void refundAttribute() {
        playerData.giveAttribute("spirit", 5);
        assertEquals(5, playerData.getAttribute("spirit"));
        assertEquals(5, playerData.getInvestedAttribute("spirit"));

        playerData.setAttribPoints(0);

        playerData.refundAttribute("spirit", 1);
        assertEventFired(PlayerAttributeChangeEvent.class,
                event -> event.getChange() == -1 && event.getAttribute().equals("spirit") && !event.isCancelled());
        assertEquals(4, playerData.getAttribute("spirit"));
        assertEquals(4, playerData.getInvestedAttribute("spirit"));
        assertEquals(1, playerData.getAttributePoints());

        playerData.refundAttribute("spirit", 1);
        assertEventFired(PlayerAttributeChangeEvent.class,
                event -> event.getChange() == -1 && event.getAttribute().equals("spirit") && !event.isCancelled());
        assertEquals(3, playerData.getAttribute("spirit"));
        assertEquals(3, playerData.getInvestedAttribute("spirit"));
        assertEquals(2, playerData.getAttributePoints());
    }

    @Test
    void refundAttribute_cancelled() {
        playerData.giveAttribute("spirit", 5);
        assertEquals(5, playerData.getAttribute("spirit"));
        assertEquals(5, playerData.getInvestedAttribute("spirit"));

        playerData.setAttribPoints(0);

        cancelEvent = "spirit";
        playerData.refundAttribute("spirit", 1);
        assertEventFired(PlayerAttributeChangeEvent.class,
                event -> event.getChange() == -1 && event.getAttribute().equals("spirit") && event.isCancelled());
        assertEquals(5, playerData.getAttribute("spirit"));
        assertEquals(5, playerData.getInvestedAttribute("spirit"));
        assertEquals(0, playerData.getAttributePoints());
    }

    @Test
    void refundAttribute_modifier() {
        FabledAttribute attribute = Fabled.getAttributesManager().getAttribute("spirit");
        attribute.setCostModifier(1.5);

        playerData.giveAttribute("spirit", 5);
        assertEquals(5, playerData.getAttribute("spirit"));
        assertEquals(19, playerData.getInvestedAttribute("spirit"));

        playerData.setAttribPoints(0);

        playerData.refundAttribute("spirit", 1);
        assertEventFired(PlayerAttributeChangeEvent.class,
                event -> event.getChange() == -1 && event.getAttribute().equals("spirit") && !event.isCancelled());
        assertEquals(4, playerData.getAttribute("spirit"));
        assertEquals(12, playerData.getInvestedAttribute("spirit"));
        assertEquals(7, playerData.getAttributePoints());

        modifier = -2;
        playerData.refundAttribute("spirit", 1);
        assertEventFired(PlayerAttributeChangeEvent.class,
                event -> event.getChange() == -2 && event.getAttribute().equals("spirit") && !event.isCancelled());
        assertEquals(2, playerData.getAttribute("spirit"));
        assertEquals(3, playerData.getInvestedAttribute("spirit"));
        assertEquals(16, playerData.getAttributePoints());
    }

    @Test
    void refundAttributeAll() {
        playerData.giveAttribute("spirit", 5);
        assertEquals(5, playerData.getAttribute("spirit"));
        assertEquals(5, playerData.getInvestedAttribute("spirit"));

        playerData.setAttribPoints(0);

        playerData.refundAttributeAll("spirit");
        assertEventFired(PlayerAttributeChangeEvent.class,
                event -> event.getChange() == -5 && event.getAttribute().equals("spirit") && !event.isCancelled());
        assertEquals(0, playerData.getAttribute("spirit"));
        assertEquals(0, playerData.getInvestedAttribute("spirit"));
        assertEquals(5, playerData.getAttributePoints());
    }

    @Test
    void refundAttributeAll_modifier() {
        playerData.giveAttribute("spirit", 5);
        assertEquals(5, playerData.getAttribute("spirit"));
        assertEquals(5, playerData.getInvestedAttribute("spirit"));

        playerData.setAttribPoints(0);

        modifier = -2;
        playerData.refundAttributeAll("spirit");
        assertEventFired(PlayerAttributeChangeEvent.class,
                event -> event.getChange() == -2 && event.getAttribute().equals("spirit") && !event.isCancelled());
        assertEquals(3, playerData.getAttribute("spirit"));
        assertEquals(3, playerData.getInvestedAttribute("spirit"));
        assertEquals(2, playerData.getAttributePoints());
    }

    @Test
    void refundAttributeAll_cancelled() {
        playerData.giveAttribute("spirit", 5);
        assertEquals(5, playerData.getAttribute("spirit"));
        assertEquals(5, playerData.getInvestedAttribute("spirit"));

        playerData.setAttribPoints(0);

        cancelEvent = "spirit";
        playerData.refundAttributeAll("spirit");
        assertEventFired(PlayerAttributeChangeEvent.class,
                event -> event.getChange() == -5 && event.getAttribute().equals("spirit") && event.isCancelled());
        assertEquals(5, playerData.getAttribute("spirit"));
        assertEquals(5, playerData.getInvestedAttribute("spirit"));
        assertEquals(0, playerData.getAttributePoints());
    }

    @Test
    void refundAttribute_noParam() {
        playerData.giveAttribute("spirit", 5);
        playerData.giveAttribute("strength", 5);
        assertEquals(5, playerData.getAttribute("spirit"));
        assertEquals(5, playerData.getInvestedAttribute("spirit"));

        playerData.setAttribPoints(0);

        playerData.refundAttributes();
        assertEventFired(PlayerAttributeChangeEvent.class,
                event -> event.getChange() == -5 && event.getAttribute().equals("spirit") && !event.isCancelled());
        assertEventFired(PlayerAttributeChangeEvent.class,
                event -> event.getChange() == -5 && event.getAttribute().equals("strength") && !event.isCancelled());
        assertEquals(0, playerData.getAttribute("spirit"));
        assertEquals(0, playerData.getInvestedAttribute("spirit"));
        assertEquals(0, playerData.getAttribute("strength"));
        assertEquals(0, playerData.getInvestedAttribute("strength"));
        assertEquals(10, playerData.getAttributePoints());
    }

    @Test
    void resetAttribs() {
        playerData.giveAttribute("spirit", 5);
        playerData.giveAttribute("strength", 5);
        playerData.giveAttribute("intelligence", 5);
        playerData.giveAttribute("vitality", 5);
        assertEquals(5, playerData.getInvestedAttribute("spirit"));
        assertEquals(5, playerData.getInvestedAttribute("strength"));
        assertEquals(5, playerData.getInvestedAttribute("intelligence"));
        assertEquals(5, playerData.getInvestedAttribute("vitality"));

        cancelEvent = "spirit";
        playerData.resetAttribs(false);
        assertEquals(5, playerData.getInvestedAttribute("spirit"));
        assertEquals(0, playerData.getInvestedAttribute("strength"));
        assertEquals(0, playerData.getInvestedAttribute("intelligence"));
        assertEquals(0, playerData.getInvestedAttribute("vitality"));
        assertEquals(0, playerData.getAttributePoints());
        assertEventFired(PlayerAttributeChangeEvent.class,
                event -> event.getChange() == -5 && event.getAttribute().equals("spirit") && event.isCancelled());
        assertEventFired(PlayerAttributeChangeEvent.class,
                event -> event.getChange() == -5 && event.getAttribute().equals("strength") && !event.isCancelled());
    }

    @Test
    void resetAttribs_refund() {
        playerData.giveAttribute("spirit", 5);
        playerData.giveAttribute("strength", 5);
        playerData.giveAttribute("intelligence", 5);
        playerData.giveAttribute("vitality", 5);
        assertEquals(5, playerData.getInvestedAttribute("spirit"));
        assertEquals(5, playerData.getInvestedAttribute("strength"));
        assertEquals(5, playerData.getInvestedAttribute("intelligence"));
        assertEquals(5, playerData.getInvestedAttribute("vitality"));

        cancelEvent = "spirit";
        playerData.setAttribPoints(5);
        playerData.resetAttribs(true);
        assertEquals(5, playerData.getInvestedAttribute("spirit"));
        assertEquals(0, playerData.getInvestedAttribute("strength"));
        assertEquals(0, playerData.getInvestedAttribute("intelligence"));
        assertEquals(0, playerData.getInvestedAttribute("vitality"));
        assertEquals(20, playerData.getAttributePoints());
        assertEventFired(PlayerAttributeChangeEvent.class,
                event -> event.getChange() == -5 && event.getAttribute().equals("spirit") && event.isCancelled());
        assertEventFired(PlayerAttributeChangeEvent.class,
                event -> event.getChange() == -5 && event.getAttribute().equals("strength") && !event.isCancelled());
    }

    @Test
    void resetAttribs_modifier() {
        playerData.giveAttribute("spirit", 5);
        playerData.giveAttribute("strength", 5);
        playerData.giveAttribute("intelligence", 5);
        playerData.giveAttribute("vitality", 5);
        assertEquals(5, playerData.getInvestedAttribute("spirit"));
        assertEquals(5, playerData.getInvestedAttribute("strength"));
        assertEquals(5, playerData.getInvestedAttribute("intelligence"));
        assertEquals(5, playerData.getInvestedAttribute("vitality"));

        cancelEvent = "spirit";
        modifier = -2;
        playerData.resetAttribs(false);
        assertEquals(5, playerData.getInvestedAttribute("spirit"));
        assertEquals(3, playerData.getInvestedAttribute("strength"));
        assertEquals(3, playerData.getInvestedAttribute("intelligence"));
        assertEquals(3, playerData.getInvestedAttribute("vitality"));
        assertEquals(0, playerData.getAttributePoints());
        assertEventFired(PlayerAttributeChangeEvent.class,
                event -> event.getChange() == -2 && event.getAttribute().equals("spirit") && event.isCancelled());
        assertEventFired(PlayerAttributeChangeEvent.class,
                event -> event.getChange() == -2 && event.getAttribute().equals("strength") && !event.isCancelled());
    }

    @Test
    void resetAttribs_modifier_refund() {
        playerData.giveAttribute("spirit", 5);
        playerData.giveAttribute("strength", 5);
        playerData.giveAttribute("intelligence", 5);
        playerData.giveAttribute("vitality", 5);
        assertEquals(5, playerData.getInvestedAttribute("spirit"));
        assertEquals(5, playerData.getInvestedAttribute("strength"));
        assertEquals(5, playerData.getInvestedAttribute("intelligence"));
        assertEquals(5, playerData.getInvestedAttribute("vitality"));

        cancelEvent = "spirit";
        modifier = -2;
        playerData.setAttribPoints(3);
        playerData.resetAttribs(true);
        assertEquals(5, playerData.getInvestedAttribute("spirit"));
        assertEquals(3, playerData.getInvestedAttribute("strength"));
        assertEquals(3, playerData.getInvestedAttribute("intelligence"));
        assertEquals(3, playerData.getInvestedAttribute("vitality"));
        assertEquals(9, playerData.getAttributePoints());
        assertEventFired(PlayerAttributeChangeEvent.class,
                event -> event.getChange() == -2 && event.getAttribute().equals("spirit") && event.isCancelled());
        assertEventFired(PlayerAttributeChangeEvent.class,
                event -> event.getChange() == -2 && event.getAttribute().equals("strength") && !event.isCancelled());
    }

    @Test
    void resetAll_resetsAttributePointsToZero() {
        playerData.setAttribPoints(20);
        for (int i = 0; i < 5; i++) {
            playerData.upAttribute("spirit");
            playerData.upAttribute("strength");
            playerData.upAttribute("intelligence");
        }

        assertEquals(5, playerData.getAttributePoints());

        playerData.resetAll();
        assertEquals(0, playerData.getAttributePoints());
    }

    @Test
    void upAttribute_doesNotExceedMax() {
        FabledAttribute attribute = Fabled.getAttributesManager().getAttribute("spirit");
        attribute.setMax(5);

        playerData.giveAttribute("spirit", 5);
        boolean upgraded = playerData.upAttribute("spirit");
        assertFalse(upgraded);
        assertEquals(5, playerData.getAttribute("spirit"));
    }
}
