/**
 * Fabled
 * com.promcteam.fabled.cmd.CmdInfo
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 ProMCTeam
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
package com.promcteam.fabled.cmd;

import com.promcteam.codex.mccore.commands.ConfigurableCommand;
import com.promcteam.codex.mccore.commands.IFunction;
import com.promcteam.codex.mccore.config.Filter;
import com.promcteam.codex.mccore.util.TextFormatter;
import com.promcteam.fabled.Fabled;
import com.promcteam.fabled.api.player.PlayerClass;
import com.promcteam.fabled.api.player.PlayerData;
import com.promcteam.fabled.language.RPGFilter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A command that displays a player's current class information
 */
public class CmdInfo implements IFunction, TabCompleter {
    private static final String NEEDS_ARGS = "needs-player";
    private static final String TITLE      = "title";
    private static final String CATEGORY   = "category";
    private static final String PROFESSION = "profession";
    private static final String EXP        = "exp";
    private static final String SEPARATOR  = "separator";
    private static final String END        = "end";
    private static final String NO_CLASS   = "no-class";
    private static final String NOT_PLAYER = "not-player";
    private static final String DISABLED   = "world-disabled";

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
        // Disabled world
        if (sender instanceof Player && !Fabled.getSettings().isWorldEnabled(((Player) sender).getWorld())
                && args.length == 0) {
            cmd.sendMessage(sender, DISABLED, "&4You cannot use this command in this world");
        }

        // Only can show info of a player so console needs to provide a name
        else if (sender instanceof Player || args.length >= 1) {
            OfflinePlayer target = args.length == 0 ? (OfflinePlayer) sender : Bukkit.getOfflinePlayer(args[0]);
            if (target == null) {
                cmd.sendMessage(sender, NOT_PLAYER, ChatColor.RED + "That is not a valid player name");
                return;
            }

            PlayerData data = Fabled.getPlayerData(target);
            cmd.sendMessage(sender,
                    TITLE,
                    ChatColor.DARK_GRAY + "--" + ChatColor.DARK_GREEN + " {player} " + ChatColor.DARK_GRAY
                            + "-----------",
                    Filter.PLAYER.setReplacement(target.getName()));
            String  separator = cmd.getMessage(SEPARATOR, ChatColor.DARK_GRAY + "----------------------------");
            boolean first     = true;
            if (data != null) {
                for (String group : Fabled.getGroups()) {
                    PlayerClass c = data.getClass(group);

                    // Separator message if not the first group
                    if (first) {
                        first = false;
                    } else {
                        sender.sendMessage(separator);
                    }

                    // Compose the message
                    cmd.sendMessage(sender,
                            CATEGORY,
                            ChatColor.GOLD + "{group}" + ChatColor.GRAY + ": ",
                            RPGFilter.GROUP.setReplacement(TextFormatter.format(group)));
                    PlayerClass profession = data.getClass(group);
                    if (profession == null) {
                        cmd.sendMessage(sender, NO_CLASS, ChatColor.GRAY + "Not Professed");
                    } else {
                        cmd.sendMessage(sender,
                                PROFESSION,
                                ChatColor.AQUA + "Lv{level} " + ChatColor.DARK_GREEN + "{profession}",
                                RPGFilter.LEVEL.setReplacement(profession.getLevel() + ""),
                                RPGFilter.PROFESSION.setReplacement(profession.getData().getName()));
                        cmd.sendMessage(sender,
                                EXP,
                                ChatColor.AQUA + "Exp " + ChatColor.DARK_GREEN + "{exp}",
                                RPGFilter.EXP.setReplacement(
                                        (int) profession.getExp() + "/" + profession.getRequiredExp()));
                    }
                }
            }
            cmd.sendMessage(sender, END, ChatColor.DARK_GRAY + "----------------------------");
        }

        // Console doesn't have profession options
        else {
            cmd.sendMessage(sender, NEEDS_ARGS, ChatColor.RED + "A player name is required from the console");
        }
    }

    @Override
    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender commandSender,
                                      @NotNull Command command,
                                      @NotNull String s,
                                      @NotNull String[] args) {
        if (args.length == 1) {
            return ConfigurableCommand.getPlayerTabCompletions(commandSender, args[0]);
        }
        return null;
    }
}
