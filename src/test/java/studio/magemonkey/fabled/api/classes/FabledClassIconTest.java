package studio.magemonkey.fabled.api.classes;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.testutil.MockedTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Covers that FabledClass's icon field caches consistently after the Supplier -> direct
 * field refactor, and that getToolIcon() still preserves the source icon's lore.
 */
public class FabledClassIconTest extends MockedTest {
    @Override
    public void preInit() {
        loadClasses("Honor Guard");
    }

    private FabledClass fabledClass;

    @BeforeEach
    void setup() {
        fabledClass = Fabled.getClass("Honor Guard");
        assertNotNull(fabledClass);
    }

    @Test
    void getIcon_returnsStableCachedIcon_acrossMultipleCalls() {
        ItemStack first  = fabledClass.getIcon();
        ItemStack second = fabledClass.getIcon();

        assertSame(first, second);
    }

    @Test
    void getIcon_matchesConfiguredMaterial() {
        assertSame(Material.DIAMOND_CHESTPLATE, fabledClass.getIcon().getType());
    }

    @Test
    void getToolIcon_preservesSourceLoreAndUsesClassNameAsDisplayName() {
        ItemStack toolIcon = fabledClass.getToolIcon();
        ItemMeta  meta     = toolIcon.getItemMeta();

        assertNotNull(meta);
        assertTrue(meta.hasLore());
        // The tool icon overrides the display name to the class name for editor clarity,
        // demoting the source icon's own display name into the first lore line instead.
        assertEquals(fabledClass.getName(), meta.getDisplayName());
    }
}
