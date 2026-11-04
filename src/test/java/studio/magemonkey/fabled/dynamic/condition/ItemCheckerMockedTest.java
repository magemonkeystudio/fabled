package studio.magemonkey.fabled.dynamic.condition;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;
import studio.magemonkey.fabled.api.Settings;
import studio.magemonkey.fabled.dynamic.ItemChecker;
import studio.magemonkey.fabled.testutil.MockedTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ItemCheckerMockedTest extends MockedTest {

    @Test
    void check_tagMatches() {
        ItemStack item = new ItemStack(Material.IRON_AXE);
        Settings settings = new Settings();
        settings.set("check-tag", true);
        settings.set("check-mat", false);
        settings.set("tag", "axes");
        assertTrue(ItemChecker.check(item, 0, settings));
    }

    @Test
    void check_tagDoesntMatch() {
        ItemStack item = new ItemStack(Material.IRON_AXE);
        Settings settings = new Settings();
        settings.set("check-tag", true);
        settings.set("check-mat", false);
        settings.set("tag", "anvils");
        assertTrue(ItemChecker.check(item, 0, settings));
    }


}
