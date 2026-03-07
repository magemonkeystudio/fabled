/**
 * Fabled
 * studio.magemonkey.fabled.cmd.CmdLevel
 * <p>
 * The MIT License (MIT)
 * <p>
 * © 2026 VoidEdge
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
import studio.magemonkey.codex.mccore.commands.CommandManager;
import studio.magemonkey.codex.mccore.commands.ConfigurableCommand;
import studio.magemonkey.codex.mccore.commands.IFunction;
import studio.magemonkey.codex.mccore.config.Filter;
import studio.magemonkey.codex.mccore.config.parse.NumberParser;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.enums.ExpSource;
import studio.magemonkey.fabled.api.player.PlayerClass;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.cmd.api.NumericAction;
import studio.magemonkey.fabled.language.RPGFilter;
import studio.magemonkey.fabled.manager.CmdManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * A command that gives a player class levels
 * <p>
 * /class level [player] &lt;add|remove|set&gt; &lt;amount&gt; [class] [true|false] - Gives a player class levels
 * Arguments:
 * - player: The player to give levels to
 * - add/remove/set: The action to perform
 * - amount: The amount of levels to give
 * - class: The class to give levels to
 * - true/false: Whether to show the message to the player
 */
public class CmdLevel implements IFunction, TabCompleter {
    private static final Pattern IS_NUMBER = Pattern.compile("-?[0-9]+");
    private static final Pattern IS_BOOL   = Pattern.compile("(true)|(false)");

    private static final String NOT_PLAYER     = "not-player";
    private static final String GAVE_LEVEL     = "gave-level";
    private static final String RECEIVED_LEVEL = "received-level";
    private static final String DISABLED       = "world-disabled";
    private static final String NO_CLASSES     = "no-classes";

    /**
     * Runs the command
     *
     * @param cmd    command that was executed
     * @param plugin plugin reference
     * @param sender sender of the command
     * @param args   argument list
     * @param silent whether to suppress output
     */
    @Override
    public void execute(ConfigurableCommand cmd, Plugin plugin, CommandSender sender, String[] args, boolean silent) {
        int           numberIndex = getNumberIndex(args);
        OfflinePlayer target;
        if (!(sender instanceof Player)) {
            // Not enough arguments -- Console needs to supply a user and an amount
            if (args.length < 3) {
                CommandManager.displayUsage(cmd, sender);
                return;
            }

            target = Bukkit.getOfflinePlayer(args[0]);
        } else {
            // Disabled world
            if (!Fabled.getSettings().isWorldEnabled(((Player) sender).getWorld()) && args.length == 2) {
                cmd.sendMessage(sender, DISABLED, "&4You cannot use this command in this world", silent);
                return;
            }

            // Not enough arguments -- Player needs to supply at least an amount
            if (args.length < 2) {
                CommandManager.displayUsage(cmd, sender);
                return;
            }

            target = numberIndex == 1 ? (OfflinePlayer) sender : Bukkit.getOfflinePlayer(args[0]);
        }


        // Only can show info of a player so console needs to provide a name
        if (target == null) {
            cmd.sendMessage(sender, NOT_PLAYER, ChatColor.RED + "That is not a valid player name", silent);
            return;
        }
        // Get the player data
        PlayerData data = Fabled.getData(target);

        NumericAction action;
        try {
            action = NumericAction.valueOf(args[numberIndex - 1].toUpperCase(Locale.US));
        } catch (IllegalArgumentException e) {
            CommandManager.displayUsage(cmd, sender);
            return;
        }

        // Parse the levels
        int amount = NumberParser.parseInt(args[numberIndex]);
        // Invalid amount of levels
        if (amount == 0) {
            return;
        }

        if (action == NumericAction.REMOVE) amount = -amount;

        int lastArg = args.length - 1;

        // Give levels to a specific class group
        boolean success;
        if (numberIndex + 1 <= lastArg) {
            PlayerClass playerClass = data.getClass(CmdManager.join(args, numberIndex + 1, lastArg));
            if (playerClass == null) {
                CommandManager.displayUsage(cmd, sender);
                return;
            }

            if (action == NumericAction.SET) amount = amount - playerClass.getLevel();

            if (amount > 0) {
                playerClass.giveLevels(amount);
            } else {
                playerClass.loseLevels(-amount);
            }
            success = true;
        }

        // Give levels
        else {
            if (action == NumericAction.SET) {
                success = data.setLevel(amount, ExpSource.COMMAND);
            } else if (amount > 0) {
                success = data.giveLevels(amount, ExpSource.COMMAND);
            } else {
                data.loseLevels(-amount);
                success = true;
            }
        }

        // Messages
        if (!success) {
            cmd.sendMessage(sender,
                    NO_CLASSES,
                    ChatColor.RED + "You aren't professed as a class that receives experience from commands",
                    silent,
                    Filter.PLAYER.setReplacement(target.getName()),
                    RPGFilter.LEVEL.setReplacement("" + amount));
        } else if (target != sender) {
                cmd.sendMessage(sender,
                    GAVE_LEVEL,
                    ChatColor.DARK_GREEN + "You have given " + ChatColor.GOLD + "{player} {level} levels",
                    silent,
                    Filter.PLAYER.setReplacement(target.getName()),
                    RPGFilter.LEVEL.setReplacement("" + amount));
        }
        if (target.isOnline()) {
            cmd.sendMessage(target.getPlayer(),
                    RECEIVED_LEVEL,
                    ChatColor.DARK_GREEN + "You have received " + ChatColor.GOLD + "{level} levels "
                            + ChatColor.DARK_GREEN + "from " + ChatColor.GOLD + "{player}",
                    silent,
                    Filter.PLAYER.setReplacement(sender.getName()),
                    RPGFilter.LEVEL.setReplacement("" + amount));
        }
    }

    private int getNumberIndex(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (IS_NUMBER.matcher(args[i]).matches()) {
                return i;
            }
        }

        return -1;
    }

    @Override
    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender commandSender,
                                      @NotNull Command command,
                                      @NotNull String s,
                                      @NotNull String[] args) {
        // Filter -s and --silent out of the args
        if (args.length > 0) {
            args = Arrays.stream(args)
                    .filter(arg -> !arg.equalsIgnoreCase("-s") && !arg.equalsIgnoreCase("--silent"))
                    .toArray(String[]::new);
        }

        if (args.length == 1) {
            List<String> list = new ArrayList<>(ConfigurableCommand.getPlayerTabCompletions(commandSender, args[0]));
            list.add("add");
            list.add("remove");
            list.add("set");
            return list;
        } else if (args.length == 2) {
            // The second arg is add/set/remove if the first arg isn't add/set/remove
            if (!args[args.length - 2].equalsIgnoreCase("add") && !args[args.length - 2].equalsIgnoreCase("set")
                    && !args[args.length - 2].equalsIgnoreCase("remove")) {
                return List.of("add", "remove", "set");
            } else {
                return List.of("<level>");
            }
        } else if (args.length > 2) {
            // If the first arg is a number, the second arg could be a player or a class
            int numberIndex = getNumberIndex(args);
            if (numberIndex == -1) return List.of("<level>");

            return ConfigurableCommand.getTabCompletions(Fabled.getGroups(),
                    Arrays.copyOfRange(args, numberIndex + 1, args.length));
        }
        return null;
    }
}
