/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.ItemChecker
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 MageMonkeyStudio
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
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
package studio.magemonkey.fabled.dynamic;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import studio.magemonkey.codex.mccore.config.parse.NumberParser;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles checking items for dynamic effects
 */
public class ItemChecker {
    private static final String CHECK_MAT         = "check-mat";
    private static final String MATERIAL          = "material";
    private static final String CHECK_DATA        = "check-data";
    private static final String DATA              = "data";
    private static final String CHECK_CUSTOM_DATA = "check-custom-data";
    private static final String CUSTOM_DATA       = "custom-data";
    private static final String CHECK_LORE        = "check-lore";
    private static final String LORE              = "lore";
    private static final String REGEX             = "regex";
    private static final String CHECK_NAME        = "check-name";
    private static final String NAME              = "name";
    private static final String AMOUNT            = "amount";

    /**
     * Checks the player inventory for items matching the settings
     *
     * @param player    player to check
     * @param level     level of the effect
     * @param component effect component checking for
     * @param remove    whether to remove matching items
     * @return true if all conditions met, false otherwise
     */
    public static boolean check(Player player, int level, EffectComponent component, boolean remove) {
        final Settings settings = component.getSettings();

        int count = (int) component.parseValues(player, AMOUNT, level, 1);

        // Checks to do
        boolean mat         = settings.getBool(CHECK_MAT, true);
        boolean data        = settings.getBool(CHECK_DATA, true);
        boolean checkCustom = settings.getBool(CHECK_CUSTOM_DATA, false);
        boolean lore        = settings.getBool(CHECK_LORE, false);
        boolean name        = settings.getBool(CHECK_NAME, false);
        boolean regex       = settings.getBool(REGEX, false);

        // Values to compare to
        String material   = settings.getString(MATERIAL, "ARROW").toUpperCase(Locale.US).replace(" ", "_");
        int    dur        = settings.getInt(DATA, 0);
        int    customData = settings.getInt(CUSTOM_DATA, 0);
        String text       = settings.getString(LORE, "");
        String display    = settings.getString(NAME, "");

        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item == null
                    || (mat && !item.getType().name().equals(material))
                    || (data && item.getData().getData() != dur)
                    || (checkCustom && (!item.hasItemMeta() || item.getItemMeta().hasCustomModelData()
                    || item.getItemMeta().getCustomModelData() != customData))
                    || (lore && !checkLore(item, text, regex))
                    || (name && !checkName(item, display, regex)))
                continue;

            if (item.getAmount() <= count) {
                count -= item.getAmount();
                if (remove)
                    contents[i] = null;
            } else {
                if (remove)
                    item.setAmount(item.getAmount() - count);
                count = 0;
            }

            if (count == 0)
                break;
        }
        if (remove)
            player.getInventory().setContents(contents);

        return count == 0;
    }

    /**
     * Checks an individual item to see if it matches the settings
     *
     * @param item     item to check
     * @param level    level of the effect
     * @param settings settings to apply
     * @return true if passes all conditions, false otherwise
     */
    public static boolean check(ItemStack item, int level, Settings settings) {
        // Checks to do
        boolean mat         = settings.getBool(CHECK_MAT, true);
        boolean data        = settings.getBool(CHECK_DATA, true);
        boolean checkCustom = settings.getBool(CHECK_CUSTOM_DATA, false);
        boolean lore        = settings.getBool(CHECK_LORE, false);
        boolean name        = settings.getBool(CHECK_NAME, false);
        boolean regex       = settings.getBool(REGEX, false);

        // Values to compare to
        String  material    = settings.getString(MATERIAL, "ARROW").toUpperCase(Locale.US).replace(" ", "_");
        boolean anyMaterial = material.equals("ANY");
        String  potion      = settings.getString("potion", "Any").toUpperCase(Locale.US).replace(" ", "_");
        boolean anyPotion   = potion.equals("ANY");
        int     dur         = settings.getInt(DATA, 0);
        int     customData  = settings.getInt(CUSTOM_DATA, 0);
        String  text        = settings.getString(LORE, "");
        String  display     = settings.getString(NAME, "");

        List<String> potionTypes = new ArrayList<>();
        if (item != null && item.hasItemMeta() && item.getItemMeta() instanceof PotionMeta) {
            PotionType pType = ((PotionMeta) item.getItemMeta()).getBasePotionType();
            if (pType != null) {
                for (PotionEffect potionEffect : pType.getPotionEffects()) {
                    potionTypes.add(potionEffect.getType().getName());
                }
            }
        }

        return (item == null && material.equals("AIR"))
                || item != null
                && (!mat || anyMaterial || item.getType().name().equals(material))
                && (!material.contains("POTION") || anyPotion || potionTypes.contains(potion))
                && (!data || item.getDurability() == dur)
                && (!checkCustom || (item.hasItemMeta() && item.getItemMeta().hasCustomModelData()
                && item.getItemMeta().getCustomModelData() == customData))
                && (!lore || checkLore(item, text, regex))
                && (!name || checkName(item, display, regex));
    }

    /**
     * Checks the display name of the item
     *
     * @param item   item to check
     * @param target display name desired
     * @param regex  whether the target is a regex pattern
     * @return true if matches, false otherwise
     */
    public static boolean checkName(ItemStack item, String target, boolean regex) {
        ItemMeta meta;
        if (!item.hasItemMeta()
                || !(meta = item.getItemMeta()).hasDisplayName()) {
            return false;
        }

        String name = ChatColor.stripColor(meta.getDisplayName());
        if (regex && Pattern.compile(target).matcher(name).find()) {
            return true;
        } else if (!regex && name.contains(target)) {
            return true;
        }

        return false;
    }

    /**
     * Checks the lore of an item
     *
     * @param item   item to check
     * @param target lore text desired
     * @param regex  whether the target is a regex pattern
     * @return true if matches, false otherwise
     */
    public static boolean checkLore(ItemStack item, String target, boolean regex) {
        ItemMeta meta;
        if (!item.hasItemMeta()
                || !(meta = item.getItemMeta()).hasLore()) {
            return false;
        }

        List<String> lore = meta.getLore();
        for (String line : lore) {
            line = ChatColor.stripColor(line);
            if (regex && Pattern.compile(target).matcher(line).find()) {
                return true;
            } else if (!regex && line.contains(target)) {
                return true;
            }
        }

        return false;
    }

    public static boolean findLore(LivingEntity caster,
                                   ItemStack item,
                                   String regex,
                                   String key,
                                   double multiplier,
                                   boolean save) {
        Pattern pattern = Pattern.compile(regex.replace("{value}", "([+-]?[0-9]+([.,][0-9]+)?)"));

        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasLore())
            return false;

        List<String> lore = item.getItemMeta().getLore();
        for (String line : lore) {
            line = ChatColor.stripColor(line);
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                String value = matcher.group(1);
                try {
                    double base = NumberParser.parseDouble(value);
                    DynamicSkill.getCastData(caster).put(key, base * multiplier);
                    if (save) Fabled.getData((OfflinePlayer) caster)
                            .setPersistentData(key, DynamicSkill.getCastData(caster).getRaw(key));
                    break;
                } catch (Exception ex) {
                    // Not a valid value
                }
            }
        }

        return true;
    }
}
