/**
 * Fabled
 * studio.magemonkey.fabled.api.util.Data
 * <p>
 * The MIT License (MIT)
 * <p>
 * © 2026 VoidEdge
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package studio.magemonkey.fabled.api.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import studio.magemonkey.codex.CodexEngine;
import studio.magemonkey.codex.api.items.ItemType;
import studio.magemonkey.codex.api.items.exception.CodexItemException;
import studio.magemonkey.codex.items.CodexItemManager;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import studio.magemonkey.codex.util.StringUT;
import studio.magemonkey.fabled.Fabled;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for managing loading/saving certain data
 */
public class Data {
    private static final String MAT        = "icon";
    private static final String DATA       = "icon-data";
    private static final String DURABILITY = "icon-durability";
    private static final String LORE       = "icon-lore";
    private static final String NAME       = "name";

    /**
     * Marks a placeholder icon as standing in for a custom item (e.g. Nexo) that couldn't be
     * resolved yet, carrying the original raw icon key so a save doesn't overwrite it with the
     * placeholder before the item provider has actually loaded the item (e.g. Nexo indexes its
     * items asynchronously after enabling, so it may not be ready the moment Fabled starts up).
     */
    private static NamespacedKey pendingIconKey() {
        return new NamespacedKey(Fabled.inst(), "pending-icon-id");
    }

    private static ItemStack parse(final String mat, final int dur, final int data, final List<String> lore) {
        try {
            CodexItemManager itemManager = CodexEngine.get().getItemManager();
            ItemType         itemType    = null;
            try {
                itemType = itemManager.getItemType(mat);
            } catch (CodexItemException ignored) {
                // Provider may not be hooked in yet (e.g. Nexo hasn't finished enabling), or
                // there's simply no item registered under this key. Either way, fall through
                // and try to treat it as a vanilla material below.
            }

            ItemStack item       = itemType != null ? itemType.create() : null;
            boolean   unresolved = false;

            if (item == null) {
                Material material = Material.matchMaterial(mat);
                if (material != null) {
                    item = new ItemStack(material);
                } else {
                    // Not a vanilla material either, so this is almost certainly a reference to
                    // a custom item (e.g. NEXO_something) whose provider just hasn't loaded the
                    // item yet. Don't collapse it down to a plain Jack O'Lantern permanently --
                    // remember the original key so a save doesn't clobber it.
                    item = new ItemStack(Material.JACK_O_LANTERN);
                    unresolved = true;
                }
            }

            final ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                if (data != 0) {
                    meta.setCustomModelData(data);
                }
                if (lore != null && !lore.isEmpty()) {
                    final List<String> colored = StringUT.color(lore);
                    meta.setDisplayName(colored.remove(0));
                    meta.setLore(colored);
                }

                if (meta instanceof Damageable) {
                    ((Damageable) meta).setDamage(dur);
                }

                if (unresolved) {
                    meta.getPersistentDataContainer().set(pendingIconKey(), PersistentDataType.STRING, mat);
                }

                item.setItemMeta(meta);
            }
            return DamageLoreRemover.removeAttackDmg(item);
        } catch (final Exception ex) {
            return new ItemStack(Material.JACK_O_LANTERN);
        }
    }

    /**
     * Serializes an item icon into a configuration
     *
     * @param item   item to serialize
     * @param config config to serialize into
     */
    public static void serializeIcon(ItemStack item, DataSection config) {
        ItemMeta meta    = item.getItemMeta();
        String   pending = meta == null
                ? null
                : meta.getPersistentDataContainer().get(pendingIconKey(), PersistentDataType.STRING);

        if (pending != null) {
            // The referenced custom item still hasn't loaded (e.g. Nexo hasn't finished
            // indexing yet on this restart). Preserve the original reference instead of
            // overwriting it with the placeholder's material.
            config.set(MAT, pending);
        } else {
            CodexItemManager itemManager = CodexEngine.get().getItemManager();
            ItemType         itemType    = itemManager.getMainItemType(item);
            if (itemType != null) {
                config.set(MAT, itemType.getNamespacedID());
            } else {
                config.set(MAT, item.getType().name());
            }
        }

        if (meta != null) {
            config.set(DATA, meta.hasCustomModelData() ? meta.getCustomModelData() : 0);

            if (meta instanceof Damageable) {
                config.set(DURABILITY, ((Damageable) meta).getDamage());
            } else {
                config.set(DURABILITY, 0);
            }

            if (meta.hasDisplayName()) {
                List<String> lore = item.getItemMeta().getLore();
                if (lore == null) lore = new ArrayList<>();
                lore.add(0, item.getItemMeta().getDisplayName());
                int count = lore.size();
                for (int i = 0; i < count; i++) {
                    lore.set(i, lore.get(i).replace(ChatColor.COLOR_CHAR, '&').replaceAll("attr:(&" + ".)+", "attr:"));
                }
                config.set(LORE, lore);
            }
        }
    }

    /**
     * Parses an item icon from a configuration
     *
     * @param config config to load from
     * @return parsed item icon or a plain Jack O' Lantern if invalid
     */
    public static ItemStack parseIcon(DataSection config) {
        if (config == null) {
            return new ItemStack(Material.JACK_O_LANTERN);
        }

        final int data = config.getInt(DATA, 0);
        return parse(
                config.getString(MAT, "JACK_O_LANTERN"),
                config.getInt(DURABILITY, 0),
                data,
                config.getList(LORE, null));
    }
}
