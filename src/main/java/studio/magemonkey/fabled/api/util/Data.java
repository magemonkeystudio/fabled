/**
 * Fabled
 * studio.magemonkey.fabled.api.util.Data
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 MageMonkeyStudio
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

import studio.magemonkey.codex.mccore.config.parse.DataSection;
import studio.magemonkey.codex.mccore.util.TextFormatter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

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

    private static ItemStack parse(final String mat, final int dur, final int data, final List<String> lore) {
        try {
            Material material = Material.matchMaterial(mat);
            if (material == null) {
                material = Material.JACK_O_LANTERN;
            }

            final ItemStack item = new ItemStack(material);
            final ItemMeta  meta = item.getItemMeta();
            if (meta != null) {
                if (data != 0) {
                    meta.setCustomModelData(data);
                }
                if (lore != null && !lore.isEmpty()) {
                    final List<String> colored = TextFormatter.colorStringList(lore);
                    meta.setDisplayName(colored.remove(0));
                    meta.setLore(colored);
                }

                if (meta instanceof Damageable) {
                    ((Damageable) meta).setDamage(dur);
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
        config.set(MAT, item.getType().name());

        ItemMeta meta = item.getItemMeta();
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
