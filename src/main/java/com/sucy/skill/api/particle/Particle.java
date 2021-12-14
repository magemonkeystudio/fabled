/**
 * SkillAPI
 * com.sucy.skill.api.particle.Particle
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
package com.sucy.skill.api.particle;

import com.sucy.skill.SkillAPI;
import mc.promcteam.engine.mccore.util.VersionManager;
import mc.promcteam.engine.utils.reflection.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Simplified particle utility compared to MCCore's
 */
public class Particle {
    private static Constructor<?> packet;

    private static Method toNms;
//    private static Method getHandle;
//    private static Method sendPacket;

//    private static Field connection;

    private static final HashMap<String, Object> particleTypes = new HashMap<>();

    /**
     * Initializes the SkillAPI particle utility
     */
    public static void init() {
        try {
            String version = Bukkit.getServer().getClass().getPackage().getName().substring(23);
            String nms     = "net.minecraft.server." + version + '.';
            String craft   = "org.bukkit.craftbukkit." + version + '.';
//            getHandle = Class.forName(craft + "entity.CraftPlayer").getMethod("getHandle");
//            connection = Class.forName(nms + "EntityPlayer").getDeclaredField("playerConnection");
//            sendPacket = Class.forName(nms + "PlayerConnection")
//                    .getDeclaredMethod("sendPacket", Class.forName(nms + "Packet"));

            Class<?> packetClass;
            // 1.13+ Servers
            Class<?> particleEnum;
            if (VersionManager.isVersionAtLeast(VersionManager.V1_17)) {
                Class<?> craftParticle = Class.forName(craft + "CraftParticle");
                toNms = craftParticle.getDeclaredMethod("toNMS", org.bukkit.Particle.class, Object.class);
                particleEnum = Class.forName("net.minecraft.core.particles.ParticleParam");
                packetClass = Class.forName("net.minecraft.network.game.PacketPlayOutWorldParticles");
                packet = packetClass.getConstructor(
                        particleEnum,
                        boolean.class,
                        float.class,
                        float.class,
                        float.class,
                        float.class,
                        float.class,
                        float.class,
                        float.class,
                        int.class);
            } else if (VersionManager.isVersionAtLeast(11300)) {
                Class<?> craftParticle = Class.forName(craft + "CraftParticle");
                toNms = craftParticle.getDeclaredMethod("toNMS", org.bukkit.Particle.class, Object.class);
                particleEnum = Class.forName(nms + "ParticleParam");
                packetClass = Class.forName(nms + "PacketPlayOutWorldParticles");
                packet = packetClass.getConstructor(
                        particleEnum,
                        boolean.class,
                        float.class,
                        float.class,
                        float.class,
                        float.class,
                        float.class,
                        float.class,
                        float.class,
                        int.class);
            }

            // 1.8+ servers
            else if (VersionManager.isVersionAtLeast(VersionManager.V1_8_0)) {
                particleEnum = Class.forName(nms + "EnumParticle");
                for (Object value : particleEnum.getEnumConstants()) {
                    particleTypes.put(value.toString(), value);
                }
                packetClass = Class.forName(nms + "PacketPlayOutWorldParticles");
                packet = packetClass.getConstructor(
                        particleEnum,
                        Boolean.TYPE,
                        Float.TYPE,
                        Float.TYPE,
                        Float.TYPE,
                        Float.TYPE,
                        Float.TYPE,
                        Float.TYPE,
                        Float.TYPE,
                        Integer.TYPE,
                        int[].class);
            }

            // 1.7.x servers
            else {
                packetClass = Class.forName(nms + "PacketPlayOutWorldParticles");
                packet = packetClass.getConstructor(
                        String.class,
                        Boolean.TYPE,
                        Float.TYPE,
                        Float.TYPE,
                        Float.TYPE,
                        Float.TYPE,
                        Float.TYPE,
                        Float.TYPE,
                        Float.TYPE,
                        Integer.TYPE);
            }
        } catch (Exception ex) {
            if (!VersionManager.isVersionAtLeast(11300)) {
                System.out.println("Failed to set up particles, are you using Cauldron?");
            }
        }
    }

    /**
     * Sends a list of packets to a player
     *
     * @param player  player to send to
     * @param packets packets to send
     * @throws Exception
     */
    public static void send(Player player, List<Object> packets)
            throws Exception {
        packets.forEach(packet -> ReflectionUtil.sendPacket(player, packet));
//        Object network = connection.get(getHandle.invoke(player));
//        for (Object packet : packets) { sendPacket.invoke(network, packet); }
    }

