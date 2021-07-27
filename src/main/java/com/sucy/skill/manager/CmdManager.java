/**
 * SkillAPI
 * com.sucy.skill.manager.CmdManager
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2014 Steven Sucy
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
package com.sucy.skill.manager;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.cmd.*;
import com.sucy.skill.data.Permissions;
import mc.promcteam.engine.mccore.commands.CommandManager;
import mc.promcteam.engine.mccore.commands.ConfigurableCommand;
import mc.promcteam.engine.mccore.commands.SenderType;

/**
 * Sets up commands for the plugin
 */
public class CmdManager {
    public static ConfigurableCommand PROFESS_COMMAND;

    private SkillAPI api;

    /**
     * Initializes a new command manager. This is handled by the API and
     * shouldn't be used by other plugins.
     *
     * @param api SkillAPI reference
     */
    public CmdManager(SkillAPI api) {
        this.api = api;
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
     * Unregisters all commands for SkillAPI from the server
     */
    public void clear() {
        CommandManager.unregisterCommands(api);
    }
}
