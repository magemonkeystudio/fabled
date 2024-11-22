package studio.magemonkey.fabled.gui.customization.tools.instances;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import studio.magemonkey.fabled.api.classes.FabledClass;

@Getter
public class FabledClassInstance extends CustomInstance {

    private final FabledClass fabledClass;

    public FabledClassInstance(ItemStack icon, FabledClass fabledClass) {
        super(icon);
        this.fabledClass = fabledClass;
    }
}
