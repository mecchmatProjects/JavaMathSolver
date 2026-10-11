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

        assertEquals(2.0, res.get(0, 0), EPSILON);
        assertEquals(4.0, res.get(0, 1), EPSILON);
        assertEquals(6.0, res.get(1, 0), EPSILON);
        assertEquals(8.0, res.get(1, 1), EPSILON);
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
        assertEquals(4.0, product.get(0, 0), EPSILON);
        assertEquals(4.0, product.get(0, 1), EPSILON);
        assertEquals(10.0, product.get(1, 0), EPSILON);
        assertEquals(8.0, product.get(1, 1), EPSILON);
    }

    @Test
    void testDeterminant() {
        double[][] data = {
                {6.0, 1.0, 1.0},
                {4.0, -2.0, 5.0},
                {2.0, 8.0, 7.0}
        };
        Matrix m = new Matrix(data);
        assertEquals(-306.0, m.determinant(), EPSILON);
    }

    @Test
    void testNorm2Basic() {
        double[][] data = {
                {1.0, 2.0},
                {2.0, 4.0}
        };
        Matrix m = new Matrix(data);
        assertEquals(5.0, m.norm2(), EPSILON);
    }

    @Test
    void testNorm2WithNegativeValues() {
        double[][] data = {
                {-3.0, 0.0},
                {0.0, 4.0}
        };
        Matrix m = new Matrix(data);
        assertEquals(5.0, m.norm2(), EPSILON);
    }

    @Test
    void testNormWithP1() {
        double[][] data = {
                {-1.0, 2.0},
                {-3.0, 4.0}
        };
        Matrix m = new Matrix(data);
        assertEquals(10.0, m.norm(1.0), EPSILON);
    }

    @Test
    void testNormConsistencyWithNorm2() {
        double[][] data = {
                {1.5, -2.5},
                {3.0, 4.2}
        };
        Matrix m = new Matrix(data);
        assertEquals(m.norm2(), m.norm(2.0), EPSILON);
    }

    @Test
    void testNormWithP3() {
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
        double[][] data = {
                {2.0, -9.5},
                {7.1, 4.0}
        };
        Matrix m = new Matrix(data);
        assertEquals(9.5, m.norm(Double.POSITIVE_INFINITY), EPSILON);
    }

    @Test
    void testNormInvalidP() {
        double[][] data = {
                {1.0, 2.0},
                {3.0, 4.0}
        };
        Matrix m = new Matrix(data);
        assertThrows(IllegalArgumentException.class, () -> m.norm(0.5));
        assertThrows(IllegalArgumentException.class, () -> m.norm(-1.0));
    }

    @Test
    void testStandardTraceSquareMatrix() {
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
        double[][] data = {
                {1.0, 2.0, 3.0},
                {4.0, 5.0, 6.0}
        };
        Matrix m = new Matrix(data);
        assertEquals(6.0, m.trace(), EPSILON);
    }

    @Test
    void testTraceSuperDiagonal() {
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
        double[][] data = {
                {1.0, 2.0},
                {3.0, 4.0}
        };
        Matrix m = new Matrix(data);
        assertEquals(5.0, m.tracePower(1), EPSILON);
    }

    @Test
    void testTracePowerTwo() {
        double[][] data = {
                {1.0, 2.0},
                {3.0, 4.0}
        };
        Matrix m = new Matrix(data);
        assertEquals(29.0, m.tracePower(2), EPSILON);
    }

    @Test
    void testTracePowerThree() {
        double[][] data = {
                {1.0, 2.0},
                {3.0, 4.0}
        };
        Matrix m = new Matrix(data);
        assertEquals(155.0, m.tracePower(3), EPSILON);
    }

    @Test
    void testTracePowerInvalidInputs() {
        double[][] nonSquare = {
                {1.0, 2.0, 3.0},
                {4.0, 5.0, 6.0}
        };
        Matrix mNonSquare = new Matrix(nonSquare);
        assertThrows(IllegalArgumentException.class, () -> mNonSquare.tracePower(2));

        double[][] square = {
                {1.0, 2.0},
                {3.0, 4.0}
        };
        Matrix mSquare = new Matrix(square);
        assertThrows(IllegalArgumentException.class, () -> mSquare.tracePower(-1));
    }

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
    void testSingularMatrixThrowsException() {
        double[][] data = {
                {1.0, 2.0},
                {2.0, 4.0}
        };
        Matrix a = new Matrix(data);
        assertThrows(ArithmeticException.class, a::inverse);
    }

    @Test
    void testNonSquareMatrixThrowsException() {
        double[][] data = {
                {1.0, 2.0, 3.0},
                {4.0, 5.0, 6.0}
        };
        Matrix a = new Matrix(data);
        assertThrows(IllegalArgumentException.class, a::inverse);
    }

    private void assertSystemSolution(Matrix a, double[] x, double[] b) {
        assertNotNull(x, "Розв'язок x не повинен бути null");
        int n = a.getRows();
        for (int i = 0; i < n; i++) {
            double actualB_i = 0.0;
            for (int j = 0; j < n; j++) {
                actualB_i += a.get(i, j) * x[j];
            }
            assertEquals(b[i], actualB_i, EPSILON,
                    "Невідповідність у рівнянні номер " + i);
        }
    }

    @Test
    void testSolve2x2Exact() {
        double[][] aData = {
                {2.0, 1.0},
                {1.0, 3.0}
        };
        double[] b = {5.0, 5.0};

        Matrix a = new Matrix(aData);
        double[] x = a.solve(b);

        assertNotNull(x);
        assertEquals(2, x.length);
        assertEquals(2.0, x[0], EPSILON);
        assertEquals(1.0, x[1], EPSILON);
    }

    @Test
    void testSolve3x3WithVerification() {
        double[][] aData = {
                { 2.0, -1.0,  0.0},
                {-1.0,  2.0, -1.0},
                { 0.0, -1.0,  2.0}
        };
        double[] b = {1.0, 2.0, 3.0};

        Matrix a = new Matrix(aData);
        double[] x = a.solve(b);

        assertSystemSolution(a, x, b);
    }

    @Test
    void testSolveRequiresPivoting() {
        double[][] aData = {
                {0.0, 2.0},
                {3.0, -2.0}
        };
        double[] b = {4.0, 2.0};

        Matrix a = new Matrix(aData);
        double[] x = a.solve(b);

        assertNotNull(x);
        assertEquals(2.0, x[0], EPSILON);
        assertEquals(2.0, x[1], EPSILON);
    }

    @Test
    void testSolveSingularMatrixThrowsException() {
        double[][] aData = {
                {1.0, 2.0},
                {2.0, 4.0}
        };
        double[] b = {3.0, 6.0};

        Matrix a = new Matrix(aData);
        assertThrows(ArithmeticException.class, () -> a.solve(b));
    }

    @Test
    void testSolveNonSquareMatrixThrowsException() {
        double[][] aData = {
                {1.0, 2.0, 3.0},
                {4.0, 5.0, 6.0}
        };
        double[] b = {1.0, 2.0};

        Matrix a = new Matrix(aData);
        assertThrows(IllegalArgumentException.class, () -> a.solve(b));
    }

    @Test
    void testSolveDimensionMismatchThrowsException() {
        double[][] aData = {
                {2.0, 1.0},
                {1.0, 3.0}
        };
        double[] b = {1.0, 2.0, 3.0};

        Matrix a = new Matrix(aData);
        assertThrows(IllegalArgumentException.class, () -> a.solve(b));
    }

    @Test
    void testSolveNullVectorThrowsException() {
        double[][] aData = {
                {1.0, 0.0},
                {0.0, 1.0}
        };
        Matrix a = new Matrix(aData);
        assertThrows(IllegalArgumentException.class, () -> a.solve(null));
    }

    private void sort(double[] arr) {
        if (arr == null) return;
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    double temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }

    @Test
    void testDiagonalMatrix() {
        double[][] data = {
                {5.0,  0.0, 0.0},
                {0.0, -2.0, 0.0},
                {0.0,  0.0, 7.0}
        };
        Matrix m = new Matrix(data);
        double[] ev = m.realEigenvals();

        assertNotNull(ev);
        assertEquals(3, ev.length);

        sort(ev);
        assertEquals(-2.0, ev[0], EPSILON);
        assertEquals(5.0, ev[1], EPSILON);
        assertEquals(7.0, ev[2], EPSILON);
    }

    @Test
    void testTriangularMatrix() {
        double[][] data = {
                {1.0, 4.0, 5.0},
                {0.0, 2.0, 6.0},
                {0.0, 0.0, 3.0}
        };
        Matrix m = new Matrix(data);
        double[] ev = m.realEigenvals();

        assertNotNull(ev);
        assertEquals(3, ev.length);

        sort(ev);
        assertEquals(1.0, ev[0], EPSILON);
        assertEquals(2.0, ev[1], EPSILON);
        assertEquals(3.0, ev[2], EPSILON);
    }

    @Test
    void testSymmetric2x2() {
        double[][] data = {
                {2.0, 1.0},
                {1.0, 2.0}
        };
        Matrix m = new Matrix(data);
        double[] ev = m.realEigenvals();

        assertNotNull(ev);
        assertEquals(2, ev.length);

        sort(ev);
        assertEquals(1.0, ev[0], EPSILON);
        assertEquals(3.0, ev[1], EPSILON);
    }

    @Test
    void testGeneral3x3ViaTraceAndDeterminant() {
        double[][] data = {
                {3.0, 2.0, 4.0},
                {2.0, 0.0, 2.0},
                {4.0, 2.0, 3.0}
        };
        Matrix m = new Matrix(data);
        double[] ev = m.realEigenvals();

        assertNotNull(ev);
        assertEquals(3, ev.length);

        sort(ev);
        assertEquals(-1.0, ev[0], EPSILON);
        assertEquals(-1.0, ev[1], EPSILON);
        assertEquals(8.0, ev[2], EPSILON);

        double sum = ev[0] + ev[1] + ev[2];
        double prod = ev[0] * ev[1] * ev[2];
        assertEquals(m.trace(), sum, EPSILON);
        assertEquals(8.0, prod, EPSILON);
    }

    @Test
    void testComplexConjugateEigenvaluesOmitted() {
        double[][] data = {
                {0.0, -1.0},
                {1.0,  0.0}
        };
        Matrix m = new Matrix(data);
        double[] ev = m.realEigenvals();

        assertNotNull(ev);
        assertEquals(0, ev.length);
    }

    @Test
    void testMixedRealAndComplexEigenvalues() {
        double[][] data = {
                {5.0, 0.0,  0.0},
                {0.0, 0.0, -1.0},
                {0.0, 1.0,  0.0}
        };
        Matrix m = new Matrix(data);
        double[] ev = m.realEigenvals();

        assertNotNull(ev);
        assertEquals(1, ev.length);
        assertEquals(5.0, ev[0], EPSILON);
    }

    @Test
    void testEigenvaluesNonSquareMatrixThrowsException() {
        double[][] data = {
                {1.0, 2.0, 3.0},
                {4.0, 5.0, 6.0}
        };
        Matrix m = new Matrix(data);
        assertThrows(IllegalArgumentException.class, m::realEigenvals);
    }
}
