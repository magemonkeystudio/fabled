package studio.magemonkey.fabled.listener;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.permissions.PermissionAttachmentInfo;
import studio.magemonkey.codex.mccore.config.CommentedConfig;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.enums.ExpSource;
import studio.magemonkey.fabled.api.event.PlayerExperienceGainEvent;
import studio.magemonkey.fabled.api.player.PlayerClass;
import studio.magemonkey.fabled.api.player.PlayerData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.OptionalInt;
import java.util.Set;

/**
 * Fabled © 2024
 * studio.magemonkey.fabled.listener.ExperienceListener
 */
public class ExperienceListener extends FabledListener {

    private static final String CONFIG_KEY = "unnatural";

    boolean         track;
    HashSet<String> unnatural = new HashSet<>();

    public ExperienceListener() {
        track = Fabled.getSettings().isTrackBreak();
        if (track) {
            CommentedConfig data = Fabled.getConfig("data/placed");
            unnatural = new HashSet<>(data.getConfig().getList(CONFIG_KEY));
        }
    }

    @Override
    public void cleanup() {
        if (track) {
            CommentedConfig config = Fabled.getConfig("data/placed");
            config.getConfig().set(CONFIG_KEY, new ArrayList<>(unnatural));
            config.save();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        if (track && unnatural.contains(format(event.getBlock()))) {
            return;
        }

        PlayerData playerData = Fabled.getData(event.getPlayer());
        for (PlayerClass playerClass : playerData.getClasses()) {
            double yield = Fabled.getSettings().getBreakYield(playerClass, event.getBlock().getType());
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

        PlayerData playerData = Fabled.getData(event.getPlayer());
        for (PlayerClass playerClass : playerData.getClasses()) {
            double yield = Fabled.getSettings().getPlaceYield(playerClass, event.getBlock().getType());
            if (yield > 0) {
                playerClass.giveExp(yield, ExpSource.BLOCK_PLACE);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCraft(CraftItemEvent event) {
        PlayerData playerData = Fabled.getData((Player) event.getWhoClicked());
        for (PlayerClass playerClass : playerData.getClasses()) {
            double yield = Fabled.getSettings().getCraftYield(playerClass, event.getRecipe().getResult().getType());
            if (yield > 0) {
                playerClass.giveExp(yield, ExpSource.CRAFT);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onExpGain(PlayerExperienceGainEvent event) {
        Player                        player = event.getPlayerData().getPlayer();
        Set<PermissionAttachmentInfo> perms  = player.getEffectivePermissions();
        OptionalInt max = perms.stream()
                .filter(c -> c.getPermission().startsWith("fabled.exp.booster"))
                .map(c -> c.getPermission().substring(21))
                .mapToInt(number -> {
                    try {
                        return Integer.parseInt(number);
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                })
                .max();
        if (max.isEmpty()) return;
        event.setExp(event.getExp() * max.getAsInt() / 100);
    }

    private String format(Block block) {
        Location loc = block.getLocation();
        return loc.getWorld().getName() + "|" + loc.getBlockX() + "|" + loc.getBlockY() + "|" + loc.getBlockZ();
    }
}
