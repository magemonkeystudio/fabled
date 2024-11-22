package studio.magemonkey.fabled.hook.mimic;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import ru.endlesscode.mimic.Mimic;
import ru.endlesscode.mimic.MimicApiLevel;
import studio.magemonkey.fabled.log.Logger;

public class MimicHook {

    private static boolean hooked = false;

    public static boolean isHooked() {
        return hooked;
    }

    public static void init(Plugin plugin) {
        if (Bukkit.getPluginManager().getPlugin("Mimic") == null) return;
        if (!MimicApiLevel.checkApiLevel(MimicApiLevel.VERSION_0_7)) {
            Logger.bug(
                    "At least Mimic 0.7 is required. " +
                            "Please download it from https://www.spigotmc.org/resources/82515/"
            );
            return;
        }

        try {
            registerMimicServices(plugin);
            hooked = true;
        } catch (Exception ex) {
            Logger.bug("Mimic hook failed: " + ex.getLocalizedMessage());
        }
    }

    private static void registerMimicServices(Plugin plugin) {
        Mimic mimic = Mimic.getInstance();
        mimic.registerClassSystem(FabledClassSystem::new, MimicApiLevel.CURRENT, plugin);
        mimic.registerLevelSystem(FabledLevelSystem::new, MimicApiLevel.CURRENT, plugin);
    }
}
