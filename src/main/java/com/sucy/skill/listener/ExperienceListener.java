package com.sucy.skill.listener;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.enums.ExpSource;
import com.sucy.skill.api.event.PlayerExperienceGainEvent;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
import mc.promcteam.engine.mccore.config.CommentedConfig;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.OptionalInt;
import java.util.Set;

/**
 * ProSkillAPI © 2023
 * com.sucy.skill.listener.ExperienceListener
 */
public class ExperienceListener extends SkillAPIListener {

    private static final String CONFIG_KEY = "unnatural";

    boolean         track;
    HashSet<String> unnatural = new HashSet<>();

    public ExperienceListener() {
        track = SkillAPI.getSettings().isTrackBreak();
        if (track) {
            CommentedConfig data = SkillAPI.getConfig("data/placed");
            unnatural = new HashSet<>(data.getConfig().getList(CONFIG_KEY));
        }
    }

    @Override
    public void cleanup() {
        if (track) {
            CommentedConfig config = SkillAPI.getConfig("data/placed");
            config.getConfig().set(CONFIG_KEY, new ArrayList<>(unnatural));
            config.save();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        if (track && unnatural.contains(format(event.getBlock()))) {
            return;
        }

        PlayerData playerData = SkillAPI.getPlayerData(event.getPlayer());
        for (PlayerClass playerClass : playerData.getClasses()) {
            double yield = SkillAPI.getSettings().getBreakYield(playerClass, event.getBlock().getType());
            if (yield > 0) {
                playerClass.giveExp(yield, ExpSource.BLOCK_BREAK);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        if (track) {
            unnatural.add(format(event.getBlock()));
        }

        PlayerData playerData = SkillAPI.getPlayerData(event.getPlayer());
        for (PlayerClass playerClass : playerData.getClasses()) {
            double yield = SkillAPI.getSettings().getPlaceYield(playerClass, event.getBlock().getType());
            if (yield > 0) {
                playerClass.giveExp(yield, ExpSource.BLOCK_PLACE);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCraft(CraftItemEvent event) {
        PlayerData playerData = SkillAPI.getPlayerData((Player) event.getWhoClicked());
        for (PlayerClass playerClass : playerData.getClasses()) {
            double yield = SkillAPI.getSettings().getCraftYield(playerClass, event.getRecipe().getResult().getType());
            if (yield > 0) {
                playerClass.giveExp(yield, ExpSource.CRAFT);
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onExpGain(PlayerExperienceGainEvent event){
        Player player = event.getPlayerData().getPlayer();
        Set<PermissionAttachmentInfo> perms = player.getEffectivePermissions();
        OptionalInt max = perms.stream()
                .filter(c->c.getPermission().startsWith("skillapi.exp.booster"))
                .map(c->c.getPermission().substring(21))
                .mapToInt(c->Integer.valueOf(c))
                .max();
        if (max.isEmpty()) return;
        event.setExp(event.getExp() + event.getExp()*max.getAsInt()/100);
    }

    private String format(Block block) {
        Location loc = block.getLocation();
        return loc.getWorld().getName() + "|" + loc.getBlockX() + "|" + loc.getBlockY() + "|" + loc.getBlockZ();
    }
}
