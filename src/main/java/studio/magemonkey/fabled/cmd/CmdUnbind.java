/**
 * Fabled
 * studio.magemonkey.fabled.cmd.CmdUnbind
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

import studio.magemonkey.codex.mccore.commands.ConfigurableCommand;
import studio.magemonkey.codex.mccore.commands.IFunction;
import studio.magemonkey.codex.mccore.util.TextFormatter;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.api.player.PlayerSkill;
import studio.magemonkey.fabled.language.RPGFilter;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

/**
 * Command to bind a skill to an item
 */
public class CmdUnbind implements IFunction {
    private static final String NOT_PLAYER  = "not-player";
    private static final String NOT_BOUND   = "not-bound";
    private static final String NO_ITEM     = "no-item";
    private static final String SKILL_BOUND = "skill-unbound";
    private static final String DISABLED    = "world-disabled";

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
        } else {
            ItemStack item = ((Player) sender).getInventory().getItemInMainHand();
            if (item == null || item.getType() == Material.AIR) {
                command.sendMessage(sender, NO_ITEM, "&4You are not holding an item");
                return;
            }

            PlayerData player = Fabled.getPlayerData((Player) sender);

            if (!player.isBound(item.getType())) {
                command.sendMessage(sender, NOT_BOUND, "&4There are no skills bound to the held item");
            } else {
                PlayerSkill skill = player.getBoundSkill(item.getType());
                player.clearBind(item.getType());
                command.sendMessage(sender,
                        SKILL_BOUND,
                        "&6{skill} &2has been unbound from &6{item}",
                        RPGFilter.SKILL.setReplacement(skill.getData().getName()),
                        RPGFilter.ITEM.setReplacement(TextFormatter.format(item.getType().name())));
            }
        }
    }
}