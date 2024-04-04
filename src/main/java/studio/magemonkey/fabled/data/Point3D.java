/**
 * Fabled
 * studio.magemonkey.fabled.data.Point
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 Mage Monkey Studios
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package studio.magemonkey.fabled.data;

import studio.magemonkey.fabled.api.particle.MatrixUtil;

public class Point3D {
    public double x;
    public double y;
    public double z;

    public Point3D() {
        this.x = this.y = this.z = 0;
    }

    public Point3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Rotates the point around the origin
     * @param xRot The rotation around the x-axis (in degrees)
     * @param yRot The rotation around the y-axis (in degrees)
     * @param zRot The rotation around the z-axis (in degrees)
     */
    public void rotate(double xRot, double yRot, double zRot) {
        Matrix3D rotMatrix = MatrixUtil.getRotationMatrix(xRot, yRot, zRot);
        Point3D newPoint = MatrixUtil.multiply(rotMatrix, this);

        this.x = newPoint.x;
        this.y = newPoint.y;
        this.z = newPoint.z;
    }

    /**
     * Rotates the point around the origin
     * @param xRot The rotation around the x-axis (in degrees)
     */
    public void rotateX(double xRot) {
        rotate(xRot, 0, 0);
    }

    /**
     * Rotates the point around the origin
     * @param yRot The rotation around the y-axis (in degrees)
     */
    public void rotateY(double yRot) {
        rotate(0, yRot, 0);
    }

    /**
     * Rotates the point around the origin
     * @param zRot The rotation around the z-axis (in degrees)
     */
    public void rotateZ(double zRot) {
        rotate(0, 0, zRot);
    }

    @Override
    public String toString() {
        return "(" + String.format("%.2f", x) + ", " + String.format("%.2f", y) + ", " + String.format("%.2f", z) + ")";
    }
}
