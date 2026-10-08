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


    @Test
    void testStandardTraceSquareMatrix() {
        // tr(A) = 5 + (-3) + 8 = 10.0
        double[][] data = {
                { 5.0,  1.0,  2.0},
                { 0.0, -3.0,  4.0},
                { 1.0,  2.0,  8.0}
        };
        Matrix m = new Matrix(data);
        assertEquals(10.0, m.trace(), EPSILON);
    }

    @Test
    void testStandardTraceRectangularMatrix() {
        // Для прямокутної 2x3 береться min(2, 3): a[0][0] + a[1][1] = 1.0 + 5.0 = 6.0
        double[][] data = {
                {1.0, 2.0, 3.0},
                {4.0, 5.0, 6.0}
        };
        Matrix m = new Matrix(data);
        assertEquals(6.0, m.trace(), EPSILON);
    }

    @Test
    void testTraceSuperDiagonal() {
        // offset = +1 (перша наддіагональ): a[0][1] + a[1][2] = 2.0 + 6.0 = 8.0
        double[][] data = {
                {1.0, 2.0, 3.0},
                {4.0, 5.0, 6.0},
                {7.0, 8.0, 9.0}
        };
        Matrix m = new Matrix(data);
        assertEquals(8.0, m.trace(1), EPSILON);
    }

    @Test
    void testTraceSubDiagonal() {
        // offset = -1 (перша піддіагональ): a[1][0] + a[2][1] = 4.0 + 8.0 = 12.0
        double[][] data = {
                {1.0, 2.0, 3.0},
                {4.0, 5.0, 6.0},
                {7.0, 8.0, 9.0}
        };
        Matrix m = new Matrix(data);
        assertEquals(12.0, m.trace(-1), EPSILON);
    }

    @Test
    void testTraceCornerElements() {
        // offset = 2 (верхній правий кут a[0][2] = 3.0)
        // offset = -2 (нижній лівий кут a[2][0] = 7.0)
        double[][] data = {
                {1.0, 2.0, 3.0},
                {4.0, 5.0, 6.0},
                {7.0, 8.0, 9.0}
        };
        Matrix m = new Matrix(data);
        assertEquals(3.0, m.trace(2), EPSILON);
        assertEquals(7.0, m.trace(-2), EPSILON);
    }

    @Test
    void testTraceOffsetOutOfBounds() {
        // Вихід за межі матриці повинен повертати 0.0
        double[][] data = {
                {1.0, 2.0},
                {3.0, 4.0}
        };
        Matrix m = new Matrix(data);
        assertEquals(0.0, m.trace(2), EPSILON);
        assertEquals(0.0, m.trace(-2), EPSILON);
        assertEquals(0.0, m.trace(10), EPSILON);
    }

    @Test
    void testTracePowerZero() {
        // tr(A^0) = tr(I_n) = n = 3
        double[][] data = {
                {2.0, 1.0, 0.0},
                {0.0, 3.0, 1.0},
                {1.0, 0.0, 2.0}
        };
        Matrix m = new Matrix(data);
        assertEquals(3.0, m.tracePower(0), EPSILON);
    }

    @Test
    void testTracePowerOne() {
        // tr(A^1) = tr(A) = 1.0 + 4.0 = 5.0
        double[][] data = {
                {1.0, 2.0},
                {3.0, 4.0}
        };
        Matrix m = new Matrix(data);
        assertEquals(5.0, m.tracePower(1), EPSILON);
    }

    @Test
    void testTracePowerTwo() {
        // A = [[1, 2], [3, 4]]
        // A^2 = [[7, 10], [15, 22]] -> tr(A^2) = 7 + 22 = 29.0
        // Формула оптимізації: 1*1 + 2*3 + 3*2 + 4*4 = 1 + 6 + 6 + 16 = 29.0
        double[][] data = {
                {1.0, 2.0},
                {3.0, 4.0}
        };
        Matrix m = new Matrix(data);
        assertEquals(29.0, m.tracePower(2), EPSILON);
    }

    @Test
    void testTracePowerThree() {
        // A = [[1, 2], [3, 4]]
        // A^3 = [[37, 54], [81, 118]] -> tr(A^3) = 37 + 118 = 155.0
        double[][] data = {
                {1.0, 2.0},
                {3.0, 4.0}
        };
        Matrix m = new Matrix(data);
        assertEquals(155.0, m.tracePower(3), EPSILON);
    }

    @Test
    void testTracePowerInvalidInputs() {
        // Непрямокутна матриця повинна повертати Double.NaN
        double[][] nonSquare = {
                {1.0, 2.0, 3.0},
                {4.0, 5.0, 6.0}
        };
        Matrix mNonSquare = new Matrix(nonSquare);
        assertTrue(Double.isNaN(mNonSquare.tracePower(2)));

        // Від'ємний степінь повинен повертати Double.NaN
        double[][] square = {
                {1.0, 2.0},
                {3.0, 4.0}
        };
        Matrix mSquare = new Matrix(square);
        assertTrue(Double.isNaN(mSquare.tracePower(-1)));
    }


    // Допоміжний метод для перевірки, чи є матриця одиничною (Identity Matrix)
    private void assertIdentity(Matrix matrix, int size) {
        assertNotNull(matrix, "Матриця не повинна бути null");
        assertEquals(size, matrix.getRows());
        assertEquals(size, matrix.getCols());

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                double expected = (i == j) ? 1.0 : 0.0;
                assertEquals(expected, matrix.get(i, j), EPSILON,
                        "Розбіжність у позиції [" + i + "][" + j + "]");
            }
        }
    }

    @Test
    void testInverse2x2ExplicitValues() {
        // A = [[4, 7], [2, 6]], det(A) = 24 - 14 = 10
        // A^(-1) = 1/10 * [[6, -7], [-2, 4]] = [[0.6, -0.7], [-0.2, 0.4]]
        double[][] data = {
                {4.0, 7.0},
                {2.0, 6.0}
        };
        Matrix a = new Matrix(data);
        Matrix inv = a.inverse();

        assertNotNull(inv);
        assertEquals(0.6, inv.get(0, 0), EPSILON);
        assertEquals(-0.7, inv.get(0, 1), EPSILON);
        assertEquals(-0.2, inv.get(1, 0), EPSILON);
        assertEquals(0.4, inv.get(1, 1), EPSILON);
    }

    @Test
    void testInverseIdentityProperty3x3() {
        // Перевірка фундаментальної алгебраїчної рівності: A * A^(-1) = I
        double[][] data = {
                { 2.0, -1.0,  0.0},
                {-1.0,  2.0, -1.0},
                { 0.0, -1.0,  2.0}
        };
        Matrix a = new Matrix(data);
        Matrix inv = a.inverse();

        assertNotNull(inv);
        Matrix product = a.multiplyMatrix(inv);
        assertIdentity(product, 3);
    }

    @Test
    void testInverseWithPivotingRequired() {
        // Перший елемент a[0][0] = 0, метод зобов'язаний виконати pivoting (обмін рядків)
        double[][] data = {
                {0.0, 1.0, 2.0},
                {1.0, 0.0, 3.0},
                {4.0, -3.0, 8.0}
        };
        Matrix a = new Matrix(data);
        Matrix inv = a.inverse();

        assertNotNull(inv);
        Matrix product = a.multiplyMatrix(inv);
        assertIdentity(product, 3);
    }

    @Test
    void testInverseIdentityMatrix() {
        // Обернена до одиничної є самою одиничною матрицею: I^(-1) = I
        double[][] data = {
                {1.0, 0.0, 0.0},
                {0.0, 1.0, 0.0},
                {0.0, 0.0, 1.0}
        };
        Matrix i = new Matrix(data);
        Matrix inv = i.inverse();

        assertNotNull(inv);
        assertIdentity(inv, 3);
    }

    @Test
    void testSingularMatrixReturnsNull() {
        // Рядки лінійно залежні (другий рядок удвічі більший за перший) -> det = 0
        double[][] data = {
                {1.0, 2.0},
                {2.0, 4.0}
        };
        Matrix a = new Matrix(data);
        Matrix inv = a.inverse();

        // Метод має коректно вивести помилку в консоль і повернути null
        assertNull(inv, "Обернена матриця для виродженої матриці має повертати null");
    }

    @Test
    void testNonSquareMatrixReturnsNull() {
        // Прямокутна матриця 2x3
        double[][] data = {
                {1.0, 2.0, 3.0},
                {4.0, 5.0, 6.0}
        };
        Matrix a = new Matrix(data);
        Matrix inv = a.inverse();

        assertNull(inv, "Обернена матриця не існує для неквадратних матриць");
    }


}
