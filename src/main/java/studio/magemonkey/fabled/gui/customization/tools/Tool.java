package studio.magemonkey.fabled.gui.customization.tools;

import lombok.Getter;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@Getter
public class Tool {

    private final int slot;
    private final ItemStack icon;
    private final Map<InventoryAction, ToolType> tools;

    public Tool(int slot, ItemStack icon, Map<InventoryAction, ToolType> tools) {
        this.slot = slot;
        this.icon = icon;
        this.tools = tools;
    }
}
