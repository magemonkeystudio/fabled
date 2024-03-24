/**
 * Fabled
 * com.promcteam.fabled.api.util.ItemSerializer
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 ProMCTeam
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
package com.promcteam.fabled.api.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.promcteam.fabled.Fabled;
import com.promcteam.codex.mccore.util.VersionManager;
import com.promcteam.codex.utils.Reflex;
import com.promcteam.codex.utils.reflection.ReflectionManager;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Based on the thread https://bukkit.org/threads/help-with-serialized-nbttagcompounds.116335/

/**
 * Will be removed in lieu of Codex's ItemUT from/toBase64
 */
@Deprecated
public class ItemSerializer {

    // Missing the following (not sure what to do with these):
//    MULTISHOT
//    QUICK_CHARGE
//    PIERCING
//    MENDING
//    VANISHING_CURSE
//    SOUL_SPEED
    private static final BiMap<String, Integer> ENCHANT_IDS = ImmutableBiMap.<String, Integer>builder()
            .put("PROTECTION_ENVIRONMENTAL", 0)
            .put("PROTECTION_FIRE", 1)
            .put("PROTECTION_FALL", 2)
            .put("PROTECTION_EXPLOSIONS", 3)
            .put("PROTECTION_PROJECTILE", 4)
            .put("OXYGEN", 5)
            .put("WATER_WORKER", 6)
            .put("THORNS", 7)
            .put("DEPTH_STRIDER", 8)
            .put("FROST_WALKER", 9)
            .put("BINDING_CURSE", 10)
            .put("DAMAGE_ALL", 16)
            .put("DAMAGE_UNDEAD", 17)
            .put("DAMAGE_ARTHROPODS", 18)
            .put("KNOCKBACK", 19)
            .put("FIRE_ASPECT", 20)
            .put("LOOT_BONUS_MOBS", 21)
            .put("SWEEPING_EDGE", 22)
            .put("DIG_SPEED", 32)
            .put("SILK_TOUCH", 33)
            .put("DURABILITY", 34)
            .put("LOOT_BONUS_BLOCKS", 35)
            .put("ARROW_DAMAGE", 48)
            .put("ARROW_KNOCKBACK", 49)
            .put("ARROW_FIRE", 50)
            .put("ARROW_INFINITE", 51)
            .put("LUCK", 61)
            .put("LURE", 62)
            .put("MENDING", 70)
            .put("VANISHING_CURSE", 71)
            .put("LOYALTY", 80)
            .put("IMPALING", 81)
            .put("RIPTIDE", 82)
            .put("CHANNELING", 83)
            .build();
    private static       boolean                initialized = false;
    private static       Constructor<?>         nbtTagListConstructor;
    private static       Constructor<?>         nbtTagCompoundConstructor;
    private static       Constructor<?>         craftItemConstructor;
    private static       Constructor<?>         craftItemNMSConstructor;
    private static       Constructor<?>         nmsItemConstructor;
    private static       Method                 itemStack_save;
    private static       Method                 nbtTagList_add;
    private static       Method                 nbtTagList_size;
    private static       Method                 nbtTagList_get;
    private static       Method                 nbtCompressedStreamTools_write;
    private static       Method                 nbtCompressedStreamTools_read;
    private static       Method                 nbtTagCompound_set;
    private static       Method                 nbtTagCompound_getList;
    private static       Method                 nbtTagCompound_isEmpty;
    private static       Field                  craftItemStack_getHandle;

