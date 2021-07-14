package com.sucy.skill.packet;

import com.rit.sucy.reflect.Reflection;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.util.Version;
import io.netty.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * SkillAPI © 2018
 * com.sucy.skill.PacketInjector
 */
public class PacketInjector {
    private Field playerCon;
    private Field network;
    private Method handle;
    private Field k;
    private Field dropField;
    private SkillAPI skillAPI;

    /**
     * Sets up the injector, grabbing necessary reflection data
     */
    public PacketInjector(final SkillAPI skillAPI) {
        this.skillAPI = skillAPI;

        try {
            if (Version.MINOR_VERSION >= 17) {
                playerCon = Class.forName("net.minecraft.server.level.EntityPlayer")
                        .getField("b");

                Class<?> playerConnection = Class.forName("net.minecraft.server.network.PlayerConnection");
                network = playerConnection.getField("a");

                Class<?> networkManager = Class.forName("net.minecraft.network.NetworkManager");
                try {
                    k = networkManager.getField("k");
                } catch (Exception ex) {
                    k = networkManager.getDeclaredField("i");
                    k.setAccessible(true);
                }
            } else {
                String nms = Reflection.getNMSPackage();
                playerCon = Class.forName(nms + "EntityPlayer")
                        .getField("playerConnection");

                Class<?> playerConnection = Class.forName(nms + "PlayerConnection");
                network = playerConnection.getField("networkManager");

                Class<?> networkManager = Class.forName(nms + "NetworkManager");
                try {
                    k = networkManager.getField("channel");
                } catch (Exception ex) {
                    k = networkManager.getDeclaredField("i");
                    k.setAccessible(true);
                }
            }

            handle = Class.forName(Reflection.getCraftPackage() + "entity.CraftPlayer").getMethod("getHandle");
        } catch (Throwable t) {
            this.error();
            t.printStackTrace();
        }

        try {
            if (Version.MINOR_VERSION >= 17)
                dropField = Reflection.getClass("net.minecraft.network.protocol.game.PacketPlayInBlockDig").getDeclaredField("c");
            else
                dropField = Reflection.getNMSClass("PacketPlayInBlockDig").getDeclaredField("c");
            dropField.setAccessible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean isWorking() {
        return handle != null;
    }

    private void error() {
        skillAPI.getLogger().warning("Failed to set up packet listener - some click combos may not behave properly");
    }

    /**
     * Injects an interceptor to the player's network manager
     *
     * @param p player to add to
     */
    public void addPlayer(Player p) {
        if (handle == null) return;

        try {
            Channel ch = getChannel(p);
            if (ch.pipeline().get("PacketInjector") == null) {
                PacketHandler h = new PacketHandler(p, dropField);
                ch.pipeline().addBefore("packet_handler", "PacketInjector", h);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Removes an interruptor from a player's network manager
     *
     * @param p player to remove from
     */
    public void removePlayer(Player p) {
        if (handle == null) return;

        try {
            Channel ch = getChannel(p);
            if (ch.pipeline().get("PacketInjector") != null) {
                ch.pipeline().remove("PacketInjector");
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Gets the channel used by a player's network manager
     *
     * @return retrieved channel
     */
    private Channel getChannel(final Player player) throws IllegalAccessException, InvocationTargetException {
        return (Channel) k.get(network.get(playerCon.get(handle.invoke(player))));
    }
}