    /**
     * Sends a list of packets to a player
     *
     * @param player  player to send to
     * @param packets packets to send
     * @throws Exception when reflection fails
     */
    public static void send(Player player, Object[] packets)
            throws Exception {
        Arrays.stream(packets).forEach(packet -> ReflectionUtil.sendPacket(player, packet));
//        Object network = connection.get(getHandle.invoke(player));
//        for (Object packet : packets) { sendPacket.invoke(network, packet); }
    }

    /**
     * Sends packets to all players within a range
     *
     * @param loc     location of the effect
     * @param packets packets from the effect
     * @param range   range to play for
     */
    public static void send(Location loc, List<Object> packets, double range)
            throws Exception {
        range *= range;
        for (Player player : loc.getWorld().getPlayers()) {
            if (player.getLocation().distanceSquared(loc) < range) {
                send(player, packets);
            }
        }
    }

    /**
     * Sends packets to all players within a range
     *
     * @param loc     location of the effect
     * @param packets packets from the effect
     * @param range   range to play for
     */
    public static void send(Location loc, Object[] packets, double range)
            throws Exception {
        range *= range;
        for (Player player : loc.getWorld().getPlayers()) {
            if (player.getLocation().distanceSquared(loc) < range) {
                send(player, packets);
            }
        }
    }

    /**
     * Make a particle packet using the given data
     *
     * @param settings particle details
     * @param loc      location to play at
     * @return particle object or null if invalid
     * @throws Exception
     */
    public static Object make(ParticleSettings settings, Location loc)
            throws Exception {
        return make(settings, loc.getX(), loc.getY(), loc.getZ());
    }

    /**
     * Make a particle packet using the given data
     *
     * @param settings particle details
     * @param x        X coordinate
     * @param y        Y coordinate
     * @param z        Z coordinate
     * @return particle object or null if invalid
     * @throws Exception
     */

    public static Object make(ParticleSettings settings, double x, double y, double z) throws Exception {
        // Invalid particle settings
        if (settings == null || settings.type == null) {
            return null;
        }

        return make(
                settings.type.name(),
                x,
                y,
                z,
                settings.dx,
                settings.dy,
                settings.dz,
                settings.speed,
                settings.amount,
                settings.material,
                settings.data);
    }

    public static Object make(
            final String name,
            double x,
            double y,
            double z,
            float dx,
            float dy,
            float dz,
            float speed,
            int amount,
            Material material,
            int data) throws Exception {

        // 1.8+ servers use an enum value to validate the particle type
        if (VersionManager.isVersionAtLeast(VersionManager.V1_8_0)) {
            Object enumType = particleTypes.get(name);
            return packet.newInstance(
                    enumType,
                    true,
                    (float) x,
                    (float) y,
                    (float) z,
                    dx,
                    dy,
                    dz,
                    speed,
                    amount,
                    material == null ? new int[0] : new int[]{material.ordinal(), data});
        }

        // 1.7.x servers just use a string for the type,
        // so make sure it is a usable type before blindly
        // sending it through
        else {
            return packet.newInstance(name, (float) x, (float) y, (float) z, dx, dy, dz, amount, 1);
        }
    }

    public static boolean usesData(org.bukkit.Particle particle) {
        switch (particle) {
            case REDSTONE:
            case ITEM_CRACK:
            case BLOCK_CRACK:
            case BLOCK_DUST:
            case FALLING_DUST:
                return true;
        }
        return false;
    }

    // Supported version for 1.13+
    public static void play(
            ArrayList<Player> players,
            org.bukkit.Particle particle,
            double x,
            double y,
            double z,
            int count,
            double dx,
            double dy,
            double dz,
            double speed,
            Material material,
            int data) {
        Object object = null;
        switch (particle) {
            case REDSTONE:
                final Color color = Color.fromRGB((int) (255 * dx), (int) (255 * dy), (int) (255 * dz));
                dx = 0;
                dy = 0;
                dz = 0;
                object = new org.bukkit.Particle.DustOptions(color, (float) speed);
                break;
            case ITEM_CRACK:
                ItemStack item = new ItemStack(material);
                if (SkillAPI.getSettings().useSkillModelData()) {
                    ItemMeta meta = item.getItemMeta();
                    meta.setCustomModelData(data);
                    item.setItemMeta(meta);
                } else {
                    item.setData(new MaterialData(material, (byte) data));
                }
                object = item;
                break;
            case BLOCK_CRACK:
            case BLOCK_DUST:
            case FALLING_DUST:
                object = material.createBlockData();
        }
        for (Player player : players) {
            player.spawnParticle(particle, x, y, z, count, dx, dy, dz, speed, object);
        }
    }
}
