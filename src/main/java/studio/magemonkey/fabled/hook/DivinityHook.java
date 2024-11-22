package studio.magemonkey.fabled.hook;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import studio.magemonkey.codex.util.DataUT;

public class DivinityHook {
    private static final NamespacedKey KEY_MODULE  = NamespacedKey.fromString("prorpgitems:qrpg_item_module");
    private static final NamespacedKey KEY_MODULE2 = NamespacedKey.fromString("quantumrpg:qrpg_item_module");

    public static boolean isDivinity(ItemStack item) {
        String data = DataUT.getStringData(item, KEY_MODULE2);
        if (data == null) data = DataUT.getStringData(item, KEY_MODULE);

        return data != null;
    }

}
