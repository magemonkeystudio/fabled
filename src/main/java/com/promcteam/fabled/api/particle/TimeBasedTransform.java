package com.promcteam.fabled.api.particle;

import com.promcteam.fabled.api.Settings;
import com.promcteam.fabled.api.enums.Direction;
import com.promcteam.fabled.data.Matrix3D;
import com.promcteam.fabled.data.Point3D;
import com.promcteam.fabled.data.formula.Formula;
import com.promcteam.fabled.data.formula.value.CustomValue;
import org.bukkit.util.Vector;

public class TimeBasedTransform {
    private static final Vector        UP     = new Vector(0, 1, 0);
    private static final CustomValue[] values = {
            new CustomValue("t"), // Represents time (iterations)
            new CustomValue("l"), // Represents the skill level
//            new CustomValue("s"),  // Represents the current effect scale
//            new CustomValue("r"),  // Represents the current effect rotation
//            new CustomValue("p"),  // Represents the current effect spin
//            new CustomValue("i"),  // Represents the current effect tilt
//            new CustomValue("x"),  // Represents the current effect x position
//            new CustomValue("y"),  // Represents the current effect y position
//            new CustomValue("z")   // Represents the current effect z position
    };
    private final        Formula       rotateFormula;
    private final        Formula       spinFormula;
    private final        Formula       tiltFormula;
    private final        Formula       scaleFormula;
    private final        Formula       translateFowardFormula;
    private final        Formula       translateUpFormula;
    private final        Formula       translateRightFormula;
    private final Direction direction;

    public TimeBasedTransform(Settings settings) {
        this.rotateFormula = new Formula(
                settings.getString("rotate", "0"),
                values
        );
        this.spinFormula = new Formula(
                settings.getString("spin", "0"),
                values
        );
        this.tiltFormula = new Formula(
                settings.getString("tilt", "0"),
                values
        );
        this.scaleFormula = new Formula(
                settings.getString("scale", "1"),
                values
        );
        this.translateFowardFormula = new Formula(
                settings.getString("forward", "0"),
                values
        );
        this.translateUpFormula = new Formula(
                settings.getString("upward", "0"),
                values
        );
        this.translateRightFormula = new Formula(
                settings.getString("right", "0"),
                values
        );

        this.direction = Direction.valueOf(settings.getString("direction", "XY"));
    }

    public Point3D[] apply(Point3D[] points, Vector forward, int iteration, int level) {
        double rotate           = rotateFormula.compute(iteration, level);
        double spin             = spinFormula.compute(iteration, level);
        double tilt             = tiltFormula.compute(iteration, level);
        double newScale         = scaleFormula.compute(iteration, level);
        double translateForward = translateFowardFormula.compute(iteration, level);
        double translateUp      = translateUpFormula.compute(iteration, level);
        double translateRight   = translateRightFormula.compute(iteration, level);

        forward.setY(0).normalize();
        forward.multiply(translateForward)
                .add(forward.clone().crossProduct(UP).multiply(translateRight));
        forward.setY(translateUp);

        Matrix3D directionMatrix = direction.getMatrix();
        Matrix3D rotationMatrix = MatrixUtil.getRotationMatrix(tilt, spin, rotate);
        Matrix3D scaleMatrix    = MatrixUtil.getScaleMatrix(newScale);

        Matrix3D finalMatrix = MatrixUtil.multiply(directionMatrix, rotationMatrix, scaleMatrix);

        return MatrixUtil.translate(finalMatrix.multiply(points), forward.getX(), forward.getY(), forward.getZ());
    }
}
