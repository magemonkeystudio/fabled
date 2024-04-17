package studio.magemonkey.fabled.cmd;

import studio.magemonkey.codex.mccore.commands.CommandManager;
import studio.magemonkey.codex.mccore.commands.ConfigurableCommand;
import studio.magemonkey.codex.mccore.commands.IFunction;
import studio.magemonkey.codex.mccore.config.Filter;
import studio.magemonkey.codex.mccore.config.parse.NumberParser;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.enums.ExpSource;
import studio.magemonkey.fabled.api.player.PlayerClass;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.language.RPGFilter;
import studio.magemonkey.fabled.manager.CmdManager;
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

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Fabled
 * studio.magemonkey.fabled.cmd.CmdExp
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
public class CmdExp implements IFunction, TabCompleter {
    public static final Pattern IS_NUMBER = Pattern.compile("-?[0-9]+");
    public static final Pattern IS_BOOL   = Pattern.compile("(true)|(false)");

    private static final String NOT_PLAYER = "not-player";
    private static final String GAVE_EXP   = "gave-exp";
    private static final String TOOK_EXP   = "took-exp";
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
    public void execute(ConfigurableCommand cmd, Plugin plugin, CommandSender sender, String... args) {
        // Disabled world
        if (sender instanceof Player && !Fabled.getSettings().isWorldEnabled(((Player) sender).getWorld())
                && args.length == 1) {
            cmd.sendMessage(sender, DISABLED, "&4You cannot use this command in this world");
        }

        // Only can show info of a player so console needs to provide a name
        else if ((args.length >= 1 && sender instanceof Player && IS_NUMBER.matcher(args[0]).matches())
                || args.length >= 2) {
            int numberIndex = IS_NUMBER.matcher(args[0]).matches() ? 0 : 1;
            if (args.length > 1 && IS_NUMBER.matcher(args[1]).matches()) numberIndex = 1;

            // Get the player data
            OfflinePlayer target = numberIndex == 0 ? (OfflinePlayer) sender : Bukkit.getOfflinePlayer(args[0]);
            if (target == null) {
                cmd.sendMessage(sender, NOT_PLAYER, ChatColor.RED + "That is not a valid player name");
                return;
            }
            PlayerData data = Fabled.getPlayerData(target);

            // Parse the experience
            double amount = NumberParser.parseDouble(args[numberIndex]);

            if (amount == 0) {
                return;
            }

            int     lastArg     = args.length - 1;
            boolean message     = IS_BOOL.matcher(args[lastArg]).matches();
            boolean showMessage = !message || Boolean.parseBoolean(args[lastArg]);
            if (message) lastArg--;


            // Give experience to a specific class group
            if (numberIndex + 1 <= lastArg) {
                PlayerClass playerClass = data.getClass(CmdManager.join(args, numberIndex + 1, lastArg));
                if (playerClass == null) {
                    return;
                }

                if (amount > 0) {
                    playerClass.giveExp(amount, ExpSource.COMMAND, showMessage);
                    if (showMessage) {
                        if (target != sender) {
                            cmd.sendMessage(
                                    sender,
                                    GAVE_EXP,
                                    ChatColor.DARK_GREEN + "You have given " + ChatColor.GOLD
                                            + "{player} {exp}{class} experience",
                                    Filter.PLAYER.setReplacement(target.getName()),
                                    RPGFilter.EXP.setReplacement("" + amount),
                                    RPGFilter.CLASS.setReplacement(' ' + playerClass.getData().getGroup()));
                        }
                    }
                } else {
                    playerClass.loseExp(-amount, false, true);
                    if (showMessage && target != sender) {
                        cmd.sendMessage(
                                sender,
                                TOOK_EXP,
                                ChatColor.DARK_GREEN + "You have taken " + ChatColor.GOLD + "{exp}{class} experience "
                                        + ChatColor.DARK_GREEN + "from " + ChatColor.GOLD + "{player}",
                                Filter.PLAYER.setReplacement(target.getName()),
                                RPGFilter.EXP.setReplacement("" + -amount),
                                RPGFilter.CLASS.setReplacement(' ' + playerClass.getData().getGroup()));
                    }
                }
            }

            // Give experience
            else {
                if (amount > 0) {
                    data.giveExp(amount, ExpSource.COMMAND, showMessage);
                    if (showMessage) {
                        if (target != sender) {
                            cmd.sendMessage(
                                    sender,
                                    GAVE_EXP,
                                    ChatColor.DARK_GREEN + "You have given " + ChatColor.GOLD
                                            + "{player} {exp}{class} experience",
                                    Filter.PLAYER.setReplacement(target.getName()),
                                    RPGFilter.EXP.setReplacement("" + amount),
                                    RPGFilter.CLASS.setReplacement(""));
                        }
                    }
                } else {
                    data.loseExp(-amount, false, true);
                    if (showMessage && target != sender) {
                        cmd.sendMessage(
                                sender,
                                TOOK_EXP,
                                ChatColor.DARK_GREEN + "You have taken " + ChatColor.GOLD + "{exp}{class} experience "
                                        + ChatColor.DARK_GREEN + "from " + ChatColor.GOLD + "{player}",
                                Filter.PLAYER.setReplacement(target.getName()),
                                RPGFilter.EXP.setReplacement("" + -amount),
                                RPGFilter.CLASS.setReplacement(""));
                    }
                }
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
        } else if (args.length > 1 && !args[1].isBlank()) {
            int i = IS_NUMBER.matcher(args[1]).matches() ? 2 : 1;
            if (i >= args.length) return null;
            return ConfigurableCommand.getTabCompletions(Fabled.getGroups(), Arrays.copyOfRange(args, i, args.length));
        }
        return null;
    }
}
