package studio.magemonkey.fabled.dynamic.mechanic;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.enums.ExpSource;
import studio.magemonkey.fabled.api.player.PlayerClass;
import studio.magemonkey.fabled.listener.MechanicListener;

import java.util.List;

/**
 * Modifies player's class experience
 */
public class ExperienceMechanic extends MechanicComponent {

    // Shared Variables
    private static final String VALUE      = "value"; // Amount of XP to Give
    private static final String MODE       = "mode"; // Give, Set, Take
    private static final String TYPE       = "type"; // Flat, Percent, Levels
    private static final String GROUP      = "group"; // Specified Class Group
    private static final String LEVEL_DOWN = "level-down"; // Whether to Level Down or not
    private static final String VANILLA    = "vanilla"; // Boolean whether to give vanills experience instead.

    @Override
    public String getKey() {
        return "experience";
    }

    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        if (!(caster instanceof Player)) return false;
        Player player = (Player) caster;

        boolean vanilla = settings.getBool(VANILLA, false);

        if (vanilla) {
            return vanillaExperienceMechanic(player, level, targets, force);
        } else {
            return classExperienceMechanic(player, level, targets, force);
        }
    }

    public boolean vanillaExperienceMechanic(Player caster, int level, List<LivingEntity> targets, boolean force) {
        int     expValue  = (int) getNum(caster, VALUE, 0.0);
        String  mode      = settings.getString(MODE, "give").toLowerCase();
        String  type      = settings.getString(TYPE, "flat").toLowerCase();
        boolean levelDown = settings.getBool(LEVEL_DOWN, true);

        int previousLevel = caster.getLevel();

        // Levels
        if (type.equals("levels")) {
            switch (mode) {
                case "give":
                    caster.giveExpLevels(expValue);
                    break;
                case "set":
                    caster.setLevel(expValue);
                    break;
                case "take":
                    caster.giveExpLevels(-expValue);
                    break;
                default:
                    return false;
            }
        }
        // Percent
        else if (type.equals("percent")) {
            int expToLevel = caster.getExpToLevel();
            int percentExp = expToLevel * expValue / 100;
            switch (mode) {
                case "give":
                    caster.giveExp(percentExp);
                    break;
                case "set":
                    caster.setExp(expValue / 100f);
                    break;
                case "take":
                    caster.giveExp(-percentExp);
                    break;
                default:
                    return false;
            }
        }
        // Flat
        else if (type.equals("flat")) {
            switch (mode) {
                case "give":
                    caster.giveExp(expValue);
                    break;
                case "set":
                    caster.setLevel(0);
                    caster.setExp(0);
                    caster.giveExp(expValue);
                    break;
                case "take":
                    caster.giveExp(-expValue);
                    break;
                default:
                    return false;
            }
        } else {
            return false; // Invalid
        }

        // Prevent level down if not allowed
        if (caster.getLevel() < previousLevel) {
            if (!levelDown) {
                caster.setLevel(previousLevel);
                caster.setExp(0);
            }
        }

        return true;
    }

    public boolean classExperienceMechanic(Player caster, int level, List<LivingEntity> targets, boolean force) {
        int     value     = (int) getNum(caster, VALUE, 0.0);
        String  mode      = settings.getString(MODE, "give").toLowerCase();
        String  type      = settings.getString(TYPE, "flat").toLowerCase();
        boolean levelDown = settings.getBool(LEVEL_DOWN, true);
        String  group     = settings.getString(GROUP, "class");
        if (Fabled.getGroups().stream().noneMatch(c -> c.equalsIgnoreCase(group))) return false;
        PlayerClass playerClass     = Fabled.getData(caster).getClass(group);
        double      allNextLevelExp = playerClass.getData().getRequiredExp(playerClass.getLevel());

        // Levels
        if (type.equals("levels")) {
            switch (mode) {
                case "give":
                    playerClass.giveLevels(value);
                    break;
                case "set":
                    playerClass.setLevel(value);
                    break;
                case "take":
                    playerClass.loseLevels(value);
                    break;
                default:
                    return false;
            }
        }
        // Percent
        else if (type.equals("percent")) {
            double percentExp = allNextLevelExp * value / 100;
            switch (mode) {
                case "give":
                    MechanicListener.addExemptExperience(caster, percentExp);
                    playerClass.giveExp(percentExp, ExpSource.PLUGIN);
                case "set":
                    playerClass.setExp(percentExp);
                case "take":
                    playerClass.loseExp(percentExp, false, levelDown, true);
                default:
                    return false;
            }
        }
        // Flat
        else if (type.equals("flat")) {
            switch (mode) {
                case "give":
                    MechanicListener.addExemptExperience(caster, value);
                    playerClass.giveExp(value, ExpSource.PLUGIN);
                case "set":
                    playerClass.setExp(value);
                case "take":
                    playerClass.loseExp(value, false, levelDown, true);
                default:
                    return false;
            }
        } else {
            return false; // Invalid
        }
        return true;
    }
}
