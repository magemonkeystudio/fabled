/**
 * SkillAPI
 * com.sucy.skill.cmd.CmdBind
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2014 Steven Sucy
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
package com.sucy.skill.cmd;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.binding.BindingMenu;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import com.sucy.skill.language.RPGFilter;
import com.sucy.skill.listener.BindListener;
import mc.promcteam.engine.mccore.commands.ConfigurableCommand;
import mc.promcteam.engine.mccore.commands.IFunction;
import mc.promcteam.engine.mccore.util.TextFormatter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Command to bind a skill to an item
 */
public class CmdBind implements IFunction, TabCompleter {
    private static final String NOT_PLAYER   = "not-player";
    private static final String NOT_SKILL    = "not-skill";
    private static final String NOT_UNLOCKED = "not-unlocked";
    private static final String NO_ITEM      = "no-item";
    private static final String SKILL_BOUND  = "skill-bound";
    private static final String DISABLED     = "world-disabled";

    /**
     * Executes the command
     *
     * @param command owning command
     * @param plugin  plugin reference
     * @param sender  sender of the command
     * @param args    arguments
     */
    @Override
    public void execute(ConfigurableCommand command, Plugin plugin, CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            command.sendMessage(sender, NOT_PLAYER, "&4Only players can use this command");
            return;
        }
        Player player = (Player) sender;

        // Disabled world
        if (!SkillAPI.getSettings().isWorldEnabled(player.getWorld())) {
            command.sendMessage(sender, DISABLED, "&4You cannot use this command in this world");
            return;
        }

        ItemStack item = BindListener.getHeldItem(player.getInventory());
        if (item == null || item.getItemMeta() == null) {
            command.sendMessage(sender, NO_ITEM, "&4You are not holding an item");
            return;
        }
        ItemMeta meta = item.getItemMeta();

        if (args.length >= 1) {
            PlayerData playerData = SkillAPI.getPlayerData((Player) sender);

            StringBuilder skillName = new StringBuilder(args[0]);
            for (int i = 1; i < args.length; i++) {
                skillName.append(' ').append(args[i]);
            }
            PlayerSkill skill = playerData.getSkill(skillName.toString());

            if (skill == null) {
                command.sendMessage(sender, NOT_SKILL, "&4You do not have that skill");
            } else if (skill.getLevel() == 0) {
                command.sendMessage(sender, NOT_UNLOCKED, "&4You have not unlocked that skill");
            } else {
                List<String> bound = BindListener.getBoundSkills(item);
                if (!bound.contains(skill.getData().getKey())) {
                    bound.add(skill.getData().getKey());
                    BindListener.setBoundSkills(item, bound);
                }
                command.sendMessage(sender,
                        SKILL_BOUND,
                        "&6{skill} &2has been bound to &6{item}",
                        RPGFilter.SKILL.setReplacement(skill.getData().getName()),
                        RPGFilter.ITEM.setReplacement(meta.hasDisplayName() ? meta.getDisplayName() : TextFormatter.format(item.getType().name())));
            }
        } else {
            new BindingMenu(player, item).open();
        }
    }

    @Override
    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(commandSender instanceof Player)) return null;
        return ConfigurableCommand.getTabCompletions(SkillAPI.getPlayerData((Player) commandSender).getSkills().stream()
                .map(playerSkill -> playerSkill.getData().getName())
                .collect(Collectors.toList()), args);
    }
}