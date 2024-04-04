package studio.magemonkey.fabled.api.particle;

import studio.magemonkey.fabled.data.Matrix3D;
import studio.magemonkey.fabled.data.Point3D;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MatrixUtilTest {
    private static double delta = 0.0001;

    @Test
    void getRotationMatrix_rotateAboutY() {
        Matrix3D matrix = MatrixUtil.getRotationMatrix(0, 90, 0);
        /*
        Expected:
         0 0 1
         0 1 0
        -1 0 0

        Since we're rotating about the Y axis, the X and Z values should be swapped
         */
        assertEquals(0, matrix.get(0, 0), delta);
        assertEquals(0, matrix.get(0, 1), delta);
        assertEquals(1, matrix.get(0, 2), delta);
        assertEquals(0, matrix.get(1, 0), delta);
        assertEquals(1, matrix.get(1, 1), delta);
        assertEquals(0, matrix.get(1, 2), delta);
        assertEquals(-1, matrix.get(2, 0), delta);
        assertEquals(0, matrix.get(2, 1), delta);
        assertEquals(0, matrix.get(2, 2), delta);
    }

    @Test
    void getRotationMatrix_rotateAboutX() {
        Matrix3D matrix = MatrixUtil.getRotationMatrix(90, 0, 0);
        /*
        Expected:
        1  0  0
        0  0 -1
        0  1  0

        Since we're rotating about the X axis, the Y and Z values should be swapped
         */
        assertEquals(1, matrix.get(0, 0), delta);
        assertEquals(0, matrix.get(0, 1), delta);
        assertEquals(0, matrix.get(0, 2), delta);
        assertEquals(0, matrix.get(1, 0), delta);
        assertEquals(0, matrix.get(1, 1), delta);
        assertEquals(-1, matrix.get(1, 2), delta);
        assertEquals(0, matrix.get(2, 0), delta);
        assertEquals(1, matrix.get(2, 1), delta);
        assertEquals(0, matrix.get(2, 2), delta);
    }

    @Test
    void getRotationMatrix_rotateAboutZ() {
        Matrix3D matrix = MatrixUtil.getRotationMatrix(0, 0, 90);
        /*
        Expected:
        0 -1  0
        1  0  0
        0  0  1

        Since we're rotating about the Z axis, the X and Y values should be swapped
         */
        assertEquals(0, matrix.get(0, 0), delta);
        assertEquals(-1, matrix.get(0, 1), delta);
        assertEquals(0, matrix.get(0, 2), delta);
        assertEquals(1, matrix.get(1, 0), delta);
        assertEquals(0, matrix.get(1, 1), delta);
        assertEquals(0, matrix.get(1, 2), delta);
        assertEquals(0, matrix.get(2, 0), delta);
        assertEquals(0, matrix.get(2, 1), delta);
        assertEquals(1, matrix.get(2, 2), delta);
    }

    @Test
    void getRotationMatrix_rotateAboutXAndZ() {
        Matrix3D matrix = MatrixUtil.getRotationMatrix(90, 0, 90);
        /*
        Expected:
        0 -1  0
        0  0 -1
        1  0  0
        */
        assertEquals(0, matrix.get(0, 0), delta);
        assertEquals(-1, matrix.get(0, 1), delta);
        assertEquals(0, matrix.get(0, 2), delta);
        assertEquals(0, matrix.get(1, 0), delta);
        assertEquals(0, matrix.get(1, 1), delta);
        assertEquals(-1, matrix.get(1, 2), delta);
        assertEquals(1, matrix.get(2, 0), delta);
        assertEquals(0, matrix.get(2, 1), delta);
        assertEquals(0, matrix.get(2, 2), delta);
    }

    @Test
    void multiply_point_x90TimesZ90() {
        Matrix3D matrix = MatrixUtil.getRotationMatrix(90, 0, 90);
        Point3D point  = new Point3D(1, 2, 3);
        Point3D result = MatrixUtil.multiply(matrix, point);

        /*
        Expected:
        -2 -3 1
         */
        assertEquals(-2, result.x, delta);
        assertEquals(-3, result.y, delta);
        assertEquals(1, result.z, delta);
    }

    @Test
    void mutiply_point_x45TimeZ45() {
        Matrix3D matrix = MatrixUtil.getRotationMatrix(45, 0, 45);
        Point3D point  = new Point3D(1, 2, 3);
        Point3D result = MatrixUtil.multiply(matrix, point);

        /*
        Expected:
        -0.7071 -0.6213 3.6213
         */
        assertEquals(-0.7071, result.x, delta);
        assertEquals(-0.6213, result.y, delta);
        assertEquals(3.6213, result.z, delta);
    }

    @Test
    void multiply_matrices_x90TimesZ90() {
        Matrix3D m1     = MatrixUtil.getRotationMatrix(90, 0, 0);
        Matrix3D m2     = MatrixUtil.getRotationMatrix(0, 0, 90);
        Matrix3D result = MatrixUtil.multiply(m1, m2);

        /*
        Expected:
        0 -1  0
        0  0 -1
        1  0  0
         */
        assertEquals(0, result.get(0, 0), delta);
        assertEquals(-1, result.get(0, 1), delta);
        assertEquals(0, result.get(0, 2), delta);
        assertEquals(0, result.get(1, 0), delta);
        assertEquals(0, result.get(1, 1), delta);
        assertEquals(-1, result.get(1, 2), delta);
        assertEquals(1, result.get(2, 0), delta);
        assertEquals(0, result.get(2, 1), delta);
        assertEquals(0, result.get(2, 2), delta);
    }
}