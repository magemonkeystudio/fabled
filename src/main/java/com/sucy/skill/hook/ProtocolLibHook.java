package com.sucy.skill.hook;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.sucy.skill.SkillAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class ProtocolLibHook {
    private final SkillAPI        plugin;
    private final ProtocolManager protocolManager;

    public ProtocolLibHook(SkillAPI plugin) {
        this.plugin = plugin;
        protocolManager = ProtocolLibrary.getProtocolManager();
    }

    public ProtocolManager getProtocolManager() {return protocolManager;}

    public void register(PacketAdapter listener) {
        protocolManager.addPacketListener(listener);
    }

    public void unregister() {protocolManager.removePacketListeners(plugin);}

    public void unregister(PacketAdapter listener) {protocolManager.removePacketListener(listener);}

    public void unregister(Iterable<PacketAdapter> listeners) {
        listeners.forEach(c -> unregister(c));
    }

    public void broadcastToNearby(Player player, PacketContainer packet) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (player.equals(p) || !p.getWorld().equals(player.getWorld())) continue;

            double dist = player.getLocation().distanceSquared(p.getLocation());
            if (dist < Bukkit.getViewDistance() * 16) {
                try {
                    protocolManager.sendServerPacket(p, packet);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
