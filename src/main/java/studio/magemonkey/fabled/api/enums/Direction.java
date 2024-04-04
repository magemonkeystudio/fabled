/**
 * Fabled
 * studio.magemonkey.fabled.api.enums.Direction
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 Mage Monkey Studios
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
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
package studio.magemonkey.fabled.api.enums;

import studio.magemonkey.fabled.api.particle.MatrixUtil;
import studio.magemonkey.fabled.data.Matrix3D;

/**
 * Direction used in the Particle class that are defined by what directions it uses for 2D shapes
 */
public enum Direction {

    /**
     * X-axis and Y-axis
     */
    XY,

    /**
     * Y-axis and Z-axis
     */
    YZ,

    /**
     * X-axis and Z-axis
     */
    XZ;

    /**
     * Gets the rotation matrix for the direction.
     * This matrix can be used to transform a point from the XY plane into the direction's plane.
     *
     * @return The rotation matrix
     */
    public Matrix3D getMatrix() {
        return MatrixUtil.getRotationMatrix(
                this == XZ ? 90 : 0,
                this == YZ ? 90 : 0,
                0
        );
    }
}
