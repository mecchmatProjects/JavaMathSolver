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
}