    private static void initialize() {
        if (initialized)
            return;

        initialized = true;

        try {
            Class<?> craftItemStack = Reflex.getCraftClass("inventory.CraftItemStack");
            Class<?> nmsItemStack = ReflectionManager.MINOR_VERSION >= 17
                    ? Reflex.getClass("net.minecraft.world.item.ItemStack")
                    : Reflex.getNMSClass("ItemStack");
            craftItemConstructor = Reflex.getConstructor(craftItemStack, ItemStack.class);
            craftItemConstructor.setAccessible(true);
            craftItemNMSConstructor = Reflex.getConstructor(craftItemStack, nmsItemStack);
            craftItemNMSConstructor.setAccessible(true);
            craftItemStack_getHandle = Reflex.getField(craftItemStack, "handle");
            craftItemStack_getHandle.setAccessible(true);

            Class<?> nbtBase = ReflectionManager.MINOR_VERSION >= 17
                    ? Reflex.getClass("net.minecraft.nbt.NBTBase")
                    : Reflex.getNMSClass("NBTBase");
            Class<?> nbtTagCompound = ReflectionManager.MINOR_VERSION >= 17
                    ? Reflex.getClass("net.minecraft.nbt.NBTTagCompound")
                    : Reflex.getNMSClass("NBTTagCompound");
            Class<?> nbtTagList = ReflectionManager.MINOR_VERSION >= 17
                    ? Reflex.getClass("net.minecraft.nbt.NBTTagList")
                    : Reflex.getNMSClass("NBTTagList");
            Class<?> nbtCompressedStreamTools = ReflectionManager.MINOR_VERSION >= 17
                    ? Reflex.getClass("net.minecraft.nbt.NBTCompressedStreamTools")
                    : Reflex.getNMSClass("NBTCompressedStreamTools");
            nmsItemConstructor = Reflex.getConstructor(nmsItemStack, nbtTagCompound);
            nmsItemConstructor.setAccessible(true);
            nbtTagCompoundConstructor = nbtTagCompound.getConstructor();
            nbtTagListConstructor = nbtTagList.getConstructor();
            if (ReflectionManager.MINOR_VERSION >= 18) {
                nbtTagCompound_set = Reflex.getMethod(nbtTagCompound, "a", String.class, nbtBase);
                nbtTagCompound_getList = Reflex.getMethod(nbtTagCompound, "c", String.class, int.class);
                nbtTagCompound_isEmpty = Reflex.getMethod(nbtTagCompound, "f");

                itemStack_save = Reflex.getMethod(nmsItemStack, "b", nbtTagCompound);
                nbtTagList_get = Reflex.getMethod(nbtTagList, "k", int.class);
            } else {
                nbtTagCompound_set = Reflex.getMethod(nbtTagCompound, "set", String.class, nbtBase);
                nbtTagCompound_getList = Reflex.getMethod(nbtTagCompound, "getList", String.class, int.class);
                nbtTagCompound_isEmpty = Reflex.getMethod(nbtTagCompound, "isEmpty");

                itemStack_save = Reflex.getMethod(nmsItemStack, "save", nbtTagCompound);
                nbtTagList_get = Reflex.getMethod(nbtTagList, "get", int.class);
            }

            nbtTagList_add = Reflex.getMethod(nbtTagList, "add", nbtBase);
            nbtTagList_size = Reflex.getMethod(nbtTagList, "size");

            nbtCompressedStreamTools_write =
                    Reflex.getMethod(nbtCompressedStreamTools, "a", nbtTagCompound, DataOutput.class);
            nbtCompressedStreamTools_read = Reflex.getMethod(nbtCompressedStreamTools, "a", DataInputStream.class);
        } catch (Exception ex) {
            Fabled.inst()
                    .getLogger()
                    .warning("Server doesn't support NBT serialization - resorting to a less complete implementation");
        }
    }

