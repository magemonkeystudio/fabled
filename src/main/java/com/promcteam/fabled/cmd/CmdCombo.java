/**
 * Fabled
 * com.promcteam.fabled.cmd.CmdCombo
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
import com.promcteam.fabled.Fabled;
import com.promcteam.fabled.api.player.PlayerData;
import com.promcteam.fabled.api.player.PlayerSkill;
import com.promcteam.fabled.api.skills.SkillShot;
import com.promcteam.fabled.data.Click;
import com.promcteam.fabled.language.RPGFilter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Command to bind a skill to an item
 */
public class CmdCombo implements IFunction, TabCompleter {
    private static final String NOT_PLAYER   = "not-player";
    private static final String NOT_SKILL    = "not-skill";
    private static final String NOT_CASTABLE = "not-unlocked";
    private static final String NOT_CLICK    = "not-click";
    private static final String NOT_COMBO    = "not-combo";
    private static final String COMBO_SET    = "skill-bound";
    private static final String DISABLED     = "world-disabled";

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
        if (!(sender instanceof Player)) {
            command.sendMessage(sender, NOT_PLAYER, "&4Only players can use this command");
        }

        // Disabled world
        else if (!Fabled.getSettings().isWorldEnabled(((Player) sender).getWorld())) {
            command.sendMessage(sender, DISABLED, "&4You cannot use this command in this world");
        } else if (args.length >= Fabled.getComboManager().getComboSize() + 1) {
            PlayerData player = Fabled.getPlayerData((Player) sender);

            String name      = args[0];
            int    comboSize = Fabled.getComboManager().getComboSize();
            for (int i = 1; i < args.length - comboSize; i++) {
                name += ' ' + args[i];
            }
            PlayerSkill skill = player.getSkill(name);

            if (skill == null) {
                command.sendMessage(sender, NOT_SKILL, "&4You do not have that skill");
            } else if (!skill.getData().canCast()) {
                command.sendMessage(sender, NOT_CASTABLE, "&4That skill cannot be cast");
            } else {

                Click[] clicks = new Click[comboSize];
                for (int i = args.length - comboSize; i < args.length; i++) {
                    Click click = Click.getByName(args[i]);
                    if (click == null) {
                        command.sendMessage(sender,
                                NOT_CLICK,
                                "&6{name} &4is not a valid click type. Use Left, Right, or Shift instead",
                                RPGFilter.NAME);
                        return;
                    }
                    clicks[i - args.length + comboSize] = click;
                }
                int id = Fabled.getComboManager().convertCombo(clicks);
                if (player.getComboData().setSkill(skill.getData(), id)) {
                    if (Fabled.getSettings().isSkillBarEnabled() && player.getSkillBar().isSetup()) {
                        player.getSkillBar().update(player.getPlayer());
                    }
                    command.sendMessage(sender,
                            COMBO_SET,
                            "&2The combo for &6{skill} &2has been updated",
                            RPGFilter.SKILL.setReplacement(skill.getData().getName()));
                } else {
                    command.sendMessage(sender, NOT_COMBO, "&4That combo cannot be used");
                }
            }
        } else {
            CommandManager.displayUsage(command, sender);
        }
    }

    @Override
    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender commandSender,
                                      @NotNull Command command,
                                      @NotNull String s,
                                      @NotNull String[] args) {
        if (!(commandSender instanceof Player)) return null;
        Player player = (Player) commandSender;

        // Tab-complete skill until nothing matches
        List<String> tabCompletions =
                ConfigurableCommand.getTabCompletions(Fabled.getPlayerData(player).getSkills().stream()
                        .filter(playerSkill -> playerSkill.getData() instanceof SkillShot)
                        .map(playerSkill -> playerSkill.getData().getKey())
                        .collect(Collectors.toList()), args);
        if (!tabCompletions.isEmpty() || args.length < 1) return tabCompletions;

        // Then tab-complete skill until the correct amount of valid keys is there
        int comboSize = Fabled.getComboManager().getComboSize();
        int diff      = Click.getByName(args[args.length - 1]) == null ? 1 : 0;
        for (int i = 1 + diff; i <= comboSize + diff; i++) {
            if (args.length < i || Click.getByName(args[args.length - i]) == null) {
                // Prompt another Click
                return ConfigurableCommand.getTabCompletions(Arrays.stream(Click.values())
                        .map(click -> click.name().toLowerCase())
                        .collect(Collectors.toList()), new String[]{args[args.length - 1]});
            }
        }
        return null;
    }
}