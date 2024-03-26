/**
 * Fabled
 * com.promcteam.fabled.cmd.CmdPoints
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

import com.promcteam.codex.mccore.commands.CommandManager;
import com.promcteam.codex.mccore.commands.ConfigurableCommand;
import com.promcteam.codex.mccore.commands.IFunction;
import com.promcteam.codex.mccore.config.Filter;
import com.promcteam.fabled.Fabled;
import com.promcteam.fabled.api.enums.ExpSource;
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
 * A command that gives a player class experience
 */
public class CmdPoints implements IFunction, TabCompleter {
    private static final String NOT_PLAYER   = "not-player";
    private static final String NOT_NUMBER   = "not-number";
    private static final String NOT_POSITIVE = "not-positive";
    private static final String GAVE_SP      = "gave-points";
    private static final String RECEIVED_SP  = "received-points";
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
        // Disabled world
        if (sender instanceof Player && !Fabled.getSettings().isWorldEnabled(((Player) sender).getWorld())
                && args.length == 1) {
            cmd.sendMessage(sender, DISABLED, "&4You cannot use this command in this world");
        }

        // Only can show info of a player so console needs to provide a name
        else if (args.length >= 1 && (args.length >= 2 || sender instanceof Player)) {
            // Get the player data
            OfflinePlayer target = args.length == 1 ? (OfflinePlayer) sender : Bukkit.getOfflinePlayer(args[0]);
            if (target == null) {
                cmd.sendMessage(sender, NOT_PLAYER, ChatColor.RED + "That is not a valid player name");
                return;
            }

            // Parse the skill points
            int amount;
            try {
                amount = Integer.parseInt(args[args.length == 1 ? 0 : 1]);
            } catch (Exception ex) {
                cmd.sendMessage(sender, NOT_NUMBER, ChatColor.RED + "That is not a valid skill point amount");
                return;
            }

            // Invalid amount of skill points
            if (amount <= 0) {
                cmd.sendMessage(sender,
                        NOT_POSITIVE,
                        ChatColor.RED + "You must give a positive amount of skill points");
                return;
            }

            // Give skill points
            PlayerData data = Fabled.getPlayerData(target);
            data.givePoints(amount, ExpSource.COMMAND);

            // Messages
            if (target != sender) {
                cmd.sendMessage(sender,
                        GAVE_SP,
                        ChatColor.DARK_GREEN + "You have given " + ChatColor.GOLD + "{player} {points} skill points",
                        Filter.PLAYER.setReplacement(target.getName()),
                        RPGFilter.POINTS.setReplacement("" + amount));
            }
            if (target.isOnline()) {
                cmd.sendMessage(target.getPlayer(),
                        RECEIVED_SP,
                        ChatColor.DARK_GREEN + "You have received " + ChatColor.GOLD + "{points} skill points "
                                + ChatColor.DARK_GREEN + "from " + ChatColor.GOLD + "{player}",
                        Filter.PLAYER.setReplacement(sender.getName()),
                        RPGFilter.POINTS.setReplacement("" + amount));
            }
        }

        // Not enough arguments
        else {
            CommandManager.displayUsage(cmd, sender);
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
