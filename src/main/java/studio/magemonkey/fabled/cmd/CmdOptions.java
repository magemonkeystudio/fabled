/**
 * Fabled
 * studio.magemonkey.fabled.cmd.CmdOptions
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

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import studio.magemonkey.codex.mccore.commands.ConfigurableCommand;
import studio.magemonkey.codex.mccore.commands.IFunction;
import studio.magemonkey.codex.mccore.util.TextFormatter;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.classes.FabledClass;
import studio.magemonkey.fabled.api.player.PlayerClass;
import studio.magemonkey.fabled.api.player.PlayerData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A command that displays the list of available profess options
 */
public class CmdOptions implements IFunction {
    private static final String TITLE      = "title";
    private static final String CATEGORY   = "category";
    private static final String OPTION     = "option";
    private static final String SEPARATOR  = "separator";
    private static final String END        = "end";
    private static final String CANNOT_USE = "cannot-use";
    private static final String NO_OPTIONS = "no-options";
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
    public void execute(ConfigurableCommand cmd, Plugin plugin, CommandSender sender, String[] args) {
        // Disabled world
        if (sender instanceof Player && !Fabled.getSettings().isWorldEnabled(((Player) sender).getWorld()))
            cmd.sendMessage(sender, DISABLED, "&4You cannot use this command in this world");

            // Only players have profession options
        else if (sender instanceof Player) {
            cmd.sendMessage(sender,
                    TITLE,
                    ChatColor.DARK_GRAY + "--" + ChatColor.DARK_GREEN + " Profess Options " + ChatColor.DARK_GRAY
                            + "-----------");
            PlayerData data = Fabled.getData((Player) sender);
            String categoryTemplate =
                    cmd.getMessage(CATEGORY, ChatColor.GOLD + "{category}" + ChatColor.GRAY + ": ");
            String optionTemplate = cmd.getMessage(OPTION, ChatColor.LIGHT_PURPLE + "{option}" + ChatColor.GRAY);
            String separator =
                    cmd.getMessage(SEPARATOR, ChatColor.DARK_GRAY + "----------------------------");
            String  none  = cmd.getMessage(NO_OPTIONS, ChatColor.GRAY + "None");
            boolean first = true;

            if (data != null) {
                Map<String, List<String>> groupList = new HashMap<>();
                for (String group : Fabled.getGroups()) {
                    PlayerClass c = data.getClass(group);

                    // Get the options list
                    List<FabledClass> options = c != null
                            ? c.getData().getOptions()
                            : Fabled.getBaseClasses(group);


                    // Compose the message
                    for (FabledClass option : options) {
                        String gp = option.getGroup().toLowerCase();
                        if (!groupList.containsKey(gp)) groupList.put(gp, new ArrayList<>());

                        String entry = optionTemplate.replace("{option}", option.getName());
                        if (!groupList.get(gp).contains(entry)) groupList.get(gp).add(entry);
                    }
                }

                //Send the entire message :D
                for (Map.Entry<String, List<String>> entry : groupList.entrySet()) {
                    if (first) first = false;
                    else sender.sendMessage(separator);

                    String        group   = entry.getKey();
                    List<String>  options = entry.getValue();
                    StringBuilder list    = new StringBuilder();
                    list.append(categoryTemplate.replace("{category}", TextFormatter.format(group)));
                    if (options.size() > 0)
                        list.append(String.join(", ", options));
                    else
                        list.append(none);

                    sender.sendMessage(list.toString());
                }
            }
            cmd.sendMessage(sender, END, ChatColor.DARK_GRAY + "----------------------------");
        }
        // Console doesn't have profession options
        else cmd.sendMessage(sender, CANNOT_USE, ChatColor.RED + "This cannot be used by the console");
    }
}
