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
    void ofCreatesIndependentButEqualInstances() {
        Point a = Point.of(1, 2);
        Point b = Point.of(1, 2);
        assertNotSame(a, b);
        assertEquals(a, b);
        assertEquals(1.0, a.x);
        assertEquals(1.0, b.x);
        assertEquals(2.0, a.y);
    }

    @Test
    void pointsWithSameCoordinatesAreEqual() {
        Point a = Point.of(1.5, -2);
        Point b = Point.of(1.5, -2);
        assertNotSame(a, b);
        assertEquals(a, b);
        assertEquals(b, a);
    }

    @Test
    void pointsWithDifferentCoordinatesAreNotEqual() {
        assertNotEquals(Point.of(1, 2), Point.of(2, 1));
        assertNotEquals(Point.of(1, 2), Point.of(1, 2.0000001));
    }

    @Test
    void pointIsEqualToItself() {
        Point p = Point.of(3, 4);
        assertEquals(p, p);
    }

    @Test
    void pointIsNotEqualToNullOrOtherType() {
        Point p = Point.of(3, 4);
        assertNotEquals(null, p);
        assertFalse(p.equals(null));
        assertFalse(p.equals(Vector2D.of(3, 4)));
        assertFalse(p.equals("Point(3.0, 4.0)"));
    }

    @Test
    void pointEqualityIsTransitive() {
        Point a = Point.of(7, 8);
        Point b = Point.of(7, 8);
        Point c = Point.of(7, 8);
        assertEquals(a, b);
        assertEquals(b, c);
        assertEquals(a, c);
    }

    @Test
    void pointNaNEqualsNaN() {
        // Double.compare вважає NaN рівним NaN, тож equals лишається рефлексивним
        Point a = Point.of(Double.NaN, 0);
        Point b = Point.of(Double.NaN, 0);
        assertEquals(a, b);
    }

    @Test
    void pointZeroAndNegativeZeroDiffer() {
        assertNotEquals(Point.of(0.0, 0), Point.of(-0.0, 0));
    }

    @Test
    void computedPointEqualsExpected() {
        assertEquals(Point.of(2, 2), Point.of(-2, 4).midpoint(Point.of(6, 0)));
        assertEquals(Point.of(3, -2), Point.of(1, 1).translate(Vector2D.of(2, -3)));
    }

    @Test
    void toStringShowsFields() {
        assertEquals("Point(1.0, 2.0)", Point.of(1, 2).toString());
    }
}
