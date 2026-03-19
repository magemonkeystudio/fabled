package studio.magemonkey.fabled.dynamic.mechanic.value;

import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import studio.magemonkey.fabled.api.Settings;
import studio.magemonkey.fabled.dynamic.DynamicSkill;
import studio.magemonkey.fabled.dynamic.EffectComponent;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class ValueLoreMechanicTest {
    private Settings        settings;
    private LivingEntity    caster;
    private EntityEquipment equipment;
    private ValueLoreMechanic mechanic;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        settings = mock(Settings.class);
        when(settings.has("key")).thenReturn(true);
        when(settings.getString("key")).thenReturn("damage");
        when(settings.getString(anyString(), anyString())).thenAnswer(ans -> ans.getArgument(1));
        when(settings.getBool(anyString(), anyBoolean())).thenAnswer(ans -> ans.getArgument(1));

        caster = mock(LivingEntity.class);
        when(caster.getEntityId()).thenReturn(1);

        equipment = mock(EntityEquipment.class);
        when(caster.getEquipment()).thenReturn(equipment);

        mechanic = spy(new ValueLoreMechanic());
        doReturn(2d).when(mechanic).parseValues(caster, "multiplier", 1, 1);

        Field settingsField = EffectComponent.class.getDeclaredField("settings");
        settingsField.setAccessible(true);
        settingsField.set(mechanic, settings);
    }

    @AfterEach
    void tearDown() {
        DynamicSkill.clearCastData(caster);
    }

    @Test
    void execute_usesFirstLoreMatchByDefault() {
        ItemStack item = mockLoreItem("Damage: 2", "Damage: 5");
        when(equipment.getItemInHand()).thenReturn(item);

        boolean result = mechanic.execute(caster, 1, List.of(caster), false);

        assertTrue(result);
        assertEquals(4, DynamicSkill.getCastData(caster).getDouble("damage"));
    }

    @Test
    void execute_sumAllAddsMatchesAcrossLoreLines() {
        when(settings.getBool("sum-all", false)).thenReturn(true);
        ItemStack item = mockLoreItem("Damage: 2", "Flavor text", "Damage: 5.5");
        when(equipment.getItemInHand()).thenReturn(item);

        boolean result = mechanic.execute(caster, 1, List.of(caster), false);

        assertTrue(result);
        assertEquals(15, DynamicSkill.getCastData(caster).getDouble("damage"));
    }

    private ItemStack mockLoreItem(String... loreLines) {
        ItemStack item = mock(ItemStack.class);
        ItemMeta  meta = mock(ItemMeta.class);

        when(item.hasItemMeta()).thenReturn(true);
        when(item.getItemMeta()).thenReturn(meta);
        when(meta.hasLore()).thenReturn(true);
        when(meta.getLore()).thenReturn(List.of(loreLines));

        return item;
    }
}
