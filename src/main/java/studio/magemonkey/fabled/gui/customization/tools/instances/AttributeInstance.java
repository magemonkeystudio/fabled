package studio.magemonkey.fabled.gui.customization.tools.instances;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import studio.magemonkey.fabled.api.skills.Skill;
import studio.magemonkey.fabled.manager.AttributeValue;

@Getter
public class AttributeInstance extends CustomInstance {

    private final AttributeValue attribute;

    public AttributeInstance(ItemStack icon, AttributeValue attribute) {
        super(icon);
        this.attribute = attribute;
    }
}
