/**
 * SkillAPI
 * com.sucy.skill.api.util.ActionBar
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
package com.sucy.skill.api.util;

import com.sucy.skill.log.Logger;
import com.sucy.skill.util.Version;
import mc.promcteam.engine.mccore.util.TextFormatter;
import mc.promcteam.engine.mccore.util.VersionManager;
import mc.promcteam.engine.utils.Reflex;
import mc.promcteam.engine.utils.reflection.ReflectionUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;

/**
 * Handles sending text to players using the action bar.
 */
public class ActionBar {
    private static Class<?> chatPacket;
    private static Class<?> chatText;
    private static Class<?> chatBase;


    private static Constructor<?> constructPacket;
    private static Constructor<?> constructText;

    private static Object messageType = (byte) 2;

    private static boolean initialized = false;
    private static boolean supported   = false;

    private static void initialize() {
        initialized = true;
        try {
            chatPacket = Version.MINOR_VERSION >= 17
                    ? Reflex.getClass("net.minecraft.network.protocol.game.PacketPlayOutChat")
                    : Reflex.getNMSClass("PacketPlayOutChat");
            chatBase = Version.MINOR_VERSION >= 17 ? Reflex.getClass("net.minecraft.network.chat.IChatBaseComponent")
                    : Reflex.getNMSClass("IChatBaseComponent");
            chatText = Version.MINOR_VERSION >= 17 ? Reflex.getClass("net.minecraft.network.chat.ChatComponentText")
                    : Reflex.getNMSClass("ChatComponentText");
            if (VersionManager.isVersionAtLeast(11200)) {
                Class<?> chatMessageType = Version.MINOR_VERSION >= 17
                        ? Reflex.getClass("net.minecraft.network.chat.ChatMessageType")
                        : Reflex.getNMSClass("ChatMessageType");
                messageType = chatMessageType.getMethod("a", byte.class).invoke(null, messageType);
                constructPacket = chatPacket.getConstructor(chatBase, chatMessageType);
            } else {
                constructPacket = chatPacket.getConstructor(chatBase, byte.class);
            }
            constructText = chatText.getConstructor(String.class);

            supported = true;
        } catch (Exception ex) {
            try {
                Player.Spigot.class.getMethod("sendMessage", ChatMessageType.class, BaseComponent.class);
                supported = true;
            } catch (NoSuchMethodException e) {
                ex.printStackTrace();
                Logger.invalid("Failed to setup Action Bar utility - not supported on pre-1.8 servers");
            }
        }
    }

    /**
     * Checks whether the action bar is supported
     *
     * @return true if supported, false otherwise
     */
    public static boolean isSupported() {
        if (!initialized) initialize();
        return supported;
    }

    /**
     * Shows an action bar message to the given player
     *
     * @param player  player to show the message to
     * @param message message to show
     */
    public static void show(Player player, String message) {
        if (!initialized) initialize();
        if (!isSupported()) return;

        try {
            Object text = constructText.newInstance(TextFormatter.colorString(message));
            Object data = constructPacket.newInstance(text, messageType);
            ReflectionUtil.sendPacket(player, data);
        } catch (Exception ex) {
            Logger.bug("Failed to apply Action Bar");
            ex.printStackTrace();
            // Failed to send
            supported = false;
        }
    }
}
