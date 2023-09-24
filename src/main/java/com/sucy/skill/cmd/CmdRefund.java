/**
 * SkillAPI
 * com.sucy.skill.cmd.CmdRefund
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2016 Steven Sucy
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
package com.sucy.skill.cmd;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.event.PlayerRefundAttributeEvent;
import com.sucy.skill.api.player.PlayerData;
import mc.promcteam.engine.mccore.commands.ConfigurableCommand;
import mc.promcteam.engine.mccore.commands.IFunction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Command for refunding invested skill points
 */
public class CmdRefund implements IFunction {
    private static final String CANNOT_USE         = "cannot-use";
    private static final String NO_CLASS           = "no-class";
    private static final String REFUNDED           = "refunded";
    private static final String REFUNDED_OTHER     = "refunded-other";
    private static final String REFUNDED_OTHER_ALL = "refunded-other-all";

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
        // Only players have skills
        if (sender instanceof Player) {
            PlayerData player = SkillAPI.getPlayerData((Player) sender);

            // Reset all skills
            if (args.length < 1) {
                // Player must have a class
                if (!player.hasClass()) {
                    cmd.sendMessage(sender, NO_CLASS, "&4You have not professed as any class yet");
                }
                player.refundSkills();
                cmd.sendMessage(sender, REFUNDED, "&2Your skill points have been refunded");
            } else if (args.length > 2 && args[1].equals("attribute")) {

                OfflinePlayer player1    = Bukkit.getOfflinePlayer(args[0]);
                PlayerData    playerData = SkillAPI.getPlayerData(player1);

                int current = playerData.getInvestedAttribute(args[2].toLowerCase());

                if (current > 0) {
                    PlayerRefundAttributeEvent event =
                            new PlayerRefundAttributeEvent(playerData, args[2].toLowerCase());
                    Bukkit.getPluginManager().callEvent(event);
                    if (event.isCancelled()) {
                        return;
                    }

                    playerData.attributes.put(args[2].toLowerCase(), current - Integer.parseInt(args[3]));
                    if (current - Integer.parseInt(args[3]) <= 0) {
                        playerData.attribPoints += current;
                        playerData.attributes.remove(args[2].toLowerCase());
                    } else {
                        playerData.attribPoints += Integer.parseInt(args[3]);
                    }
                    playerData.updatePlayerStat(playerData.getPlayer());
                    cmd.sendMessage(sender,
                            REFUNDED_OTHER,
                            "&6" + args[3] + " " + args[2] + " &2attribute points have been refunded for &6" + args[0]);
                }
            } else if (args.length == 2 && args[1].equals("attribute")) {

                OfflinePlayer player1    = Bukkit.getOfflinePlayer(args[0]);
                PlayerData    playerData = SkillAPI.getPlayerData(player1);

                playerData.refundAttributes();
                cmd.sendMessage(sender, REFUNDED_OTHER_ALL, "&2Refunded &6" + args[0] + "&2's attribute points");
            }
        }
        // Console doesn't have profession options
        else {
            cmd.sendMessage(sender, CANNOT_USE, ChatColor.RED + "This cannot be used by the console");
        }
    }
}
