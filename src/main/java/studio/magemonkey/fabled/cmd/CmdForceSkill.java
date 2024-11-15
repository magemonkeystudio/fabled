/**
 * Fabled
 * studio.magemonkey.fabled.cmd.CmdForceSkill
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 MageMonkeyStudio
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
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
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import studio.magemonkey.codex.mccore.commands.ConfigurableCommand;
import studio.magemonkey.codex.mccore.commands.IFunction;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.api.player.PlayerSkill;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Command to forcefully modify a skill's level
 */
public class CmdForceSkill implements IFunction, TabCompleter {
    private static final String NOT_PLAYER   = "not-player";
    private static final String NOT_SKILL    = "not-skill";
    private static final String NOT_FUNCTION = "not-function";
    private static final String UPGRADED     = "skill-upped";
    private static final String DOWNGRADED   = "skill-downed";
    private static final String RESET        = "skill-reset";

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
        if (args.length < 3) {
            command.displayHelp(sender);
        }

        // Switch accounts if valid number
        else {
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);

            if (player == null) {
                command.sendMessage(sender, NOT_PLAYER, "&4That is not a valid player name");
                return;
            }

            PlayerData    playerData = Fabled.getData(player);
            StringBuilder skillName  = new StringBuilder(args[2]);
            for (int i = 3; i < args.length; i++) skillName.append(" " + args[i]);
            PlayerSkill skill = playerData.getSkill(skillName.toString());

            if (skill == null) {
                command.sendMessage(sender, NOT_SKILL, "&4The player does not have access to that skill");
                return;
            }

            if (args[1].equals("up")) {
                playerData.forceUpSkill(skill);
                command.sendMessage(sender,
                        UPGRADED,
                        "&6" + skill.getData().getName() + "&2 was upgraded for &6" + player.getName());
            } else if (args[1].equals("down")) {
                playerData.forceDownSkill(skill);
                command.sendMessage(sender,
                        DOWNGRADED,
                        "&6" + skill.getData().getName() + "&2 was downgraded for &6" + player.getName());
            } else if (args[1].equals("reset")) {
                playerData.refundSkill(skill);
                command.sendMessage(sender,
                        RESET,
                        "&6" + skill.getData().getName() + "&2 was reset for &6" + player.getName());
            } else
                command.sendMessage(sender, NOT_FUNCTION, "&4That is not a valid function. Use up, down, or reset.");
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
        } else if (args.length == 2) {
            return ConfigurableCommand.getTabCompletions(List.of("up", "down", "reset"), new String[]{args[1]});
        } else if (args.length > 2) {
            PlayerData playerData = Fabled.getData(Bukkit.getPlayer(args[0]));
            if (playerData == null) return null;
            return ConfigurableCommand.getTabCompletions(playerData.getSkills().stream()
                    .map(playerSkill -> playerSkill.getData().getName())
                    .collect(Collectors.toList()), Arrays.copyOfRange(args, 2, args.length));
        }
        return null;
    }
}
