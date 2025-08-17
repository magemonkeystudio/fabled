package studio.magemonkey.fabled.dynamic.condition;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.cast.PlayerTextCastingData;
import studio.magemonkey.fabled.cast.PlayerCastWheel;

public class ActionBarCondition extends ConditionComponent {
    private static final String CASTING = "casting";

    private boolean casting;

    @Override
    boolean test(LivingEntity caster, int level, LivingEntity target) {
        final PlayerTextCastingData textData = Fabled.getData((Player) target).getTextCastingData();
        final PlayerCastWheel wheelData = Fabled.getData((Player) target).getCastWheel();
        final boolean isCasting = textData.isCasting() || wheelData.isCasting();
        casting = settings.getString(CASTING, "true").toLowerCase().equals("true");
        return target instanceof Player && casting == isCasting;
    }

    @Override
    public String getKey() {
        return "action bar";
    }

}