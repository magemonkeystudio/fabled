package studio.magemonkey.fabled.manager;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.testutil.MockedTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Covers that FabledAttribute#getIcon(PlayerData) preserves damage and custom model data
 * from the source icon, instead of dropping them as before. getToolIcon() (the GUI-editor
 * variant) was not changed to preserve those two properties by this refactor, so it's
 * covered separately below for what it does actually guarantee: a valid, non-null icon.
 */
public class FabledAttributeIconMetadataTest extends MockedTest {
    private Player     player;
    private PlayerData playerData;

    @BeforeEach
    void setup() {
        player = genPlayer("Travja");
        playerData = Fabled.getData(player);
        playerData.giveAttribPoints(Integer.MAX_VALUE);
    }

    private FabledAttribute getAttribute(String key) {
        DataSection data = new DataSection();
        data.set("icon", "DIAMOND_SWORD");
        data.set("icon-data", 1234);
        data.set("icon-durability", 7);
        data.set("icon-lore", List.of("Source Display"));
        return new FabledAttribute(data, key);
    }

    @Test
    void getToolIcon_producesNonNullIconOfSourceType() {
        FabledAttribute attribute = getAttribute("spirit");

        ItemStack icon = attribute.getToolIcon();

        assertEquals(Material.DIAMOND_SWORD, icon.getType());
        assertNotNull(icon.getItemMeta());
    }

    @Test
    void getIcon_playerData_preservesDamageAndCustomModelData() {
        FabledAttribute attribute = getAttribute("spirit");

        ItemStack icon = attribute.getIcon(playerData);
        ItemMeta  meta = icon.getItemMeta();

        assertEquals(1234, meta.getCustomModelData());
        assertEquals(7, ((Damageable) meta).getDamage());
    }
}
