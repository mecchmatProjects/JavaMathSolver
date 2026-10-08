package solver.algebra;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NumericArrayTest {

    private static final double EPSILON = 0.0001;

    @Test
    void testEmptyArray() {
        NumericArray a = new NumericArray(new double[]{});

        assertEquals(0, a.size());
        assertArrayEquals(new double[]{}, a.toArray());
        assertEquals(0, a.normalize().size());

        assertThrows(IllegalStateException.class, () -> a.min());
        assertThrows(IllegalStateException.class, () -> a.max());
    }

    @Test
    void testSingleElement() {
        NumericArray a = new NumericArray(new double[]{5});

        assertEquals(1, a.size());
        assertEquals(5.0, a.get(0), EPSILON);
        assertEquals(5.0, a.min(), EPSILON);
        assertEquals(5.0, a.max(), EPSILON);

        assertArrayEquals(
                new double[]{0.0},
                a.normalize().toArray(),
                EPSILON
        );
    }

    @Test
    void testNegativeNumbers() {
        NumericArray a = new NumericArray(
                new double[]{-5, -10, -3}
        );

        assertEquals(3, a.size());
        assertEquals(-10.0, a.min(), EPSILON);
        assertEquals(-3.0, a.max(), EPSILON);

        assertArrayEquals(
                new double[]{5.0 / 7.0, 0.0, 1.0},
                a.normalize().toArray(),
                EPSILON
        );
    }

    @Test
    void testSameValues() {
        NumericArray a = new NumericArray(
                new double[]{7, 7, 7}
        );

        assertEquals(3, a.size());
        assertEquals(7.0, a.min(), EPSILON);
        assertEquals(7.0, a.max(), EPSILON);

        assertArrayEquals(
                new double[]{0.0, 0.0, 0.0},
                a.normalize().toArray(),
                EPSILON
        );
    }

    @Test
    void testNormalArray() {
        NumericArray a = new NumericArray(
                new double[]{10, 20, 30}
        );

        assertEquals(3, a.size());
        assertEquals(10.0, a.get(0), EPSILON);
        assertEquals(20.0, a.get(1), EPSILON);
        assertEquals(30.0, a.get(2), EPSILON);

        assertEquals(10.0, a.min(), EPSILON);
        assertEquals(30.0, a.max(), EPSILON);

        assertArrayEquals(
                new double[]{0.0, 0.5, 1.0},
                a.normalize().toArray(),
                EPSILON
        );
    }

    @Test
    void testInvalidInput() {
        assertThrows(IllegalArgumentException.class,
                () -> new NumericArray(null));

        assertThrows(IllegalArgumentException.class,
                () -> new NumericArray(
                        new double[]{Double.NaN}
                ));

        assertThrows(IllegalArgumentException.class,
                () -> new NumericArray(
                        new double[]{Double.POSITIVE_INFINITY}
                ));
    }

    @Test
    void testArrayCopy() {
        double[] data = {10, 20, 30};
        NumericArray a = new NumericArray(data);

        data[0] = 100;

        assertEquals(10.0, a.get(0), EPSILON);

        double[] copy = a.toArray();
        copy[1] = 200;

        assertEquals(20.0, a.get(1), EPSILON);
    }
}
