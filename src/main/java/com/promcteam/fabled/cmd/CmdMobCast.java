/**
 * Fabled
 * com.promcteam.fabled.cmd.CmdMobCast
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

import com.promcteam.fabled.Fabled;
import com.promcteam.fabled.api.skills.Skill;
import com.promcteam.fabled.api.skills.SkillShot;
import com.promcteam.codex.mccore.commands.CommandManager;
import com.promcteam.codex.mccore.commands.ConfigurableCommand;
import com.promcteam.codex.mccore.commands.IFunction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A command that makes a player cast a skill regardless
 * of them owning it or not and also ignores cooldown/mana costs.
 */
public class CmdMobCast implements IFunction, TabCompleter {
    private static final Pattern INTEGER = Pattern.compile("-?[0-9]+");

    private static final String NOT_ENTITY    = "not-entity";
    private static final String WRONG_SKILL   = "wrong-skill";
    private static final String INVALID_SKILL = "invalid-skill";

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
        if (args.length < 2) {
            CommandManager.displayUsage(cmd, sender);
        } else {
            Entity ent = Bukkit.getEntity(UUID.fromString(args[0]));
            if (ent == null || !(ent instanceof LivingEntity)) {
                cmd.sendMessage(sender, NOT_ENTITY, ChatColor.RED + "That is not a valid entity");
                return;
            }

            LivingEntity entity = (LivingEntity) ent;

            String name  = args[1];
            int    level = 1;
            for (int i = 2; i < args.length; i++) {
                if (i == args.length - 1 && Fabled.getSkill(name) != null && INTEGER.matcher(args[i]).matches()) {
                    level = Integer.parseInt(args[i]);
                } else name += ' ' + args[i];
            }

            Skill skill = Fabled.getSkill(name);

            // Invalid class
            if (skill == null) {
                cmd.sendMessage(sender, INVALID_SKILL, ChatColor.RED + "That is not a valid skill");
            }

            // Castable skill
            else if (skill instanceof SkillShot) {
                ((SkillShot) skill).cast(entity, level, true);
            }

            // Not castable
            else {
                cmd.sendMessage(sender,
                        WRONG_SKILL,
                        ChatColor.RED + "Skills must be skill shot skills or dynamic skills to be cast this way.");
            }
        }
    }

    @Override
    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender commandSender,
                                      @NotNull Command command,
                                      @NotNull String s,
                                      @NotNull String[] args) {
        if (args.length == 1 && commandSender instanceof Player) {
            Player         player         = (Player) commandSender;
            Location       location       = player.getEyeLocation();
            RayTraceResult rayTraceResult = player.getWorld()
                    .rayTraceEntities(location,
                            location.getDirection(),
                            5,
                            0,
                            entity -> entity != player && player.canSee(entity));
            if (rayTraceResult == null) return null;
            Entity entity = rayTraceResult.getHitEntity();
            if (entity == null) return null;
            return ConfigurableCommand.getTabCompletions(List.of(entity.getUniqueId().toString()),
                    new String[]{args[0]});
        } else if (args.length > 1) {
            return ConfigurableCommand.getTabCompletions(Fabled.getSkills().values().stream()
                    .filter(skill -> skill instanceof SkillShot)
                    .map(Skill::getKey)
                    .collect(Collectors.toList()), Arrays.copyOfRange(args, 1, args.length));
        }
        return null;
    }
}
