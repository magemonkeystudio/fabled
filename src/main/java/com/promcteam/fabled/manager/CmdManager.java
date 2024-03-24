/**
 * Fabled
 * com.promcteam.fabled.manager.CmdManager
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
package com.promcteam.fabled.manager;

import com.promcteam.fabled.Fabled;
import com.promcteam.fabled.cmd.*;
import com.promcteam.fabled.data.Permissions;
import com.promcteam.codex.mccore.commands.CommandManager;
import com.promcteam.codex.mccore.commands.ConfigurableCommand;
import com.promcteam.codex.mccore.commands.SenderType;

/**
 * Sets up commands for the plugin
 */
public class CmdManager {
    public static ConfigurableCommand PROFESS_COMMAND;

    private final Fabled api;

    /**
     * Initializes a new command manager. This is handled by the API and
     * shouldn't be used by other plugins.
     *
     * @param api Fabled reference
     */
    public CmdManager(Fabled api) {
        this.api = api;
        this.initialize();
    }

    public static String join(String[] args, int start) {
        return join(args, start, args.length - 1);
    }

    public static String join(String[] args, int start, int end) {
        final StringBuilder builder = new StringBuilder(args[start]);
        for (int i = start + 1; i <= end; i++) builder.append(' ').append(args[i]);
        return builder.toString();
    }

