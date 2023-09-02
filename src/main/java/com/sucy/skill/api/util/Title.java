package com.sucy.skill.api.util;

import mc.promcteam.engine.utils.Reflex;
import mc.promcteam.engine.utils.reflection.ReflectionManager;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;


public class Title {
    private static Constructor<?> timesConstructor;
    private static Constructor<?> contentConstructor;

    private static Class<?> packetTitle;

    private static Method serialize;

    private static Object timesType;
    private static Object titleType;
    private static Object subtitleType;

    private static void loadClasses() throws Exception { // This is not used...
        packetTitle = Reflex.getNMSClass("PacketPlayOutTitle");
        Class<?> chatBaseComponent = ReflectionManager.MINOR_VERSION >= 17
                ? Reflex.getClass("net.minecraft.network.chat.IChatBaseComponent")
                : Reflex.getNMSClass("IChatBaseComponent");
        serialize = chatBaseComponent.getDeclaredClasses()[0].getDeclaredMethod("a", String.class);

        Class<?> titleSerializer = packetTitle.getDeclaredClasses()[0];
        timesType = titleSerializer.getField("TIMES").get(null);
        titleType = titleSerializer.getField("TITLE").get(null);
        subtitleType = titleSerializer.getField("SUBTITLE").get(null);

        timesConstructor =
                packetTitle.getConstructor(titleSerializer, chatBaseComponent, int.class, int.class, int.class);
        contentConstructor = packetTitle.getConstructor(titleSerializer, chatBaseComponent);
    }

    public static void send(Player player, String title, String subtitle, int fadeIn, int duration, int fadeOut) {
        player.sendTitle(title, subtitle, fadeIn, duration, fadeOut);
//        try
//        {
//            if (packetTitle == null)
//                loadClasses();
//            if (title != null)
//                send(player, title, titleType, fadeIn, duration, fadeOut);
//            if (subtitle != null)
//                send(player, subtitle, subtitleType, fadeIn, duration, fadeOut);
//        }
//        catch (Exception ex) {
//            ex.printStackTrace();
//        }
    }

    private static void send(Player player, String text, Object type, int fadeIn, int duration, int fadeOut) throws
            Exception {
        Object chatText = serialize.invoke(null, "{\"text\":\"" + text + "\"}");

        Object packet = timesConstructor.newInstance(timesType, chatText, fadeIn, duration, fadeOut);
        ReflectionManager.getReflectionUtil().sendPacket(player, packet);

        packet = contentConstructor.newInstance(type, chatText);
        ReflectionManager.getReflectionUtil().sendPacket(player, packet);
    }
}