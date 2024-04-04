/**
 * Fabled
 * studio.magemonkey.fabled.cmd.CmdCast
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 Mage Monkey Studios
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

import studio.magemonkey.codex.mccore.commands.CommandManager;
import studio.magemonkey.codex.mccore.commands.ConfigurableCommand;
import studio.magemonkey.codex.mccore.commands.IFunction;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.api.skills.SkillShot;
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
 * Command to bind a skill to an item
 */
public class CmdCast implements IFunction, TabCompleter {
    private static final String NOT_SKILL     = "not-skill";
    private static final String NOT_AVAILABLE = "not-available";
    private static final String NOT_UNLOCKED  = "not-unlocked";
    private static final String NOT_PLAYER    = "not-player";
    private static final String DISABLED      = "world-disabled";

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
        // Player only command
        if (!(sender instanceof Player))
            command.sendMessage(sender, NOT_PLAYER, "&4Only players can use this command");

            // Disabled world
        else if (!Fabled.getSettings().isWorldEnabled(((Player) sender).getWorld()))
            command.sendMessage(sender, DISABLED, "&4You cannot use this command in this world");

            // Requires at least one argument
        else if (args.length >= 1) {
            PlayerData player = Fabled.getPlayerData((Player) sender);

            // Get the skill name
            String skill = String.join(" ", args);

            // Invalid skill
            if (!Fabled.isSkillRegistered(skill))
                command.sendMessage(sender, NOT_SKILL, ChatColor.RED + "That is not a valid skill name");

                // Class mismatch
            else if (!player.hasSkill(skill))
                command.sendMessage(sender,
                        NOT_AVAILABLE,
                        ChatColor.RED + "That skill is not available for your class");

                // Not unlocked
            else if (!player.hasSkill(skill) || player.getSkillLevel(skill) == 0)
                command.sendMessage(sender, NOT_UNLOCKED, ChatColor.RED + "You must level up the skill first");

            else {
                // Cast the skill
                player.cast(skill);
            }
        }

        // Invalid arguments
        else
            CommandManager.displayUsage(command, sender, 1);
    }

    @Override
    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender commandSender,
                                      @NotNull Command command,
                                      @NotNull String s,
                                      @NotNull String[] args) {
        if (!(commandSender instanceof Player)) return null;
        return ConfigurableCommand.getTabCompletions(Fabled.getPlayerData((Player) commandSender).getSkills().stream()
                .filter(playerSkill -> playerSkill.getData() instanceof SkillShot)
                .map(playerSkill -> playerSkill.getData().getKey())
                .collect(Collectors.toList()), args);
    }
}