    /**
     * Initializes commands with MCCore's CommandManager
     */
    public void initialize() {
        ConfigurableCommand root = new ConfigurableCommand(api, "class", SenderType.ANYONE);
        root.addSubCommands(
                new ConfigurableCommand(api,
                        "bind",
                        SenderType.PLAYER_ONLY,
                        new CmdBind(),
                        "Binds a skill",
                        "<skill>",
                        Permissions.BASIC),
                new ConfigurableCommand(api,
                        "cast",
                        SenderType.PLAYER_ONLY,
                        new CmdCast(),
                        "Casts a skill",
                        "<skill>",
                        Permissions.BASIC),
                new ConfigurableCommand(api,
                        "changeclass",
                        SenderType.ANYONE,
                        new CmdChangeClass(),
                        "Swaps classes",
                        "<player> <group> <class>",
                        Permissions.FORCE),
                new ConfigurableCommand(api,
                        "clearbind",
                        SenderType.PLAYER_ONLY,
                        new CmdClearBinds(),
                        "Clears skill binds",
                        "",
                        Permissions.BASIC),
                new ConfigurableCommand(api,
                        "customize",
                        SenderType.PLAYER_ONLY,
                        new CmdCustomize(),
                        "Opens GUI editor",
                        "",
                        Permissions.GUI),
                new ConfigurableCommand(api,
                        "exp",
                        SenderType.ANYONE,
                        new CmdExp(),
                        "Gives players exp",
                        "[player] <amount> [group]",
                        Permissions.LVL),
                new ConfigurableCommand(api,
                        "info",
                        SenderType.ANYONE,
                        new CmdInfo(),
                        "Shows class info",
                        "[player]",
                        Permissions.BASIC),
                new ConfigurableCommand(api,
                        "level",
                        SenderType.ANYONE,
                        new CmdLevel(),
                        "Gives players levels",
                        "[player] <amount> [group]",
                        Permissions.LVL),
                new ConfigurableCommand(api,
                        "list",
                        SenderType.ANYONE,
                        new CmdList(),
                        "Displays accounts",
                        "[player]",
                        Permissions.BASIC),
                new ConfigurableCommand(api,
                        "lore",
                        SenderType.PLAYER_ONLY,
                        new CmdLore(),
                        "Adds lore to item",
                        "<lore>",
                        Permissions.LORE),
                new ConfigurableCommand(api,
                        "mana",
                        SenderType.ANYONE,
                        new CmdMana(),
                        "Gives player mana",
                        "[player] <amount>",
                        Permissions.MANA),
                new ConfigurableCommand(api,
                        "options",
                        SenderType.PLAYER_ONLY,
                        new CmdOptions(),
                        "Views profess options",
                        "",
                        Permissions.BASIC),
                new ConfigurableCommand(api,
                        "points",
                        SenderType.ANYONE,
                        new CmdPoints(),
                        "Gives player points",
                        "[player] <amount>",
                        Permissions.POINTS),
                PROFESS_COMMAND = new ConfigurableCommand(api,
                        "profess",
                        SenderType.PLAYER_ONLY,
                        new CmdProfess(),
                        "Professes classes",
                        "<class>",
                        Permissions.BASIC),
                new ConfigurableCommand(api,
                        "reload",
                        SenderType.ANYONE,
                        new CmdReload(),
                        "Reloads the plugin",
                        "",
                        Permissions.RELOAD),
                new ConfigurableCommand(api,
                        "reset",
                        SenderType.PLAYER_ONLY,
                        new CmdReset(),
                        "Resets account data",
                        "",
                        Permissions.RESET),
                new ConfigurableCommand(api,
                        "refund",
                        SenderType.PLAYER_ONLY,
                        new CmdRefund(),
                        "Refound Skill/Attributes points",
                        "",
                        Permissions.REFUND),
                new ConfigurableCommand(api,
                        "skill",
                        SenderType.PLAYER_ONLY,
                        new CmdSkill(),
                        "Shows player skills",
                        "",
                        Permissions.BASIC),
                new ConfigurableCommand(api,
                        "unbind",
                        SenderType.PLAYER_ONLY,
                        new CmdUnbind(),
                        "Unbinds held item",
                        "",
                        Permissions.BASIC),
                new ConfigurableCommand(api,
                        "world",
                        SenderType.PLAYER_ONLY,
                        new CmdWorld(),
                        "Moves to world",
                        "<world>",
                        Permissions.WORLD),
                new ConfigurableCommand(api,
                        "help",
                        SenderType.ANYONE,
                        new CmdHelp(),
                        "Get help for the plugin",
                        "<page>",
                        Permissions.BASIC)
        );
        root.addSubCommands(
                new ConfigurableCommand(api,
                        "forceaccount",
                        SenderType.CONSOLE_ONLY,
                        new CmdForceAccount(),
                        "Changes player's account",
                        "<player> <accountId>",
                        Permissions.FORCE),
                new ConfigurableCommand(api,
                        "forceattr",
                        SenderType.CONSOLE_ONLY,
                        new CmdForceAttr(),
                        "Refunds/gives attributes",
                        "<player> [attr] [amount]",
                        Permissions.FORCE),
                new ConfigurableCommand(api,
                        "forcecast",
                        SenderType.CONSOLE_ONLY,
                        new CmdForceCast(),
                        "Player casts the skill",
                        "<player> <skill> [level]",
                        Permissions.FORCE),
                new ConfigurableCommand(api,
                        "forcepoints",
                        SenderType.ANYONE,
                        new CmdForcePoints(),
                        "Adds, subtracts or sets skill points",
                        "<add|set> [player] <amount> [group]",
                        Permissions.FORCE),
                new ConfigurableCommand(api,
                        "mobcast",
                        SenderType.CONSOLE_ONLY,
                        new CmdMobCast(),
                        "Mob casts the skill",
                        "<mob_uuid> <skill> [level]",
                        Permissions.FORCE),
                new ConfigurableCommand(api,
                        "forceprofess",
                        SenderType.CONSOLE_ONLY,
                        new CmdForceProfess(),
                        "Professes a player",
                        "<player> <class> [-s]",
                        Permissions.FORCE),
                new ConfigurableCommand(api,
                        "forcereset",
                        SenderType.CONSOLE_ONLY,
                        new CmdForceReset(),
                        "Resets player data",
                        "<player> [account]",
                        Permissions.FORCE),
                new ConfigurableCommand(api,
                        "forceskill",
                        SenderType.CONSOLE_ONLY,
                        new CmdForceSkill(),
                        "Modifies skill levels",
                        "<player> <up|down|reset> <skill>",
                        Permissions.FORCE)
        );
        if (Fabled.getSettings().isOnePerClass()) {
            root.addSubCommand(new ConfigurableCommand(api,
                    "switch",
                    SenderType.PLAYER_ONLY,
                    new CmdSwitch(),
                    "Changes class",
                    "<class>",
                    Permissions.BASIC));
        } else {
            root.addSubCommand(new ConfigurableCommand(api,
                    "acc",
                    SenderType.PLAYER_ONLY,
                    new CmdAccount(),
                    "Changes account",
                    "<accountId>",
                    Permissions.BASIC));
        }
        if (Fabled.getSettings().isUseSql()) {
            root.addSubCommand(new ConfigurableCommand(api,
                    "backup",
                    SenderType.ANYONE,
                    new CmdBackup(),
                    "Backs up SQL data",
                    "",
                    Permissions.BACKUP));
        }
        if (Fabled.getSettings().isSkillBarEnabled()) {
            root.addSubCommand(new ConfigurableCommand(api,
                    "bar",
                    SenderType.PLAYER_ONLY,
                    new CmdBar(),
                    "Toggles skill bar",
                    "",
                    Permissions.BASIC));
        }
        if (Fabled.getSettings().isCustomCombosAllowed()) {
            root.addSubCommand(new ConfigurableCommand(api,
                    "combo",
                    SenderType.PLAYER_ONLY,
                    new CmdCombo(),
                    "Sets skill combo",
                    "<skill> <combo>",
                    Permissions.BASIC));
        }
        if (Fabled.getSettings().isAttributesEnabled()) {
            root.addSubCommand(new ConfigurableCommand(api,
                    "ap",
                    SenderType.ANYONE,
                    new CmdAP(),
                    "Gives attrib points",
                    "[player] <amount>",
                    Permissions.ATTRIB));
            root.addSubCommand(new ConfigurableCommand(api,
                    "attr",
                    SenderType.PLAYER_ONLY,
                    new CmdAttribute(),
                    "Opens attribute menu",
                    "",
                    Permissions.BASIC));
        }
        CommandManager.registerCommand(root);
    }

    /**
     * Unregisters all commands for Fabled from the server
     */
    public void clear() {
        CommandManager.unregisterCommands(api);
    }
}
