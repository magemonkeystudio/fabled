package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.enums.ExpSource;
import com.sucy.skill.api.player.PlayerClass;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Modifies player's class experience
 */
public class ExperienceMechanic extends MechanicComponent {
    private static final String TYPE = "type";
    private static final String GROUP = "group";
    private static final String EXP = "value";
    private static final String LEVEL_DOWN = "level-down";
    private static final String MODE = "mode";

    @Override
    public String getKey() {
        return "experience";
    }
    /**
     * Executes the component
     *
     * @param caster  caster of the skill
     * @param level   level of the skill
     * @param targets targets to apply to
     * @param force
     * @return true if applied to something, false otherwise
     */
    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        if(!(caster instanceof Player)) return false;
        Player player           = (Player) caster;
        String group            = settings.getString(GROUP,"class");
        if(SkillAPI.getGroups().stream().noneMatch(c->c.equalsIgnoreCase(group))) return false;

        int expValue            = settings.getInt(EXP,0);
        boolean levelDown       = settings.getBool(LEVEL_DOWN,false);
        String mode             = settings.getString(MODE,"give").toLowerCase();
        String type             = settings.getString(TYPE,"flat");
        boolean flat            = type.equalsIgnoreCase("flat");
        boolean percent         = type.equalsIgnoreCase("percent");
        PlayerClass playerClass = SkillAPI.getPlayerData(player).getClass(group);
        double allNextLevelExp  = playerClass.getData().getRequiredExp(playerClass.getLevel());
        double amount           = 0;
        if(flat)
            amount = expValue;
        else if(percent)
            amount = allNextLevelExp*expValue/100;
        switch (mode){
            case "give":
                playerClass.giveExp(amount, ExpSource.SPECIAL);
                break;
            case "take":
                if(!levelDown && amount>playerClass.getExp()) return false;
                playerClass.loseExp(amount,false,levelDown);
                break;
            case "set":
                playerClass.setExp(amount);
                break;
            default:
                return false;
        }
        return true;
    }
}
