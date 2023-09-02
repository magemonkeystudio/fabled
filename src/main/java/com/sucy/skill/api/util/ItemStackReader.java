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

public final class ItemStackReader {
    private static final String MATERIAL    = "material";
    private static final String AMOUNT      = "amount";
    private static final String DURABILITY  = "durability";
    private static final String UNBREAKABLE = "unbreakable";
    private static final String CMD         = "cmd";
    private static final String HIDE_FLAGS  = "hide-flags";
    private static final String CUSTOM      = "custom";
    private static final String NAME        = "name";
    private static final String LORE        = "lore";

    private static final String POTION_COLOR    = "potion_color";
    private static final String POTION_TYPE     = "potion_type";
    private static final String POTION_LEVEL    = "potion_level";
    private static final String POTION_DURATION = "potion_duration";
    private static final String ARMOR_COLOR     = "armor_color";

    // Retrocompatibility

    private static final String DATA      = "data"; // Previously used instead of 'durability'
    private static final String BYTE      = "byte"; // Previously used instead of 'cmd'
    private static final String ITEM      = "item"; // Previously used instead of 'material' in ItemProjectileMechanic
    private static final String ITEM_DATA = "item-data"; // Previously used instead of 'cmd' in ItemProjectileMechanic

    private ItemStackReader() {}

    public static Material readMaterial(Settings settings) {
        String string = null;
        if (settings.has(MATERIAL)) {
            string = settings.getString(MATERIAL);
        } else if (settings.has(ITEM)) {
            string = settings.getString(ITEM);
        }

        try {
            return Material.valueOf(string.toUpperCase().replace(" ", "_"));
        } catch (NullPointerException |
                 IllegalArgumentException e) {
            return Material.ARROW;
        }
    }

    public static int readDurability(Settings settings) {
        if (settings.has(DURABILITY)) {
            return settings.getInt(DURABILITY);
        } else {
            return settings.getInt(DATA, 0);
        }
    }

    public static int readCustomModelData(Settings settings) {
        if (settings.has(CMD)) {
            return settings.getInt(CMD);
        } else if (settings.has(BYTE)) {
            return settings.getInt(BYTE);
        } else {
            return settings.getInt(ITEM_DATA, 0);
        }
    }

    public static ItemStack read(Settings settings) {
        ItemStack item = new ItemStack(readMaterial(settings), settings.getInt(AMOUNT, 1));
        ItemMeta  meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        if (meta instanceof Damageable) {
            Damageable damageable = (Damageable) meta;
            damageable.setDamage(readDurability(settings));
            meta.setUnbreakable(settings.getBool(UNBREAKABLE, false));
        }
        meta.setCustomModelData(readCustomModelData(settings));

        for (String hideFlag : settings.getStringList(HIDE_FLAGS)) {
            try {
                meta.addItemFlags(ItemFlag.valueOf("HIDE_" + hideFlag.toUpperCase().replace(' ', '_')));
            } catch (IllegalArgumentException ignored) {
            }
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
                        settings.getInt(POTION_DURATION) * 20,
                        settings.getInt(POTION_LEVEL)), true);
            } catch (Exception ignored) {
            }
            try {
                pm.setColor(Color.fromRGB(Integer.parseInt(settings.getString(POTION_COLOR, "#385dc6").substring(1),
                        16)));
            } catch (Exception ignored) {
            }
        }

        if (meta instanceof LeatherArmorMeta) {
            LeatherArmorMeta leatherMeta = (LeatherArmorMeta) meta;
            try {
                leatherMeta.setColor(Color.fromRGB(Integer.parseInt(settings.getString(ARMOR_COLOR, "#a06540")
                        .substring(1), 16)));
            } catch (Exception ignored) {
            }
        }

        item.setItemMeta(meta);

        return item;
    }
}
