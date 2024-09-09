/**
 * Fabled
 * studio.magemonkey.fabled.cmd.CmdForceAccount
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
import studio.magemonkey.fabled.PlayerLoader;
import studio.magemonkey.fabled.api.player.PlayerAccounts;
import studio.magemonkey.fabled.language.RPGFilter;
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
 * Command to clear all bound skills
 */
public class CmdForceAccount implements IFunction, TabCompleter {
    private static final String NOT_PLAYER  = "not-player";
    private static final String NOT_ACCOUNT = "not-account";
    private static final String CHANGED     = "account-changed";
    private static final String TARGET      = "target-notice";

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
        // Needs two arguments
        if (args.length < 2) {
            command.displayHelp(sender);
        }

        // Switch accounts if valid number
        else {
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);

            if (player == null) {
                command.sendMessage(sender, NOT_PLAYER, "&4That is not a valid player name");
                return;
            }

            PlayerAccounts accounts = PlayerLoader.getPlayerAccounts(player);
            try {
                int id = Integer.parseInt(args[1]);

                if (accounts.getAccountLimit() >= id && id > 0) {
                    accounts.setAccount(id);
                    command.sendMessage(sender,
                            CHANGED,
                            ChatColor.GOLD + "{player}'s" + ChatColor.DARK_GREEN + " active account has been changed",
                            Filter.PLAYER.setReplacement(player.getName()));
                    if (player.isOnline()) {
                        command.sendMessage((Player) player,
                                TARGET,
                                ChatColor.DARK_GREEN + "Your account has been forced to " + ChatColor.GOLD
                                        + "Account #{account}",
                                RPGFilter.ACCOUNT.setReplacement(id + ""));
                    }
                    return;
                }
            } catch (Exception ex) {
                // Invalid ID
            }

            command.sendMessage(sender, NOT_ACCOUNT, ChatColor.RED + "That is not a valid account ID");
        }
    }

    @Override
    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender commandSender,
                                      @NotNull Command command,
                                      @NotNull String s,
                                      @NotNull String[] args) {
        if (args.length == 1) return ConfigurableCommand.getPlayerTabCompletions(commandSender, args[0]);
        return null;
    }
}