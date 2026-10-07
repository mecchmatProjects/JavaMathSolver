package solver.geometry;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointTest {

    @Test
    void ofStoresXAndYInTheRightFields() {
        Point p = Point.of(3.5, -2.25);
        assertEquals(3.5, p.x);
        assertEquals(-2.25, p.y);
    }

    @Test
    void ofKeepsNaNInfinityAndNegativeZero() {
        Point p = Point.of(Double.NaN, Double.POSITIVE_INFINITY);
        assertTrue(Double.isNaN(p.x));
        assertEquals(Double.POSITIVE_INFINITY, p.y);
        assertEquals(Double.doubleToRawLongBits(-0.0), Double.doubleToRawLongBits(Point.of(0, -0.0).y));
    }

    @Test
    void ofAcceptsExtremeMagnitudes() {
        Point p = Point.of(Double.MAX_VALUE, -Double.MIN_VALUE);
        assertEquals(Double.MAX_VALUE, p.x);
        assertEquals(-Double.MIN_VALUE, p.y);
    }

    @Test
    void ofDoesNotRoundCoordinates() {
        Point p = Point.of(0.1 + 0.2, 1.0 / 3);
        assertEquals(0.30000000000000004, p.x);
        assertEquals(1.0 / 3, p.y);
    }

    @Test
    void ofCreatesIndependentInstancesComparedByIdentity() {
        Point a = Point.of(1, 2);
        Point b = Point.of(1, 2);
        assertNotSame(a, b);
        assertNotEquals(a, b); 
        a.x = 10;
        assertEquals(1.0, b.x);
        assertEquals(2.0, a.y);
    }
}
