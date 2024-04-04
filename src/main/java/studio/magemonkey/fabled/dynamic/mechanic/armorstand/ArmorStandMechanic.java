package studio.magemonkey.fabled.dynamic.mechanic.armorstand;

import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.armorstand.ArmorStandInstance;
import studio.magemonkey.fabled.api.armorstand.ArmorStandManager;
import studio.magemonkey.fabled.dynamic.TempEntity;
import studio.magemonkey.fabled.dynamic.mechanic.MechanicComponent;
import studio.magemonkey.fabled.listener.MechanicListener;
import studio.magemonkey.fabled.task.RemoveTask;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Summons an armor stand that can be used as a marker or for item display. Applies child components on the armor stand
 */
public class ArmorStandMechanic extends MechanicComponent {
    private static final Vector UP = new Vector(0, 1, 0);

    private static final String KEY          = "key";
    private static final String DURATION     = "duration";
    private static final String NAME         = "name";
    private static final String NAME_VISIBLE = "name-visible";
    private static final String FOLLOW       = "follow";
    private static final String GRAVITY      = "gravity";
    private static final String SMALL        = "tiny";
    private static final String ARMS         = "arms";
    private static final String BASE         = "base";
    private static final String VISIBLE      = "visible";
    private static final String MARKER       = "marker";
    private static final String FORWARD      = "forward";
    private static final String UPWARD       = "upward";
    private static final String RIGHT        = "right";

    @Override
    public String getKey() {
        return "armor stand";
    }

    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        String  key         = settings.getString(KEY, skill.getName());
        int     duration    = (int) (20 * parseValues(caster, DURATION, level, 5));
        String  name        = settings.getString(NAME, "Armor Stand");
        boolean nameVisible = settings.getBool(NAME_VISIBLE, false);
        boolean follow      = settings.getBool(FOLLOW, false);
        boolean gravity     = settings.getBool(GRAVITY, false);
        boolean small       = settings.getBool(SMALL, false);
        boolean arms        = settings.getBool(ARMS, false);
        boolean base        = settings.getBool(BASE, false);
        boolean visible     = settings.getBool(VISIBLE, true);
        boolean marker      = settings.getBool(MARKER, false);
        double  forward     = parseValues(caster, FORWARD, level, 0);
        double  upward      = parseValues(caster, UPWARD, level, 0);
        double  right       = parseValues(caster, RIGHT, level, 0);

        List<LivingEntity> armorStands = new ArrayList<>();
        for (LivingEntity target : targets) {
            Location loc  = target.getLocation().clone();
            Vector   dir  = loc.getDirection().setY(0).normalize();
            Vector   side = dir.clone().crossProduct(UP);
            loc.add(dir.multiply(forward)).add(0, upward, 0).add(side.multiply(right));

            Consumer<ArmorStand> onSpawn = as -> {
                try { // 1.13+
                    as.setPersistent(false);
                } catch (NoSuchMethodError ignored) {
                }
                try { // 1.19+
                    as.setMarker(marker);
                    as.setInvulnerable(true);
                } catch (NoSuchMethodError ignored) {
                }
                try { // 1.10+
                    as.setSilent(true);
                } catch (NoSuchMethodError ignored) {
                }
                as.setGravity(gravity);
                as.setCustomName(name);
                as.setCustomNameVisible(nameVisible);
                as.setSmall(small);
                as.setArms(arms);
                as.setBasePlate(base);
                as.setVisible(visible);
            };
            ArmorStand as;
            try {
                as = target.getWorld().spawn(loc, ArmorStand.class, onSpawn);
            } catch (NoSuchMethodError e) {
                as = target.getWorld().spawn(loc, ArmorStand.class);
                onSpawn.accept(as);
            }
            Fabled.setMeta(as, MechanicListener.ARMOR_STAND, true);
            armorStands.add(as);

            ArmorStandInstance instance;
            if (follow) {
                instance = new ArmorStandInstance(as, target, true, forward, upward, right);
            } else {
                instance = new ArmorStandInstance(as, target, false);
            }
            ArmorStandManager.register(instance, target, key);
        }
        executeChildren(caster, level, armorStands, force);
        new RemoveTask(armorStands, duration);
        return targets.size() > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void playPreview(List<Runnable> onPreviewStop,
                            Player caster,
                            int level,
                            Supplier<List<LivingEntity>> targetSupplier) {
        double forward = parseValues(caster, FORWARD, level, 0);
        double upward  = parseValues(caster, UPWARD, level, 0);
        double right   = parseValues(caster, RIGHT, level, 0);
        super.playPreview(onPreviewStop, caster, level, () -> {
            List<LivingEntity> newTargets = new ArrayList<>();
            for (LivingEntity target : targetSupplier.get()) {
                Location loc  = target.getLocation().clone();
                Vector   dir  = loc.getDirection().setY(0).normalize();
                Vector   side = dir.clone().crossProduct(UP);
                loc.add(dir.multiply(forward)).add(0, upward, 0).add(side.multiply(right));
                newTargets.add(new TempEntity(loc));
            }
            return newTargets;
        });
    }
}
