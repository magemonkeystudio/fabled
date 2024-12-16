package studio.magemonkey.fabled.dynamic.condition;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.cast.PlayerTextCastingData;

public class ActionBarCondition extends ConditionComponent {
    private static final String CASTING  = "casting";

    private boolean casting;

    @Override
    boolean test(LivingEntity caster, int level, LivingEntity target) {
        final PlayerTextCastingData castingData = Fabled.getData((Player) target).getTextCastingData();
        casting = settings.getString(CASTING, "true").toLowerCase().equals("true");
        return target instanceof Player && casting == castingData.isCasting();
    }

    @Override
    public String getKey() {
        return "action bar";
    }

}