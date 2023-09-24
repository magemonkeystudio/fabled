/**
 * SkillAPI
 * com.sucy.skill.api.util.DamageLoreRemover
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
import mc.promcteam.engine.utils.Reflex;
import mc.promcteam.engine.utils.reflection.ReflectionManager;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

/**
 * <p>Utility class for removing vanilla damage lore lines from items.</p>
 */
public class DamageLoreRemover {
    private static Class<?>
            NBT_BASE,
            NBT_COMPOUND,
            NBT_LIST,
            NMS_ITEM,
            CRAFT_ITEM;

    private static Method
            SET,
            SET_TAG,
            SET_BOOL,
            SET_INT,
            GET_TAG,
            AS_CRAFT,
            AS_NMS;

    /**
     * <p>Sets up reflection methods/classes ahead of time so that they don't need to constantly be fetched.</p>
     */
    private static void setup() {

        try {
            NBT_BASE = ReflectionManager.MINOR_VERSION >= 17 ? Reflex.getClass("net.minecraft.nbt.NBTBase")
                    : Reflex.getNMSClass("NBTBase");
            NBT_COMPOUND = ReflectionManager.MINOR_VERSION >= 17 ? Reflex.getClass("net.minecraft.nbt.NBTTagCompound")
                    : Reflex.getNMSClass("NBTTagCompound");
            NBT_LIST = ReflectionManager.MINOR_VERSION >= 17 ? Reflex.getClass("net.minecraft.nbt.NBTTagList")
                    : Reflex.getNMSClass("NBTTagList");
            NMS_ITEM = ReflectionManager.MINOR_VERSION >= 17 ? Reflex.getClass("net.minecraft.world.item.ItemStack")
                    : Reflex.getNMSClass("ItemStack");
            CRAFT_ITEM = Reflex.getCraftClass("inventory.CraftItemStack");

            AS_NMS = CRAFT_ITEM.getMethod("asNMSCopy", ItemStack.class);
            AS_CRAFT = CRAFT_ITEM.getMethod("asCraftMirror", NMS_ITEM);
            GET_TAG = NMS_ITEM.getMethod(ReflectionManager.MINOR_VERSION >= 18 ? "s" : "getTag");
            SET_TAG = NMS_ITEM.getMethod(ReflectionManager.MINOR_VERSION >= 18 ? "c" : "setTag", NBT_COMPOUND);
            SET = NBT_COMPOUND.getMethod(ReflectionManager.MINOR_VERSION >= 18 ? "a" : "set", String.class, NBT_BASE);
            SET_BOOL = NBT_COMPOUND.getMethod(ReflectionManager.MINOR_VERSION >= 18
                    ? "a"
                    : "setBoolean", String.class, boolean.class);
            SET_INT = NBT_COMPOUND.getMethod(ReflectionManager.MINOR_VERSION >= 18 ? "a" : "setInt",
                    String.class,
                    int.class);
        } catch (Exception ex) {
            Logger.bug("Failed to set up reflection for removing damage lores.");
        }
    }

    /**
     * <p>Removes the vanilla damage lore from tools.</p>
     * <p>If you pass in something other than a tool this will do nothing.</p>
     * <p>If there was some problem with setting up the reflection classes, this will
     * also do nothing.</p>
     *
     * @param item tool to remove the lore from
     * @return the tool without the damage lore
     */
    public static ItemStack removeAttackDmg(ItemStack item) {
        if (item == null) {
            return item;
        }
        if (NBT_BASE == null) setup();
        try {
            item = item.clone();
            Object nmsStack    = AS_NMS.invoke(null, item);
            Object nbtCompound = GET_TAG.invoke(nmsStack);

            // Disable durability if needed
            if (item.getType().getMaxDurability() > 0) {
                SET_BOOL.invoke(nbtCompound, "Unbreakable", true);
                SET_INT.invoke(nbtCompound, "HideFlags", 4);
            }

            // Remove default NBT displays
            Object nbtTagList = Reflex.getInstance(NBT_LIST);
            SET.invoke(nbtCompound, "AttributeModifiers", nbtTagList);

            // Apply to item
            SET_TAG.invoke(nmsStack, nbtCompound);

            // Return result
            return (ItemStack) AS_CRAFT.invoke(null, nmsStack);
        } catch (Exception ex) {
            return item;
        }
    }
}
