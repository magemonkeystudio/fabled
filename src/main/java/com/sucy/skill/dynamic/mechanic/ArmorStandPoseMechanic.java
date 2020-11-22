package com.sucy.skill.dynamic.mechanic;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.EulerAngle;

import java.util.Arrays;
import java.util.List;

public class ArmorStandPoseMechanic extends MechanicComponent {
    private static final String HEAD = "head";
    private static final String BODY = "body";
    private static final String LEFT_ARM = "left-arm";
    private static final String RIGHT_ARM = "right-arm";
    private static final String LEFT_LEG = "left-leg";
    private static final String RIGHT_LEG = "right-leg";

    @Override
    public String getKey() { return "armor stand pose"; }

    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets) {
        EulerAngle head = eulerAngle(settings.getString(HEAD, ""));
        EulerAngle body = eulerAngle(settings.getString(BODY, ""));
        EulerAngle leftArm = eulerAngle(settings.getString(LEFT_ARM, ""));
        EulerAngle rightArm = eulerAngle(settings.getString(RIGHT_ARM, ""));
        EulerAngle leftLeg = eulerAngle(settings.getString(LEFT_LEG, ""));
        EulerAngle rightLeg = eulerAngle(settings.getString(RIGHT_LEG, ""));

        for (LivingEntity target : targets) {
            if (!(target instanceof ArmorStand)) continue;
            ArmorStand armorStand = (ArmorStand) target;
            if (head != null) armorStand.setHeadPose(head);
            if (body != null) armorStand.setBodyPose(body);
            if (leftArm != null) armorStand.setLeftArmPose(leftArm);
            if (rightArm != null) armorStand.setRightArmPose(rightArm);
            if (leftLeg != null) armorStand.setLeftLegPose(leftLeg);
            if (rightLeg != null) armorStand.setRightLegPose(rightLeg);
        }
        return targets.size() > 0;
    }

    private static EulerAngle eulerAngle(String string) {
        if (string.equals("")) return null;
        Double[] doubles;
        try {
            doubles = Arrays.stream(string.split(",",3)).map(Double::valueOf).toArray(Double[]::new);
        } catch (NumberFormatException e) { return null; }
        if (doubles.length != 3) return null;
        return new EulerAngle(doubles[0], doubles[1], doubles[2]);
    }
}
