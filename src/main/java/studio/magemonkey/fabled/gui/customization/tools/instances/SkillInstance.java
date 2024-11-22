package studio.magemonkey.fabled.gui.customization.tools.instances;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import studio.magemonkey.fabled.api.skills.Skill;

@Getter
public class SkillInstance extends CustomInstance {

    private final Skill skill;

    public SkillInstance(ItemStack icon, Skill skill) {
        super(icon);
        this.skill = skill;
    }
}
