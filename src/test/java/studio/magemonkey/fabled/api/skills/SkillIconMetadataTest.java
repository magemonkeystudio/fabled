package studio.magemonkey.fabled.api.skills;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.classes.FabledClass;
import studio.magemonkey.fabled.api.player.PlayerClass;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.api.player.PlayerSkill;
import studio.magemonkey.fabled.api.util.Data;
import studio.magemonkey.fabled.testutil.MockedTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Covers that Skill's per-player indicator (getIndicator(PlayerSkill, boolean)) preserves
 * damage and custom model data from the source indicator ItemStack, instead of dropping
 * them as before. getToolIndicator() (the GUI-editor variant) was not changed to preserve
 * those two properties by this refactor, so that method is covered separately below for
 * what it does actually guarantee: producing a valid, non-null indicator.
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

    /**
     * Skill's designated constructors never populate incompatibleSkills (only load() does,
     * via a config default), and getIndicator(PlayerSkill, boolean) calls isCompatible()
     * which iterates it - so tests exercising that method need a load() pass. Round-trip
     * the source indicator through Data.serializeIcon/parseIcon so it still ends up as the
     * skill's indicator afterward.
     */
    private Skill getSkill() {
        Skill       skill  = new Skill("test", "test", new ItemStack(Material.APPLE), 5) {
        };
        DataSection config = new DataSection();
        Data.serializeIcon(sourceIndicator, config);
        skill.load(config);
        return skill;
    }

    @Test
    void getToolIndicator_producesNonNullIndicatorOfSourceType() {
        Skill skill = getSkill();

        ItemStack indicator = skill.getToolIndicator();

        assertEquals(Material.DIAMOND_SWORD, indicator.getType());
        assertNotNull(indicator.getItemMeta());
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
