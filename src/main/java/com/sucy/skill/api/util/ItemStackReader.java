package com.sucy.skill.api.util;

import com.sucy.skill.api.Settings;
import mc.promcteam.engine.mccore.util.TextFormatter;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Objects;

public final class ItemStackReader {
    private static final String MATERIAL   = "material";
    private static final String AMOUNT     = "amount";
    private static final String DURABILITY = "data"; // Wrong name for retrocompatibility
    private static final String UNBREAKABLE = "unbreakable";
    private static final String CMD        = "byte"; // Wrong name for retrocompatibility
    private static final String HIDE_FLAGS = "hide-flags";
    private static final String CUSTOM     = "custom";
    private static final String NAME       = "name";
    private static final String LORE       = "lore";

    private static final String POTION_COLOR    = "potion_color";
    private static final String POTION_TYPE     = "potion_type";
    private static final String POTION_LEVEL    = "potion_level";
    private static final String POTION_DURATION = "potion_duration";
    private static final String ARMOR_COLOR = "armor_color";

    private ItemStackReader() { }

    public static ItemStack read(Settings settings) {
        ItemStack item = new ItemStack(Material.valueOf(settings.getString(MATERIAL, "arrow").toUpperCase().replace(" ", "_")), settings.getInt(AMOUNT, 1));
        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());

        if (meta instanceof Damageable) {
            Damageable damageable = (Damageable) meta;
            damageable.setDamage(settings.getInt(DURABILITY, 0));
            damageable.setUnbreakable(settings.getBool(UNBREAKABLE, false));
        }
        meta.setCustomModelData(settings.getInt(CMD, 0));

        for (String hideFlag : settings.getStringList(HIDE_FLAGS)) {
            try {
                meta.addItemFlags(ItemFlag.valueOf("HIDE_"+hideFlag.toUpperCase().replace(' ', '_')));
            } catch (IllegalArgumentException ignored) { }
        }

        if (settings.getString(CUSTOM, "false").equalsIgnoreCase("true")) {
            String name = TextFormatter.colorString(settings.getString(NAME, ""));
            if (name.length() > 0) {
                meta.setDisplayName(name);
            }
            List<String> lore = TextFormatter.colorStringList(settings.getStringList(LORE));
            meta.setLore(lore);
        }

        if (meta instanceof PotionMeta) {
            PotionMeta pm = (PotionMeta) meta;
            pm.clearCustomEffects();
            try {
                pm.addCustomEffect(new PotionEffect(
                        PotionEffectType.getByName(settings.getString(POTION_TYPE).replace(" ", "_")),
                        settings.getInt(POTION_DURATION)*20,
                        settings.getInt(POTION_LEVEL)), true);
            } catch (Exception ignored) { }
            try {
                pm.setColor(Color.fromRGB(Integer.parseInt(settings.getString(POTION_COLOR, "#385dc6").substring(1), 16)));
            } catch (Exception ignored) { }
        }

        if (meta instanceof LeatherArmorMeta) {
            LeatherArmorMeta leatherMeta = (LeatherArmorMeta) meta;
            try {
                leatherMeta.setColor(Color.fromRGB(Integer.parseInt(settings.getString(ARMOR_COLOR, "#a06540").substring(1), 16)));
            } catch (Exception ignored) { }
        }

        item.setItemMeta(meta);

        return item;
    }
}
