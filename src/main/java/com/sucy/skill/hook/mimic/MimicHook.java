package com.sucy.skill.hook.mimic;

import com.sucy.skill.log.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import ru.endlesscode.mimic.classes.BukkitClassSystem;
import ru.endlesscode.mimic.level.BukkitLevelSystem;

public class MimicHook {

    private static Plugin plugin;
    private static boolean hooked = false;

    public static boolean isHooked() {
        return hooked;
    }

    public static void init(Plugin plugin) {
        if (Bukkit.getPluginManager().getPlugin("Mimic") != null) {
            try {
                MimicHook.plugin = plugin;
                registerMimicServices();
                hooked = true;
            } catch (Exception ex) {
                Logger.bug("Mimic hook failed: " + ex.getLocalizedMessage());
            }
        }
    }

    private static void registerMimicServices() {
        registerService(BukkitLevelSystem.Provider.class, new ProSkillApiLevelSystem.Provider());
        registerService(BukkitClassSystem.Provider.class, new ProSkillApiClassSystem.Provider());
    }

    private static <T> void registerService(Class<T> aClass, T service) {
        ServicesManager sm = plugin.getServer().getServicesManager();
        sm.register(aClass, service, plugin, ServicePriority.High);
    }
}
