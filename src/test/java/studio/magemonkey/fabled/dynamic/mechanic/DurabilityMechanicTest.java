package studio.magemonkey.fabled.dynamic.mechanic;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import studio.magemonkey.fabled.dynamic.DynamicSkill;
import studio.magemonkey.fabled.hook.PluginChecker;
import studio.magemonkey.fabled.testutil.MockedTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Covers DurabilityMechanic's Divinity durability integration is gated behind
 * PluginChecker.isDivinityActive() - see HealMechanicTest for why an ungated call is a
 * real crash risk on servers without Divinity installed.
 */
public class DurabilityMechanicTest extends MockedTest {
    private Player caster;

    @BeforeEach
    void setup() {
        caster = genPlayer("Travja");
        caster.getInventory().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));
    }

    private DurabilityMechanic getMechanic(double amount) {
        DurabilityMechanic mechanic = new DurabilityMechanic();
        DynamicSkill       skill    = new DynamicSkill("Durability");
        DataSection        config   = new DataSection();
        DataSection        data     = new DataSection();
        data.set("amount-base", amount);
        data.set("amount-scale", 0);
        config.set("data", data);
        mechanic.load(skill, config);
        return mechanic;
    }

    @Test
    void execute_divinityInactive_stillDamagesVanillaDurability() {
        assertFalse(PluginChecker.isDivinityActive());

        DurabilityMechanic mechanic = getMechanic(3);
        boolean             result  = mechanic.execute(caster, 1, List.of(caster), false);

        assertTrue(result);
        ItemStack  item = caster.getInventory().getItemInMainHand();
        Damageable meta = (Damageable) item.getItemMeta();
        assertEquals(3, meta.getDamage());
    }

    @Test
    void execute_divinityInactive_doesNotThrow() {
        DurabilityMechanic mechanic = getMechanic(3);

        // Regression guard: previously an ungated Divinity API call here would throw
        // NoClassDefFoundError when Divinity isn't installed.
        mechanic.execute(caster, 1, List.of(caster), false);
    }
}
