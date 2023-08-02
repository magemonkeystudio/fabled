package com.sucy.skill.cmd;

import com.sucy.skill.SkillAPI;
import mc.promcteam.engine.mccore.commands.ConfigurableCommand;
import mc.promcteam.engine.mccore.commands.IFunction;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A command that displays the list of loaded groups, classes, skills and attributes
 */
public class CmdLoaded implements IFunction {

    public static final String GROUP_HEAD = "group-head";
    public static final String CLASS_HEAD = "class-head";
    public static final String SKILL_HEAD = "skill-head";
    public static final String ATTR_HEAD = "attr-head";
    private static final String END        = "end";
    public static final String NO_ARGUMENT = "no-argument";
    public static final String INVALID_ARGUMENT = "invalid-argument";

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
        if (args.length<1) {
            cmd.sendMessage(sender, NO_ARGUMENT, "&4Missing argument! <class|skill>");
            return;
        }
        AtomicInteger count = new AtomicInteger(1);
        switch (args[0]) {


            case "group": sendList(cmd, sender, GROUP_HEAD,"-------Loaded %d group(s)-------", SkillAPI.getGroups()); break;
            case "class": sendList(cmd, sender, CLASS_HEAD,"-------Loaded %d class(es)-------", SkillAPI.getClasses().keySet()); break;
            case "skill": sendList(cmd, sender, SKILL_HEAD,"-------Loaded %d skill(s)-------", SkillAPI.getSkills().keySet()); break;
            case "attr":
            case "attribute": sendList(cmd, sender, ATTR_HEAD,"-------Loaded %d attribute(s)-------", SkillAPI.getAttributeManager().getKeys()); break;
            default:
                cmd.sendMessage(sender, INVALID_ARGUMENT, "&4Invalid argument! <group|class|skill|attr>");
        }
    }
    private static void sendList(ConfigurableCommand cmd, CommandSender sender,String key, String head, Collection<?> list){
        AtomicInteger count = new AtomicInteger(1);
        cmd.sendMessage(sender, key, String.format(head, list.size()));
        list.forEach(entry -> sender.sendMessage(String.format("%d: %s", count.getAndIncrement(), entry)));
        cmd.sendMessage(sender, END, "-------------------------------");
    }
}
