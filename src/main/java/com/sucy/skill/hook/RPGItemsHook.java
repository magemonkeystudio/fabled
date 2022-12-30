package com.sucy.skill.hook;

import org.bukkit.inventory.ItemStack;
import su.nightexpress.quantumrpg.stats.items.ItemStats;

public class RPGItemsHook {

    public static boolean isRPGItem(ItemStack item) {
        return ItemStats.getModule(item) != null;
    }

}
