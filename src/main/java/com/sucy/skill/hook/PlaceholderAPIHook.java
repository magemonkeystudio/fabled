package com.sucy.skill.hook;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.player.PlayerAccounts;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.util.PlaceholderUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * ProSkillAPI © 2023
 * com.sucy.skill.hook.PlaceholderAPIHook
 */
public class PlaceholderAPIHook extends PlaceholderExpansion {
    @Override
    public boolean persist() {
        return true;
    }

    public static String format(final String message, final Player player) {
        return PlaceholderAPI.setPlaceholders(player, message);
    }

    public static ItemStack processPlaceholders(ItemStack item, Player player) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        if (meta.hasDisplayName()) {
            meta.setDisplayName(format(meta.getDisplayName(), player));
        }

        if (meta.hasLore()) {
            List<String> lore = meta.getLore()
                    .stream().map(line -> format(line, player))
                    .collect(Collectors.toList());
            meta.setLore(lore);
        }

        item.setItemMeta(meta);
        return item;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor() {
        return "Spark";
    }

    @Override
    public String getIdentifier() {
        return "sapi";
    }

    @Override
    public String getVersion() {
        return "1.0.1";
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        identifier = PlaceholderAPI.setBracketPlaceholders(player, identifier);

        return PlaceholderUtil.replace(player, identifier);
    }
}
