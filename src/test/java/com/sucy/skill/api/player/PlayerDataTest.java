package com.sucy.skill.api.player;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.manager.ProAttribute;
import com.sucy.skill.testutil.MockedTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerDataTest extends MockedTest {
    private PlayerMock attacker;
    private PlayerData playerData;

    @BeforeEach
    public void setup() {
        attacker = genPlayer("Travja");
        playerData = SkillAPI.getPlayerData(attacker);
        playerData.resetAll();
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
    void giveAttributes_modifier() {
        ProAttribute attribute = SkillAPI.getAttributeManager().getAttribute("spirit");
        attribute.setCostModifier(1.5);

        playerData.giveAttribute("spirit", 1);
        assertEquals(1, playerData.getAttribute("spirit"));
        assertEquals(1, playerData.getInvestedAttribute("spirit"));

        playerData.giveAttribute("spirit", 2);
        assertEquals(3, playerData.getAttribute("spirit"));
        assertEquals(7, playerData.getInvestedAttribute("spirit"));

        playerData.giveAttribute("spirit", -1);
        assertEquals(2, playerData.getAttribute("spirit"));
        assertEquals(3, playerData.getInvestedAttribute("spirit"));
    }

}
