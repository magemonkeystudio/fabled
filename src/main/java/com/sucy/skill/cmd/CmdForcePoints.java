/**
 * SkillAPI
 * com.sucy.skill.cmd.CmdPoints
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
import com.sucy.skill.api.enums.PointSource;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.language.RPGFilter;
import mc.promcteam.engine.mccore.commands.CommandManager;
import mc.promcteam.engine.mccore.commands.ConfigurableCommand;
import mc.promcteam.engine.mccore.commands.IFunction;
import mc.promcteam.engine.mccore.config.Filter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * A command that gives a player class experience
 */
public class CmdForcePoints implements IFunction {
    private static final String NOT_PLAYER   = "not-player";
    private static final String NOT_GROUP   = "not-group";
    private static final String NOT_NUMBER   = "not-number";
    private static final String GAVE_SP      = "gave-points";
    private static final String SET_SP      = "set-points";
    private static final String DISABLED     = "world-disabled";

    /**
     * Runs the command
     *
     * @param cmd    command that was executed
     * @param plugin plugin reference
     * @param sender sender of the command
     * @param args   argument list
     */
    @Override
    public void execute(ConfigurableCommand cmd, Plugin plugin, CommandSender sender, String[] args) {
        Player target = null;
        Integer amount = null;
        String group = null;

        if (args.length == 2) {
            if (sender instanceof Player) target = (Player) sender;
            else {
                cmd.sendMessage(sender, NOT_PLAYER, ChatColor.RED + "That is not a valid player name");
                return;
            }
            amount = Integer.parseInt(args[1]);
        } else if (args.length == 3) {
            // Group missing?
            target = Bukkit.getPlayer(args[1]);
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException ignored) {}

            if (target == null || amount == null) {
                // Player missing?
                if (sender instanceof Player) target = (Player) sender;
                else {
                    CommandManager.displayUsage(cmd, sender);
                    return;
                }
                try {
                    amount = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    CommandManager.displayUsage(cmd, sender);
                    return;
                }
                group = args[2].toLowerCase();
                if (!SkillAPI.getGroups().contains(group)) {
                    CommandManager.displayUsage(cmd, sender);
                    return;
                }

            }
        } else if (args.length == 4) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                cmd.sendMessage(sender, NOT_PLAYER, ChatColor.RED + "That is not a valid player name");
                return;
            }
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException ignored) {
                cmd.sendMessage(sender, NOT_NUMBER, ChatColor.RED + "That is not a valid skill point amount");
                return;
            }
            group = args[3].toLowerCase();
            if (!SkillAPI.getGroups().contains(group)) {
                cmd.sendMessage(sender, NOT_GROUP, ChatColor.RED + "That is not a valid class group");
                return;
            }
        }

        // Disabled world
        if (!SkillAPI.getSettings().isWorldEnabled(target.getWorld())) {
            cmd.sendMessage(sender, DISABLED, "&4You cannot use this command in this world");
            return;
        }

        PlayerData data = SkillAPI.getPlayerData(target);
        if (data == null) {
            cmd.sendMessage(sender, NOT_PLAYER, ChatColor.RED + "That is not a valid player name");
            return;
        }

        switch (args[0].toLowerCase()) {
            case "add" -> {
                if (group == null) {
                    data.givePoints(amount, PointSource.COMMAND);
                    cmd.sendMessage(sender,
                            GAVE_SP,
                            ChatColor.DARK_GREEN + "You have given " + ChatColor.GOLD + "{player} {points}{group} "+ ChatColor.DARK_GREEN + "skill points",
                            Filter.PLAYER.setReplacement(target.getName()),
                            RPGFilter.GROUP.setReplacement(""),
                            RPGFilter.POINTS.setReplacement("" + amount));
                } else {
                    PlayerClass clazz = data.getClass(group);
                    if (clazz == null) {
                        cmd.sendMessage(sender, NOT_GROUP, ChatColor.RED + "That is not a valid class group");
                    } else {
                        clazz.givePoints(amount, PointSource.COMMAND);
                        cmd.sendMessage(sender,
                                GAVE_SP,
                                ChatColor.DARK_GREEN + "You have given " + ChatColor.GOLD + "{player} {points}{group} "+ ChatColor.DARK_GREEN + "skill points",
                                Filter.PLAYER.setReplacement(target.getName()),
                                RPGFilter.GROUP.setReplacement(' '+group),
                                RPGFilter.POINTS.setReplacement("" + amount));
                    }
                }
            }
            case "set" -> {
                if (group == null) {
                    data.setPoints(amount);
                    cmd.sendMessage(sender,
                            SET_SP,
                            ChatColor.DARK_GREEN + "You have set " + ChatColor.GOLD + "{player}'s{group} " + ChatColor.DARK_GREEN + "skill points to "+ ChatColor.GOLD + "{points}",
                            Filter.PLAYER.setReplacement(target.getName()),
                            RPGFilter.GROUP.setReplacement(""),
                            RPGFilter.POINTS.setReplacement("" + amount));
                } else {
                    PlayerClass clazz = data.getClass(group);
                    if (clazz == null) {
                        cmd.sendMessage(sender, NOT_GROUP, ChatColor.RED + "That is not a valid class group");
                    } else {
                        clazz.setPoints(amount);
                        cmd.sendMessage(sender,
                                SET_SP,
                                ChatColor.DARK_GREEN + "You have set " + ChatColor.GOLD + "{player}'s{group} " + ChatColor.DARK_GREEN + "skill points to "+ ChatColor.GOLD + "{points}",
                                Filter.PLAYER.setReplacement(target.getName()),
                                RPGFilter.GROUP.setReplacement(' '+group),
                                RPGFilter.POINTS.setReplacement("" + amount));
                    }
                }
            }
            default -> CommandManager.displayUsage(cmd, sender);
        }
    }
}
