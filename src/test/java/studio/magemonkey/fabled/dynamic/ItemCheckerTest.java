package studio.magemonkey.fabled.dynamic;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import studio.magemonkey.fabled.api.Settings;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItemCheckerTest {
    private Settings settings;

    @BeforeEach
    void setUp() {
        settings = mock(Settings.class);

        when(settings.getBool(anyString())).thenReturn(false);
        when(settings.getBool(anyString(), anyBoolean())).thenAnswer(ans -> ans.getArgument(1));
        when(settings.getString(anyString(), anyString())).thenAnswer(ans -> ans.getArgument(1));
        when(settings.getStringList(anyString())).thenReturn(List.of());
        when(settings.getInt(anyString(), anyInt())).thenReturn(0);
    }

    @Test
    void check_anyMaterialMatches() {
        when(settings.getBool("check-mat", true)).thenReturn(true);
        when(settings.getString("material", "ARROW")).thenReturn("Any");

        ItemStack item = mock(ItemStack.class);
        when(item.getType()).thenReturn(Material.DIRT);
        assertTrue(ItemChecker.check(item, 0, settings));
    }

    @Test
    void check_materialMatches() {
        when(settings.getBool("check-mat", true)).thenReturn(true);
        when(settings.getString("material", "ARROW")).thenReturn("Dirt");

        ItemStack item = mock(ItemStack.class);
        when(item.getType()).thenReturn(Material.DIRT);
        assertTrue(ItemChecker.check(item, 0, settings));
    }

    @Test
    void check_materialDoesNotMatch() {
        when(settings.getBool("check-mat", true)).thenReturn(true);
        when(settings.getString("material", "ARROW")).thenReturn("Dirt");

        ItemStack item = mock(ItemStack.class);
        when(item.getType()).thenReturn(Material.STONE);
        assertFalse(ItemChecker.check(item, 0, settings));
    }

    @Test
    void check_dataMatchWorks() {
        when(settings.getBool("check-mat", true)).thenReturn(false);
        when(settings.getBool("check-data", true)).thenReturn(true);
        when(settings.getInt("data", 0)).thenReturn(1);

        ItemStack item = mock(ItemStack.class);
        when(item.getType()).thenReturn(Material.DIRT);
        when(item.getDurability()).thenReturn((short) 1);
        assertTrue(ItemChecker.check(item, 0, settings));
    }

    @Test
    void check_dataDoesNotMatch() {
        when(settings.getBool("check-mat", true)).thenReturn(false);
        when(settings.getBool("check-data")).thenReturn(true);
        when(settings.getInt("data", 0)).thenReturn(1);

        ItemStack item = mock(ItemStack.class);
        when(item.getType()).thenReturn(Material.DIRT);
        when(item.getDurability()).thenReturn((short) 2);
        assertFalse(ItemChecker.check(item, 0, settings));
    }

    @Test
    void check_anyPotionMatchWorks() {
        when(settings.getBool("check-mat", true)).thenReturn(false);
        when(settings.getString("material", "ARROW")).thenReturn("Potion");
        when(settings.getString("potion", "Any")).thenReturn("Any");

        ItemStack item = mock(ItemStack.class);
        when(item.getType()).thenReturn(Material.POTION);
        assertTrue(ItemChecker.check(item, 0, settings));
    }

// TODO - Implement potion tests
//    @Test
//    void check_potionMatchWorks() {
//        when(settings.getBool("check-mat", true)).thenReturn(false);
//        when(settings.getString("material", "ARROW")).thenReturn("Potion");
//        when(settings.getString("potion", "Any")).thenReturn("Speed");
//
//        ItemStack  item       = mock(ItemStack.class);
//        PotionMeta potionMeta = mock(PotionMeta.class);
//        when(item.getType()).thenReturn(Material.POTION);
//        when(item.hasItemMeta()).thenReturn(true);
//        when(item.getItemMeta()).thenReturn(potionMeta);
//        PotionType baseType = spy(PotionType.SPEED);
//        when(potionMeta.getBasePotionType()).thenReturn(baseType);
//        doReturn(List.of(baseType)).when(baseType).getPotionEffects();
//        assertTrue(ItemChecker.check(item, 0, settings));
//    }
}