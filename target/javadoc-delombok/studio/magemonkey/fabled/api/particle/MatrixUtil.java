package studio.magemonkey.fabled.api.particle;

import studio.magemonkey.fabled.data.Matrix3D;
import studio.magemonkey.fabled.data.Point3D;

public class MatrixUtil {
    public static final Matrix3D IDENTITY = new Matrix3D(
            1, 0, 0,
            0, 1, 0,
            0, 0, 1
    );
    public static final Matrix3D ZERO     = new Matrix3D(
            0, 0, 0,
            0, 0, 0,
            0, 0, 0
    );

    public static Matrix3D getRotationMatrix(double xRot, double yRot, double zRot) {
        // Convert angles to radians
        xRot = Math.toRadians(xRot);
        yRot = Math.toRadians(yRot);
        zRot = Math.toRadians(zRot);

        // Calculate sin and cos values
        double sinX = Math.sin(xRot);
        double cosX = Math.cos(xRot);
        double sinY = Math.sin(yRot);
        double cosY = Math.cos(yRot);
        double sinZ = Math.sin(zRot);
        double cosZ = Math.cos(zRot);

        // Calculate the matrix values
        double x1 = cosY * cosZ;
        double x2 = cosX * sinZ + sinX * sinY * cosZ;
        double x3 = sinX * sinZ - cosX * sinY * cosZ;

        double y1 = -cosY * sinZ;
        double y2 = cosX * cosZ - sinX * sinY * sinZ;
        double y3 = sinX * cosZ + cosX * sinY * sinZ;

        double z1 = sinY;
        double z2 = -sinX * cosY;
        double z3 = cosX * cosY;

        return new Matrix3D(
                x1, y1, z1,
                x2, y2, z2,
                x3, y3, z3
        );
    }

    public static Matrix3D getScaleMatrix(double scale) {
        return new Matrix3D(
                scale, 0, 0,
                0, scale, 0,
                0, 0, scale
        );
    }

    public static Point3D multiply(Matrix3D matrix, Point3D point) {
        return new Point3D(
                matrix.getX1() * point.x + matrix.getY1() * point.y + matrix.getZ1() * point.z,
                matrix.getX2() * point.x + matrix.getY2() * point.y + matrix.getZ2() * point.z,
                matrix.getX3() * point.x + matrix.getY3() * point.y + matrix.getZ3() * point.z
        );
    }

    public static Point3D[] multiply(Matrix3D matrix, Point3D[] points) {
        Point3D[] ret = new Point3D[points.length];
        for (int i = 0; i < points.length; i++) {
            ret[i] = multiply(matrix, points[i]);
        }
        return ret;
    }

    public static Matrix3D multiply(Matrix3D m1, Matrix3D m2) {
        double x1 = m1.getX1() * m2.getX1() + m1.getY1() * m2.getX2() + m1.getZ1() * m2.getX3();
        double y1 = m1.getX1() * m2.getY1() + m1.getY1() * m2.getY2() + m1.getZ1() * m2.getY3();
        double z1 = m1.getX1() * m2.getZ1() + m1.getY1() * m2.getZ2() + m1.getZ1() * m2.getZ3();

        double x2 = m1.getX2() * m2.getX1() + m1.getY2() * m2.getX2() + m1.getZ2() * m2.getX3();
        double y2 = m1.getX2() * m2.getY1() + m1.getY2() * m2.getY2() + m1.getZ2() * m2.getY3();
        double z2 = m1.getX2() * m2.getZ1() + m1.getY2() * m2.getZ2() + m1.getZ2() * m2.getZ3();

        double x3 = m1.getX3() * m2.getX1() + m1.getY3() * m2.getX2() + m1.getZ3() * m2.getX3();
        double y3 = m1.getX3() * m2.getY1() + m1.getY3() * m2.getY2() + m1.getZ3() * m2.getY3();
        double z3 = m1.getX3() * m2.getZ1() + m1.getY3() * m2.getZ2() + m1.getZ3() * m2.getZ3();

        return new Matrix3D(
                x1, y1, z1,
                x2, y2, z2,
                x3, y3, z3
        );
    }

    public static Matrix3D multiply(Matrix3D base, Matrix3D... matrices) {
        Matrix3D result = base == null ? IDENTITY : base;
        for (Matrix3D matrix : matrices) {
            if (matrix == null) continue;
            result = multiply(result, matrix);
        }
        return result;
    }

    public static Point3D[] translate(Point3D[] points, double x, double y, double z) {
        Point3D[] ret = new Point3D[points.length];
        for (int i = 0; i < points.length; i++) {
            ret[i] = new Point3D(
                    points[i].x + x,
                    points[i].y + y,
                    points[i].z + z
            );
        }
        return ret;
    }
}
