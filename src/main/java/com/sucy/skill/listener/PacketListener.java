package com.sucy.skill.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.util.BuffData;
import com.sucy.skill.api.util.BuffManager;
import com.sucy.skill.api.util.BuffType;
import com.sucy.skill.hook.ProtocolLibHook;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class PacketListener extends SkillAPIListener {
    private ProtocolLibHook     protocolLib;
    private List<PacketAdapter> packetListeners = new ArrayList<>();

    @Override
    public void init() {
        protocolLib = new ProtocolLibHook(SkillAPI.inst());
        addListener(new EntityEquipmentPacketAdapter(ListenerPriority.HIGH, PacketType.Play.Server.ENTITY_EQUIPMENT));
    }

    private void addListener(PacketAdapter listener) {
        packetListeners.add(listener);
        protocolLib.register(listener);
    }

    /**
     * Sends entity equipment packet
     *
     * @param owner
     */
    public static void updateEquipment(Player owner) {
        ProtocolLibHook protocolLib = new ProtocolLibHook(SkillAPI.inst());
        PacketContainer packet      =
                protocolLib.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
        PlayerInventory inv         = owner.getInventory();
        packet.getIntegers().write(0, owner.getEntityId());
        List<Pair<EnumWrappers.ItemSlot, ItemStack>> pairList = packet.getSlotStackPairLists().read(0);
        pairList.add(new Pair<>(EnumWrappers.ItemSlot.HEAD, inv.getHelmet()));
        pairList.add(new Pair<>(EnumWrappers.ItemSlot.CHEST, inv.getChestplate()));
        pairList.add(new Pair<>(EnumWrappers.ItemSlot.LEGS, inv.getLeggings()));
        pairList.add(new Pair<>(EnumWrappers.ItemSlot.FEET, inv.getBoots()));
        pairList.add(new Pair<>(EnumWrappers.ItemSlot.MAINHAND, inv.getItemInMainHand()));
        pairList.add(new Pair<>(EnumWrappers.ItemSlot.OFFHAND, inv.getItemInOffHand()));
        packet.getSlotStackPairLists().write(0, pairList);
        protocolLib.broadcastToNearby(owner, packet);
    }

    @Override
    public void cleanup() {
        protocolLib.unregister(packetListeners);
    }

    /**
     * Used for true invisibility mechanic
     */
    private class EntityEquipmentPacketAdapter extends PacketAdapter {

        public EntityEquipmentPacketAdapter(ListenerPriority listenerPriority, PacketType... types) {
            super(SkillAPI.inst(), listenerPriority, types);
        }

        @Override
        public void onPacketSending(PacketEvent event) {
            Entity entity = protocolLib.getProtocolManager()
                    .getEntityFromID(event.getPlayer().getWorld(), event.getPacket().getIntegers().read(0));
            if (!(entity instanceof LivingEntity)) return;

            BuffData data = BuffManager.getBuffData((LivingEntity) entity, false);
            if (data == null || !data.isActive(BuffType.INVISIBILITY) || !((LivingEntity) entity).hasPotionEffect(
                    PotionEffectType.INVISIBILITY)) return;

            PacketContainer                              packet   = event.getPacket();
            ItemStack                                    air      = new ItemStack(Material.AIR);
            List<Pair<EnumWrappers.ItemSlot, ItemStack>> pairList = packet.getSlotStackPairLists().read(0);
            pairList.add(new Pair<>(EnumWrappers.ItemSlot.HEAD, air));
            pairList.add(new Pair<>(EnumWrappers.ItemSlot.CHEST, air));
            pairList.add(new Pair<>(EnumWrappers.ItemSlot.LEGS, air));
            pairList.add(new Pair<>(EnumWrappers.ItemSlot.FEET, air));
            pairList.add(new Pair<>(EnumWrappers.ItemSlot.MAINHAND, air));
            pairList.add(new Pair<>(EnumWrappers.ItemSlot.OFFHAND, air));
            packet.getSlotStackPairLists().write(0, pairList);
        }
    }
}