    public static String toBase64(ItemStack[] items) {
        if (items == null) return null;

        initialize();
        if (nbtCompressedStreamTools_read == null) {
            return basicSerialize(items);
        }
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            DataOutputStream      dataOutput   = new DataOutputStream(outputStream);
            Object                itemList     = Reflex.invokeConstructor(nbtTagListConstructor);

            // Save every element in the list
            for (ItemStack item : items) {
                Object outputObject = Reflex.invokeConstructor(nbtTagCompoundConstructor);
                Object craft        = getCraftVersion(item);

                // Convert the item stack to a NBT compound
                if (craft != null)
                    Reflex.invokeMethod(itemStack_save, craftItemStack_getHandle.get(craft), outputObject);
                Reflex.invokeMethod(nbtTagList_add, itemList, outputObject);
            }

            Object wrapper = Reflex.invokeConstructor(nbtTagCompoundConstructor);
            Reflex.invokeMethod(nbtTagCompound_set, wrapper, "i", itemList);

            Reflex.invokeMethod(nbtCompressedStreamTools_write, null, wrapper, dataOutput);

            // Serialize that array
            return new BigInteger(1, outputStream.toByteArray()).toString(32);
        } catch (Exception ex) {
            return null;
        }
    }

    public static ItemStack[] fromBase64(String data) {
        if (data == null) return null;

        initialize();
        if (data.indexOf(';') >= 0) {
            return basicDeserialize(data);
        }
        try {
            ByteArrayInputStream inputStream     = new ByteArrayInputStream(new BigInteger(data, 32).toByteArray());
            DataInputStream      dataInputStream = new DataInputStream(inputStream);
            Object wrapper =
                    Reflex.invokeMethod(nbtCompressedStreamTools_read, null, dataInputStream);
            Object itemList = Reflex.invokeMethod(nbtTagCompound_getList, wrapper, "i", 10);
            ItemStack[] items =
                    new ItemStack[(Integer) Reflex.invokeMethod(nbtTagList_size, itemList)];

            for (int i = 0; i < items.length; i++) {
                Object inputObject = Reflex.invokeMethod(nbtTagList_get, itemList, i);

                // IsEmpty
                if (!(Boolean) Reflex.invokeMethod(nbtTagCompound_isEmpty, inputObject)) {
                    items[i] = (ItemStack) Reflex.invokeConstructor(craftItemNMSConstructor,
                            Reflex.invokeConstructor(nmsItemConstructor, inputObject));
                }
            }

            // Serialize that array
            return items;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static Object getCraftVersion(ItemStack stack) throws Exception {
        if (stack == null)
            return null;
        else if (stack.getClass() == ItemStack.class)
            return Reflex.invokeConstructor(craftItemConstructor, stack);
        else
            return stack;
    }

    private static String basicSerialize(ItemStack[] items) {
        StringBuilder builder = new StringBuilder();
        builder.append(items.length);
        builder.append(';');
        for (int i = 0; i < items.length; i++) {
            ItemStack is = items[i];
            if (is != null) {
                builder.append(i);
                builder.append('#');

                if (VersionManager.isVersionAtLeast(11605)) {
                    String isType = String.valueOf(is.getType());
                    builder.append("t@");
                    builder.append(isType);
                } else {
                    String isType = String.valueOf(is.getType().getId());
                    builder.append("t@");
                    builder.append(isType);
                }

                if (is.getDurability() != 0) {
                    String isDurability = String.valueOf(is.getDurability());
                    builder.append(":d@");
                    builder.append(isDurability);
                }

                if (is.getAmount() != 1) {
                    String isAmount = String.valueOf(is.getAmount());
                    builder.append(":a@");
                    builder.append(isAmount);
                }

                Map<Enchantment, Integer> isEnch = is.getEnchantments();
                if (isEnch.size() > 0) {
                    for (Map.Entry<Enchantment, Integer> ench : isEnch.entrySet()) {
                        builder.append(":e@");
                        builder.append(ENCHANT_IDS.get(ench.getKey().getName()));
                        builder.append('@');
                        builder.append(ench.getValue());
                    }
                }

                ItemMeta meta = is.getItemMeta();
                if (meta != null) {
                    if (meta.hasDisplayName()) {
                        builder.append(":n@");
                        builder.append(meta.getDisplayName().replaceAll("[:@#;]", ""));
                    }

                    if (meta.hasLore()) {
                        for (String line : meta.getLore()) {
                            builder.append(":l@");
                            builder.append(line.replaceAll("[:;@#]", ""));
                        }
                    }
                }

                builder.append(';');
            }
        }
        return builder.toString();
    }

    private static ItemStack[] basicDeserialize(String invString) {
        String[] serializedBlocks = invString.split(";");
        if (serializedBlocks.length == 0)
            return null;
        String      invInfo               = serializedBlocks[0];
        ItemStack[] deserializedInventory = new ItemStack[Integer.valueOf(invInfo)];

        for (int i = 1; i <= deserializedInventory.length && i < serializedBlocks.length; i++) {
            String[] serializedBlock = serializedBlocks[i].split("#");
            int      stackPosition   = Integer.valueOf(serializedBlock[0]);

            if (stackPosition >= deserializedInventory.length) {
                continue;
            }

            ItemStack is               = null;
            Boolean   createdItemStack = false;

            String[] serializedItemStack = serializedBlock[1].split(":");
            for (String itemInfo : serializedItemStack) {
                String[] itemAttribute = itemInfo.split("@");
                if (itemAttribute[0].equals("t")) {
                    if (VersionManager.isVersionAtLeast(11605)) {
                        String         id  = String.valueOf(itemAttribute[1]);
                        final Material mat = Material.getMaterial(id);
                        is = new ItemStack(mat);
                        createdItemStack = true;
                    } else {
                        int id = Integer.valueOf(itemAttribute[1]);
                        if (id >= 2256) id -= 2267 - Material.values().length;
                        final Material mat = Material.values()[id];
                        is = new ItemStack(mat);
                        createdItemStack = true;
                    }
                } else if (itemAttribute[0].equals("d") && createdItemStack) {
                    is.setDurability(Short.valueOf(itemAttribute[1]));
                } else if (itemAttribute[0].equals("a") && createdItemStack) {
                    is.setAmount(Integer.valueOf(itemAttribute[1]));
                } else if (itemAttribute[0].equals("e") && createdItemStack) {
                    final String name = ENCHANT_IDS.inverse().getOrDefault(Integer.valueOf(itemAttribute[1]), "OXYGEN");
                    is.addUnsafeEnchantment(Enchantment.getByName(name), Integer.valueOf(itemAttribute[2]));
                } else if (itemAttribute[0].equals("n") && createdItemStack) {
                    ItemMeta meta = is.getItemMeta();
                    if (meta != null) {
                        meta.setDisplayName(itemAttribute[1]);
                        is.setItemMeta(meta);
                    }
                } else if (itemAttribute[0].equals("l") && createdItemStack) {
                    ItemMeta meta = is.getItemMeta();
                    if (meta != null) {
                        List<String> lore = meta.getLore();
                        if (lore == null) lore = new ArrayList<>();
                        if (itemAttribute.length >= 1)
                            lore.add(itemAttribute[1]);
                        meta.setLore(lore);
                        is.setItemMeta(meta);
                    }
                }
            }
            deserializedInventory[stackPosition] = is;
        }

        return deserializedInventory;
    }
}
