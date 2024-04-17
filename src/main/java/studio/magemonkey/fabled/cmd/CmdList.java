/**
 * Fabled
 * studio.magemonkey.fabled.cmd.CmdList
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 MageMonkeyStudio
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
package studio.magemonkey.fabled.cmd;

import studio.magemonkey.codex.mccore.commands.ConfigurableCommand;
import studio.magemonkey.codex.mccore.commands.IFunction;
import studio.magemonkey.codex.mccore.config.Filter;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.player.PlayerAccounts;
import studio.magemonkey.fabled.api.player.PlayerClass;
import studio.magemonkey.fabled.api.player.PlayerData;
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
 * A command that displays a player's account information
 */
public class CmdList implements IFunction, TabCompleter {
    private static final String NEEDS_ARGS = "needs-player";
    private static final String TITLE      = "title";
    private static final String LINE       = "line";
    private static final String END        = "end";
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

            PlayerAccounts accounts = Fabled.getPlayerAccountData(target);
            cmd.sendMessage(sender,
                    TITLE,
                    ChatColor.DARK_GRAY + "--" + ChatColor.DARK_GREEN + " {player} " + ChatColor.DARK_GRAY
                            + "-----------",
                    Filter.PLAYER.setReplacement(target.getName()));
            String line = cmd.getMessage(LINE,
                    ChatColor.GRAY + "[" + ChatColor.GOLD + "{id}" + ChatColor.GRAY + "] " + ChatColor.DARK_GREEN + "Lv"
                            + ChatColor.GOLD + "{level} {class}");
            if (accounts != null) {
                for (int i = 1; i <= accounts.getAccountLimit(); i++) {
                    PlayerData  data  = accounts.getData(i);
                    PlayerClass cData = data == null ? null : data.getMainClass();
                    String      name  = cData == null ? ChatColor.GRAY + "Not Professed" : cData.getData().getPrefix();
                    String      level = cData == null ? "0" : cData.getLevel() + "";
                    sender.sendMessage(
                            line
                                    .replace("{id}", i + "")
                                    .replace("{level}", level)
                                    .replace("{class}", name)
                    );
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
