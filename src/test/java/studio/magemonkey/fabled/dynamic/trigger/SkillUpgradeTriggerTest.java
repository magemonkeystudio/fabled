package studio.magemonkey.fabled.dynamic.trigger;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.Settings;
import studio.magemonkey.fabled.api.classes.FabledClass;
import studio.magemonkey.fabled.api.event.PlayerSkillUpgradeEvent;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.api.player.PlayerSkill;
import studio.magemonkey.fabled.api.skills.Skill;
import studio.magemonkey.fabled.testutil.MockedTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SkillUpgradeTriggerTest extends MockedTest {
    private SkillUpgradeTrigger     trigger;
    private PlayerData              playerData;
    private PlayerSkill             playerSkill;
    private PlayerSkillUpgradeEvent event;

    @BeforeEach
    public void setup() {
        trigger = new SkillUpgradeTrigger();
        Player player = genPlayer("test");
        playerData = Fabled.getData(player);
        FabledClass fabledClass = new FabledClass("test", genItem(Material.APPLE, "Test"), 50) {
        };
        playerData.setClass(null, fabledClass, true);

        playerSkill = new PlayerSkill(playerData,
                new Skill("test", "test", Material.APPLE, 5) {
                },
                playerData.getMainClass());
        event = new PlayerSkillUpgradeEvent(playerData, playerSkill, 1);
    }

    @Test
    void shouldTrigger() {
        assertTrue(trigger.shouldTrigger(event, 1, new Settings()));
    }

    @Test
    void getCaster() {
        assertEquals(playerData.getPlayer(), trigger.getCaster(event));
    }

    @Test
    void getTarget() {
        assertEquals(playerData.getPlayer(), trigger.getTarget(event, new Settings()));
    }

    @Test
    void setValues() {
        CastData castData = new CastData(playerData.getPlayer());
        trigger.setValues(event, castData);
        assertEquals("1", castData.get("api-cost"));
        assertEquals("test", castData.get("api-skill"));
        assertEquals("0", castData.get("api-level"));
    }

    private ItemStack genItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta  meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }
}
