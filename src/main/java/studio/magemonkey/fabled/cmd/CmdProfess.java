/**
 * Fabled
 * studio.magemonkey.fabled.cmd.CmdProfess
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
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.classes.FabledClass;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.language.RPGFilter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A command that allows a player to profess through classes
 */
public class CmdProfess implements IFunction, TabCompleter {
    private static final String CANNOT_USE     = "cannot-use";
    private static final String INVALID_CLASS  = "invalid-class";
    private static final String PROFESSED      = "professed";
    private static final String CANNOT_PROFESS = "cannot-profess";
    private static final String DISABLED       = "world-disabled";
    private static final String NOT_AVAILABLE  = "not-available";

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
        if (sender instanceof Player && !Fabled.getSettings().isWorldEnabled(((Player) sender).getWorld())) {
            cmd.sendMessage(sender, DISABLED, "&4You cannot use this command in this world");
        }

        // Only players have profession options
        else if (sender instanceof Player) {
            PlayerData data = Fabled.getPlayerData((Player) sender);

            if (args.length == 0) {
                if (!data.showProfession((Player) sender))
                    cmd.sendMessage(sender,
                            NOT_AVAILABLE,
                            ChatColor.RED + "There's no profession available at this time");
            } else {
                String name = args[0];
                for (int i = 1; i < args.length; i++) name += ' ' + args[i];

                FabledClass target = Fabled.getClass(name);

                // Invalid class
                if (target == null) {
                    cmd.sendMessage(sender, INVALID_CLASS, ChatColor.RED + "That is not a valid class");
                }

                // Can profess
                else if (data.canProfess(target)) {
                    data.profess(target);
                    cmd.sendMessage(sender,
                            PROFESSED,
                            ChatColor.DARK_GREEN + "You are now a " + ChatColor.GOLD + "{class}",
                            RPGFilter.CLASS.setReplacement(target.getName()));
                }

                // Cannot profess
                else {
                    cmd.sendMessage(sender,
                            CANNOT_PROFESS,
                            ChatColor.RED + "You cannot profess to this class currently");
                }
            }
        }

        // Console doesn't have profession options
        else {
            cmd.sendMessage(sender, CANNOT_USE, ChatColor.RED + "This cannot be used by the console");
        }
    }

    @Override
    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender,
                                      @NotNull Command command,
                                      @NotNull String label,
                                      @NotNull String[] args) {
        if (sender instanceof Player) {
            PlayerData playerData = Fabled.getPlayerData((Player) sender);
            if (playerData == null) return null;
            return ConfigurableCommand.getTabCompletions(Fabled.getClasses().values().stream()
                    .filter(playerData::canProfess)
                    .map(FabledClass::getName)
                    .collect(Collectors.toList()), args);
        }
        return null;
    }
}
