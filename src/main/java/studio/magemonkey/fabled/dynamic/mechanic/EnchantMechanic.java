package studio.magemonkey.fabled.dynamic.mechanic;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import studio.magemonkey.codex.Codex;
import studio.magemonkey.codex.CodexEngine;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.enums.ExpSource;
import studio.magemonkey.fabled.api.player.PlayerClass;
import studio.magemonkey.fabled.listener.MechanicListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Modifies item enchantments for a specific duration
 */
public class EnchantMechanic extends MechanicComponent {

    public static final Map<ItemStack, ItemStack> ENCHANTED_ITEMS = new HashMap<>(); // ItemStack, [Backup-Item]
    // Shared Variables
    private static final String ENCHANT    = "enchant"; // Enchantments + levels to be applied
    private static final String DURATION   = "duration"; // Duration of the enchantment in seconds
    private static final String OFFHAND    = "off-hand"; // Rather to check on the mainhand (false) or off-hand (true)

    @Override
    public String getKey() {
        return "enchant";
    }

    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        if (!(caster instanceof Player)) return false;
        Player player = (Player) caster;

        int duration = settings.getInt(DURATION, 60);
        boolean offhand = settings.getBool(OFFHAND, false);

        Map<Enchantment, Integer> enchantments = new HashMap<>();
        List<String> enchantmentStrings = settings.getStringList(ENCHANT);

        for (String enchantmentString : enchantmentStrings) {
            String[] parts = enchantmentString.split(":");
            if (parts.length != 2) continue;
            try {
                Enchantment enchantment = Enchantment.getByName(parts[0].toUpperCase());
                int enchantLevel = Integer.parseInt(parts[1]);
                if (enchantment != null && enchantLevel > 0) {
                    enchantments.put(enchantment, enchantLevel);
                }
            } catch (NumberFormatException ignored) {
            }
        }

        ItemStack item = offhand ? player.getInventory().getItemInOffHand() : player.getInventory().getItemInMainHand();
        if(item.getType() == Material.AIR) return false;

        ItemStack previousItem = item.clone();
        item.addUnsafeEnchantments(enchantments);
        if(duration > 0) {
            ENCHANTED_ITEMS.put(item, previousItem);
            Bukkit.getScheduler().runTaskLater(Fabled.inst(), () -> restoreEnchantments(item), duration * 20L);
        }
        return true;
    }

    public static void restoreEnchantments(ItemStack item) {
        if(ENCHANTED_ITEMS.containsKey(item)) {
            ItemStack backup = ENCHANTED_ITEMS.get(item);
            if(backup != null) {
                item.setItemMeta(backup.getItemMeta());
            }
            ENCHANTED_ITEMS.remove(item);
        }
    }
}
