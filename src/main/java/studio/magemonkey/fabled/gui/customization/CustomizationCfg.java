package studio.magemonkey.fabled.gui.customization;

import net.kyori.adventure.text.Component;
import studio.magemonkey.codex.mccore.config.CommentedConfig;
import studio.magemonkey.codex.mccore.config.parse.YAMLParser;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.gui.GUIType;
import studio.magemonkey.fabled.gui.customization.tools.Tool;

import java.io.File;
import java.util.List;

public class CustomizationCfg {

    private static CommentedConfig config;

    public static void init() {
        config = new CommentedConfig(Fabled.inst(), "guis/CustomizationCfg.yml");
    }
    public static Component getTitle(GUIType type) {
        return Component.text(config.getConfig().getString(type.name().toLowerCase() + ".Title"));
    }

    public static List<Tool> getTools(GUIType type) {
        return List.of();
    }
}
