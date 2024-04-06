/**
 * Fabled
 * studio.magemonkey.fabled.api.particle.direction.XZHandler
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
package studio.magemonkey.fabled.api.particle.direction;

import studio.magemonkey.fabled.data.Matrix3D;
import studio.magemonkey.fabled.data.Point2D;
import studio.magemonkey.fabled.data.Point3D;

/**
 * Handles the XZ direction
 */
public class XZHandler implements DirectionHandler {
    public static XZHandler instance = new XZHandler();

    /**
     * Applies the two results from the polar calculation to a point
     *
     * @param point the point to apply it to
     * @param n1    first value
     * @param n2    second value
     */
    public void apply(Point3D point, double n1, double n2) {
        point.x = n1;
        point.y = 0;
        point.z = n2;
    }

    /**
     * Calculates the X value of a point after rotation
     *
     * @param p    original point
     * @param trig trig data
     * @return rotation
     */
    public double rotateX(Point3D p, Point2D trig) {
        return p.x * trig.x - p.z * trig.y;
    }

    public Point3D rotateAboutY(Point3D p, Matrix3D matrix) {
        double x = matrix.getX1() * p.x + matrix.getY1() * p.y + matrix.getZ1() * p.z;
        double y = matrix.getX2() * p.x + matrix.getY2() * p.y + matrix.getZ2() * p.z;
        double z = matrix.getX3() * p.x + matrix.getY3() * p.y + matrix.getZ3() * p.z;

        return new Point3D(x, y, z);
    }

    public Point3D rotateAboutY(Point3D p, double rads) {
        double sin = Math.sin(rads);
        double cos = Math.cos(rads);

        double x = p.x;
        double y = p.y;
        double z = p.z;

        double newX = cos * x + sin * z;
        double newY = y;
        double newZ = -sin * x + cos * z;

        Point3D ret = new Point3D(newX, newY, newZ);

        return ret;
    }

    /**
     * Calculates the Y value of a point after rotation
     *
     * @param p    original point
     * @param trig trig data
     * @return rotation
     */
    public double rotateY(Point3D p, Point2D trig) {
        return p.y;
    }

    /**
     * Calculates the Z value of a point after rotation
     *
     * @param p    original point
     * @param trig trig data
     * @return rotation
     */
    public double rotateZ(Point3D p, Point2D trig) {
        return p.x * trig.y + p.z * trig.x;
    }
}
