package com.sucy.skill.util;

import org.bukkit.Bukkit;

public class Version {
    public static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
    public static final int MINOR_VERSION = Integer.parseInt(VERSION.split("_")[1]);
}
