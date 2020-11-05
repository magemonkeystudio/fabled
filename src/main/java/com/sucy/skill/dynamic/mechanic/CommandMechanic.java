/**
 * SkillAPI
 * com.sucy.skill.dynamic.mechanic.CommandMechanic
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sucy.skill.dynamic.mechanic;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

/**
 * Executes a command for each target
 */
public class CommandMechanic extends MechanicComponent {
    private static final String COMMAND = "command";
    private static final String TYPE    = "type";

    @Override
    public String getKey() {
        return "command";
    }

    /**
     * Executes the component
     *
     * @param caster  caster of the skill
     * @param level   level of the skill
     * @param targets targets to apply to
     *
     * @return true if applied to something, false otherwise
     */
    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets) {
        if (targets.size() == 0 || !settings.has(COMMAND)) {
            return false;
        }

        String command = settings.getString(COMMAND);
        String type = settings.getString(TYPE).toLowerCase();
        boolean worked = false;

        switch (type) {
            case "op":
                for (LivingEntity t : targets) {
                    if (t instanceof Player) {
                        worked = true;
                        String filteredCommand = filter(caster, t, command);
                        Player p = (Player) t;
                        boolean op = p.isOp();
                        p.setOp(true);
                        Bukkit.getServer().dispatchCommand(p, filteredCommand);
                        p.setOp(op);
                    }
                }
                break;
            case "console":
                for (LivingEntity t : targets) {
                    worked = true;
                    String filteredCommand = filter(caster, t, command);
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), filteredCommand);
                }
                break;
            case "silent console":
                for (LivingEntity t : targets) {
                    worked = true;
                    String filteredCommand = filter(caster, t, command);
                    Bukkit.getServer().dispatchCommand(new SilentConsoleCommandSender(), filteredCommand);
                }
                break;
        }
        return worked;
    }

    private static class SilentConsoleCommandSender implements ConsoleCommandSender {

        @Override
        public void sendRawMessage(String paramString) {}

        @Override
        public boolean isConversing() {
            return false;
        }

        @Override
        public boolean beginConversation(Conversation paramConversation) {
            return false;
        }

        @Override
        public void acceptConversationInput(String paramString) {}

        @Override
        public void abandonConversation(Conversation paramConversation,
                                        ConversationAbandonedEvent paramConversationAbandonedEvent) {}

        @Override
        public void abandonConversation(Conversation paramConversation) {}

        @Override
        public void setOp(boolean paramBoolean) {}

        @Override
        public boolean isOp() { return true; }

        @Override
        public void removeAttachment(PermissionAttachment paramPermissionAttachment) {}

        @Override
        public void recalculatePermissions() {}

        @Override
        public boolean isPermissionSet(Permission paramPermission) {
            return Bukkit.getServer().getConsoleSender().isPermissionSet(paramPermission);
        }

        @Override
        public boolean isPermissionSet(String paramString) {
            return Bukkit.getServer().getConsoleSender().isPermissionSet(paramString);
        }

        @Override
        public boolean hasPermission(Permission paramPermission) {
            return Bukkit.getServer().getConsoleSender().hasPermission(paramPermission);
        }

        @Override
        public boolean hasPermission(String paramString) {
            return Bukkit.getServer().getConsoleSender().hasPermission(paramString);
        }

        @Override
        public Set<PermissionAttachmentInfo> getEffectivePermissions() { return null; }

        @Override
        public PermissionAttachment addAttachment(Plugin paramPlugin, String paramString, boolean paramBoolean, int paramInt) {
            return null;
        }

        @Override
        public PermissionAttachment addAttachment(Plugin paramPlugin, String paramString, boolean paramBoolean) {
            return null;
        }

        @Override
        public PermissionAttachment addAttachment(Plugin paramPlugin, int paramInt) { return null; }

        @Override
        public PermissionAttachment addAttachment(Plugin paramPlugin) { return null; }

        @Override
        public Spigot spigot() { return Bukkit.getServer().getConsoleSender().spigot(); }

        @Override
        public void sendMessage(String[] paramArrayOfString) {}

        @Override
        public void sendMessage(String paramString) {}

        @Override
        public Server getServer() { return Bukkit.getServer(); }

        @Override
        public String getName() { return "SkillApi SilentConsoleCommandServer"; }
    }

}
