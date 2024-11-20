/**
 * Fabled
 * studio.magemonkey.fabled.cmd.CmdForceAttr
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

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import studio.magemonkey.codex.mccore.commands.CommandManager;
import studio.magemonkey.codex.mccore.commands.ConfigurableCommand;
import studio.magemonkey.codex.mccore.commands.IFunction;
import studio.magemonkey.codex.mccore.config.CustomFilter;
import studio.magemonkey.codex.mccore.config.Filter;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.language.RPGFilter;

import java.util.Arrays;
import java.util.List;

/**
 * A command that resets the attributes of a player
 */
public class CmdForceAttr implements IFunction, TabCompleter {
    private static final String NOT_PLAYER     = "not-player";
    private static final String RESET          = "reset";
    private static final String RESET_ONE      = "reset-one";
    private static final String RESET_ONE_FAIL = "reset-one-fail";
    private static final String NOT_ATTR       = "not-attribute";
    private static final String NOT_NUM        = "not-number";
    private static final String GAVE_ATTR      = "gave-attributes";
    private static final String GAVE_ATTR_FAIL = "gave-attributes-fail";

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
        // Only players have profession options
        if (args.length < 1) {
            CommandManager.displayUsage(cmd, sender);
            return;
        }

        // Grab the player data
        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
        if (player == null) {
            cmd.sendMessage(sender, NOT_PLAYER, ChatColor.RED + "That is not a valid player name");
            return;
        }
        PlayerData data = Fabled.getData(player);

        // Reset their attributes
        if (args.length == 1) {
            List<String> refunded = data.refundAttributes();
            cmd.sendMessage(sender,
                    RESET,
                    ChatColor.GOLD + "{player}'s " + ChatColor.DARK_GREEN + "attributes were refunded for attributes "
                            + ChatColor.GOLD + "{attributes}",
                    Filter.PLAYER.setReplacement(args[0]),
                    new CustomFilter("attributes", StringUtils.join(refunded, ", ")));
            return;
        }

        // Validate the attribute
        if (Fabled.getAttributesManager().getAttribute(args[1]) == null) {
            cmd.sendMessage(sender,
                    NOT_ATTR,
                    ChatColor.GOLD + "{name}" + ChatColor.RED + " is not a valid attribute name",
                    RPGFilter.NAME.setReplacement(args[1]));
            return;
        }

        // Reset a specific attribute
        if (args.length == 2) {
            boolean success = data.refundAttributeAll(args[1]);
            if (!success) {
                cmd.sendMessage(sender,
                        RESET_ONE_FAIL,
                        ChatColor.GOLD + "{player}'s " + ChatColor.DARK_GREEN + "{name}" + ChatColor.RED
                                + " attributes were not refunded",
                        Filter.PLAYER.setReplacement(args[0]),
                        RPGFilter.NAME.setReplacement(args[1]));
                return;
            }
            cmd.sendMessage(sender,
                    RESET_ONE,
                    ChatColor.GOLD + "{player}'s " + ChatColor.DARK_GREEN + "{name}" + ChatColor.GOLD
                            + " attributes were refunded",
                    Filter.PLAYER.setReplacement(args[0]),
                    RPGFilter.NAME.setReplacement(args[1]));
        }

        // Give a specific attribute
        else {
            try {
                int     amount  = Integer.parseInt(args[2]);
                boolean success = data.giveAttribute(args[1], amount);
                if (!success) {
                    cmd.sendMessage(sender,
                            GAVE_ATTR_FAIL,
                            ChatColor.GOLD + "{player}" + ChatColor.RED + " was not given " + ChatColor.GOLD
                                    + "{amount} {name} points",
                            Filter.PLAYER.setReplacement(args[0]),
                            RPGFilter.NAME.setReplacement(args[1]));
                    return;
                }
                cmd.sendMessage(sender,
                        GAVE_ATTR,
                        ChatColor.GOLD + "{player}" + ChatColor.DARK_GREEN + " was given " + ChatColor.GOLD
                                + "{amount} {name} points",
                        Filter.PLAYER.setReplacement(args[0]),
                        RPGFilter.NAME.setReplacement(args[1]),
                        Filter.AMOUNT.setReplacement(amount + ""));
            } catch (Exception ex) {
                cmd.sendMessage(sender,
                        NOT_NUM,
                        ChatColor.GOLD + "{amount} " + ChatColor.RED + "is not an integer number",
                        Filter.AMOUNT.setReplacement(args[2]));
            }
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
        } else if (args.length > 1) {
            return ConfigurableCommand.getTabCompletions(Fabled.getAttributesManager().getKeys(),
                    Arrays.copyOfRange(args, 1, args.length));
        }
        return null;
    }
}
