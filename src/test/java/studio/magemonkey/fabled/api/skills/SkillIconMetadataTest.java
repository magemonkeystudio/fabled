package studio.magemonkey.fabled.api.skills;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.classes.FabledClass;
import studio.magemonkey.fabled.api.player.PlayerClass;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.api.player.PlayerSkill;
import studio.magemonkey.fabled.testutil.MockedTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Covers that Skill's icon-producing methods preserve damage and custom model data from
 * the source indicator ItemStack, instead of dropping them as before.
 */
public class SkillIconMetadataTest extends MockedTest {
    private PlayerData  playerData;
    private PlayerClass playerClass;
    private ItemStack   sourceIndicator;

    @BeforeEach
    void setup() {
        Player player = genPlayer("Travja");
        FabledClass fabledClass = new FabledClass("test", new ItemStack(Material.APPLE), 50) {
        };
        playerData = Fabled.getData(player);
        playerClass = playerData.setClass(null, fabledClass, true);

        sourceIndicator = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = sourceIndicator.getItemMeta();
        meta.setDisplayName("Source Icon");
        meta.setCustomModelData(1234);
        ((Damageable) meta).setDamage(7);
        sourceIndicator.setItemMeta(meta);
    }

    private Skill getSkill() {
        return new Skill("test", "test", sourceIndicator, 5) {
        };
    }

    @Test
    void getToolIndicator_preservesDamageAndCustomModelData() {
        Skill skill = getSkill();

        ItemStack indicator = skill.getToolIndicator();
        ItemMeta  meta      = indicator.getItemMeta();

        assertEquals(1234, meta.getCustomModelData());
        assertEquals(7, ((Damageable) meta).getDamage());
    }

    @Test
    void getIndicator_playerSkillOverload_preservesDamageAndCustomModelData() {
        Skill       skill       = getSkill();
        PlayerSkill playerSkill = new PlayerSkill(playerData, skill, playerClass);
        playerSkill.setLevel(1);

        ItemStack indicator = skill.getIndicator(playerSkill, false);
        ItemMeta  meta      = indicator.getItemMeta();

        assertEquals(1234, meta.getCustomModelData());
        assertEquals(7, ((Damageable) meta).getDamage());
    }
}
