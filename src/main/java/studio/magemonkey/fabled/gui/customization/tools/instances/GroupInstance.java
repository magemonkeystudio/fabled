package studio.magemonkey.fabled.gui.customization.tools.instances;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import studio.magemonkey.fabled.data.GroupSettings;

@Getter
public class GroupInstance extends CustomInstance {

    private final GroupSettings group;

    public GroupInstance(ItemStack icon, GroupSettings group) {
        super(icon);
        this.group = group;
    }
}
