package studio.magemonkey.fabled.gui.customization.tools;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.gui.GUIType;
import studio.magemonkey.fabled.gui.customization.CustomizationCfg;
import studio.magemonkey.fabled.gui.customization.tool.InventoryData;
import studio.magemonkey.fabled.gui.customization.tools.instances.AccountInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToolsEditor implements Listener, ToolMenu {

    private final Player player;
    private final GUIType type;
    private final List<Tool> tools = new ArrayList<>();

    private InventoryData data;
    private static boolean inUse = false;

    private final Inventory inventory;
    private final Map<Integer, Tool> toolMap = new HashMap<>();
    private final Map<Integer, Object> instanceMap = new HashMap<>();

    public ToolsEditor(Player player, GUIType type) {
        this.player = player;
        this.type = type;
        tools.addAll(CustomizationCfg.getTools(type));
        data = new InventoryData(player);
        inventory = player.getInventory();
        inventory.clear();
    }

    public void init() {
        toolMap.clear();
        instanceMap.clear();

        // Hotbar setup
        for (Tool tool : tools) {
            inventory.setItem(tool.getSlot(), tool.getIcon());
            toolMap.put(tool.getSlot(), tool);
        }

        // Dynamic-Instance setup (accounts, groups, classes, skills, ...)
        switch (type) {
            case ACCOUNT -> {
            }
            case GROUP -> {
            }
            case CLASS -> {
            }
            case CLASS_SKILLS -> {
            }
            case CLASS_ATTRIBUTES -> {
            }
        }
    }

    public void open() {
        if (!inUse) {
            this.data = new InventoryData(player);
            inUse = true;
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {

    }

    @Override
    public void restore() {
        if(data != null) {
            data.restore(player);
        }
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
