package solver.algebra;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MatrixTest {

    private static final double EPSILON = 1e-4;

    @Test
    void testMultiplyByScalar() {
        double[][] data = {
                {1.0, 2.0},
                {3.0, 4.0}
        };
        Matrix m = new Matrix(data);
        Matrix res = m.multiplyByScalar(2.0);

        assertEquals(2.0, res.data[0][0], EPSILON);
        assertEquals(4.0, res.data[0][1], EPSILON);
        assertEquals(6.0, res.data[1][0], EPSILON);
        assertEquals(8.0, res.data[1][1], EPSILON);
    }

    @Test
    void testMultiplyMatrices() {
        double[][] a = {
                {1.0, 2.0},
                {3.0, 4.0}
        };
        double[][] b = {
                {2.0, 0.0},
                {1.0, 2.0}
        };
        Matrix m1 = new Matrix(a);
        Matrix m2 = new Matrix(b);
        Matrix product = m1.multiplyMatrix(m2);

        assertNotNull(product);
        // [1*2 + 2*1, 1*0 + 2*2] = [4, 4]
        // [3*2 + 4*1, 3*0 + 4*2] = [10, 8]
        assertEquals(4.0, product.data[0][0], EPSILON);
        assertEquals(4.0, product.data[0][1], EPSILON);
        assertEquals(10.0, product.data[1][0], EPSILON);
        assertEquals(8.0, product.data[1][1], EPSILON);
    }

    @Test
    void testDeterminant() {
        double[][] data = {
                {6.0, 1.0, 1.0},
                {4.0, -2.0, 5.0},
                {2.0, 8.0, 7.0}
        };
        Matrix m = new Matrix(data);
        // det(A) = -306.0
        assertEquals(-306.0, m.determinant(), EPSILON);
    }

    @Test
    void testNorm2Basic() {
        // Матриця 2x2: елементи 1, 2, 2, 4 -> 1 + 4 + 4 + 16 = 25 -> sqrt(25) = 5.0
        double[][] data = {
                {1.0, 2.0},
                {2.0, 4.0}
        };
        Matrix m = new Matrix(data);
        assertEquals(5.0, m.norm2(), EPSILON);
    }

    @Test
    void testNorm2WithNegativeValues() {
        // Перевірка роботи з від'ємними числами: (-3)^2 + 4^2 = 9 + 16 = 25 -> sqrt(25) = 5.0
        double[][] data = {
                {-3.0, 0.0},
                {0.0, 4.0}
        };
        Matrix m = new Matrix(data);
        assertEquals(5.0, m.norm2(), EPSILON);
    }

    @Test
    void testNormWithP1() {
        // L_1 entrywise норма: сума абсолютних значень |-1| + |2| + |-3| + |4| = 10.0
        double[][] data = {
                {-1.0, 2.0},
                {-3.0, 4.0}
        };
        Matrix m = new Matrix(data);
        assertEquals(10.0, m.norm(1.0), EPSILON);
    }

    @Test
    void testNormConsistencyWithNorm2() {
        // norm(2.0) має давати точно такий самий результат, як і norm2()
        double[][] data = {
                {1.5, -2.5},
                {3.0, 4.2}
        };
        Matrix m = new Matrix(data);
        assertEquals(m.norm2(), m.norm(2.0), EPSILON);
    }

    @Test
    void testNormWithP3() {
        // L_3 норма: (|1|^3 + |2|^3 + |3|^3)^(1/3) = (1 + 8 + 27)^(1/3) = 36^(1/3) ≈ 3.3019
        double[][] data = {
                {1.0, -2.0},
                {3.0, 0.0}
        };
        Matrix m = new Matrix(data);
        double expected = Math.cbrt(36.0);
        assertEquals(expected, m.norm(3.0), EPSILON);
    }

    @Test
    void testNormInfinity() {
        // p = Infinity: максимальний за модулем елемент матриці = |-9.5| = 9.5
        double[][] data = {
                {2.0, -9.5},
                {7.1, 4.0}
        };
        Matrix m = new Matrix(data);
        assertEquals(9.5, m.norm(Double.POSITIVE_INFINITY), EPSILON);
    }

    @Test
    void testNormInvalidP() {
        // p < 1 повинно виводити повідомлення та повертати Double.NaN
        double[][] data = {
                {1.0, 2.0},
                {3.0, 4.0}
        };
        Matrix m = new Matrix(data);
        assertTrue(Double.isNaN(m.norm(0.5)));
        assertTrue(Double.isNaN(m.norm(-1.0)));
    }


}
