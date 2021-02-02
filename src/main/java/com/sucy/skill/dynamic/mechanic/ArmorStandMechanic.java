package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.armorstand.ArmorStandInstance;
import com.sucy.skill.api.armorstand.ArmorStandManager;
import com.sucy.skill.listener.MechanicListener;
import com.sucy.skill.task.RemoveTask;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Summons an armor stand that can be used as a marker or for item display. Applies child components on the armor stand
 */
public class ArmorStandMechanic extends MechanicComponent {
    private static final Vector UP = new Vector(0, 1, 0);

    private static final String KEY = "key";
    private static final String DURATION = "duration";
    private static final String NAME = "name";
    private static final String NAME_VISIBLE = "name-visible";
    private static final String FOLLOW = "follow";
    private static final String GRAVITY = "gravity";
    private static final String SMALL = "tiny";
    private static final String ARMS = "arms";
    private static final String BASE = "base";
    private static final String VISIBLE = "visible";
    private static final String MARKER = "marker";
    private static final String FORWARD = "forward";
    private static final String UPWARD = "upward";
    private static final String RIGHT = "right";

    @Override
    public String getKey() { return "armor stand"; }

    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets) {
        String key = settings.getString(KEY, skill.getName());
        int duration = (int) (20 * parseValues(caster, DURATION, level, 5));
        String name = settings.getString(NAME, "Armor Stand");
        boolean nameVisible = settings.getBool(NAME_VISIBLE, false);
        boolean follow = settings.getBool(FOLLOW, false);
        boolean gravity = settings.getBool(GRAVITY, false);
        boolean small = settings.getBool(SMALL, false);
        boolean arms = settings.getBool(ARMS, false);
        boolean base = settings.getBool(BASE, false);
        boolean visible = settings.getBool(VISIBLE, true);
        boolean marker = settings.getBool(MARKER, false);
        double forward = parseValues(caster, FORWARD, level, 0);
        double upward = parseValues(caster, UPWARD, level, 0);
        double right = parseValues(caster, RIGHT, level, 0);

        List<LivingEntity> armorStands = new ArrayList<>();
        for (LivingEntity target : targets) {
            Location loc = target.getLocation().clone();
            Vector dir = loc.getDirection().setY(0).normalize();
            Vector side = dir.clone().crossProduct(UP);
            loc.add(dir.multiply(forward)).add(0, upward, 0).add(side.multiply(right));

            ArmorStand armorStand = target.getWorld().spawn(loc, ArmorStand.class, as -> {
                try {
                    as.setMarker(marker);
                    as.setInvulnerable(true);
                } catch (NoSuchMethodError ignored) {}
                try {
                    as.setSilent(true);
                } catch (NoSuchMethodError ignored) {}
                as.setGravity(gravity);
                as.setCustomName(name);
                as.setCustomNameVisible(nameVisible);
                as.setSmall(small);
                as.setArms(arms);
                as.setBasePlate(base);
                as.setVisible(visible);
            });
            SkillAPI.setMeta(armorStand, MechanicListener.ARMOR_STAND, true);
            armorStands.add(armorStand);

            ArmorStandInstance instance;
            if (follow) {
                instance = new ArmorStandInstance(armorStand, target, forward, upward, right);
            } else {
                instance = new ArmorStandInstance(armorStand, target);
            }
            ArmorStandManager.register(instance, target, key);
        }
        executeChildren(caster, level, armorStands);
        new RemoveTask(armorStands, duration);
        return targets.size() > 0;
    }
}
