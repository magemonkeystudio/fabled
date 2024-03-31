package com.promcteam.fabled.data;

import com.promcteam.fabled.api.particle.MatrixUtil;

public class Matrix3D {

    /*
    [
        [x1, y1, z1],
        [x2, y2, z2],
        [x3, y3, z3]
    ]
     */
    private final double[][] data = new double[3][3];

    public Matrix3D(double x1, double y1, double z1,
                    double x2, double y2, double z2,
                    double x3, double y3, double z3) {
        data[0][0] = x1;
        data[0][1] = y1;
        data[0][2] = z1;

        data[1][0] = x2;
        data[1][1] = y2;
        data[1][2] = z2;

        data[2][0] = x3;
        data[2][1] = y3;
        data[2][2] = z3;
    }

    public double get(int a, int b) {
        return data[a][b];
    }

    public double getX(int index) {
        return data[index][0];
    }

    public double getX1() {return data[0][0];}

    public double getX2() {return data[1][0];}

    public double getX3() {return data[2][0];}

    public double getY(int index) {
        return data[index][1];
    }

    public double getY1() {return data[0][1];}

    public double getY2() {return data[1][1];}

    public double getY3() {return data[2][1];}

    public double getZ(int index) {
        return data[index][2];
    }

    public double getZ1() {return data[0][2];}

    public double getZ2() {return data[1][2];}

    public double getZ3() {return data[2][2];}

    public Point3D multiply(Point3D point) {
        return MatrixUtil.multiply(this, point);
    }

    public Point3D[] multiply(Point3D[] points) {
        return MatrixUtil.multiply(this, points);
    }

    public Matrix3D multiply(Matrix3D matrix) {
        return MatrixUtil.multiply(this, matrix);
    }

    public boolean isIdentity() {
        return data[0][0] == 1 && data[0][1] == 0 && data[0][2] == 0 &&
                data[1][0] == 0 && data[1][1] == 1 && data[1][2] == 0 &&
                data[2][0] == 0 && data[2][1] == 0 && data[2][2] == 1;
    }
}
