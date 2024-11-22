package studio.magemonkey.fabled.gui.customization.tools.instances;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
public class CustomInstance {

    private final ItemStack icon;

    public CustomInstance(ItemStack icon) {
        this.icon = icon;
    }
}
