package solver.geometry;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointTest {

    private static final double EPS = 1e-9;

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
        assertEquals(b, a);
        assertEquals(1.0, a.x);
        assertEquals(1.0, b.x);
        assertEquals(2.0, a.y);
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
        // Double.compare treats NaN as equal to NaN, so equals stays reflexive
        Point a = Point.of(Double.NaN, 0);
        Point b = Point.of(Double.NaN, 0);
        assertEquals(a, b);
    }

    @Test
    void pointZeroAndNegativeZeroDiffer() {
        assertNotEquals(Point.of(0.0, 0), Point.of(-0.0, 0));
    }

    @Test
    void toStringShowsFields() {
        assertEquals("Point(1.0, 2.0)", Point.of(1, 2).toString());
    }

    // ---------- operations ----------

    @Test
    void distanceToUsesPythagoras() {
        assertEquals(5.0, Point.of(0, 0).distanceTo(Point.of(3, 4)), EPS);
        assertEquals(5.0, Point.of(3, 4).distanceTo(Point.of(0, 0)), EPS);
    }

    @Test
    void distanceToSelfIsZero() {
        Point p = Point.of(-2.5, 7);
        assertEquals(0.0, p.distanceTo(p));
    }

    @Test
    void vectorToGivesDifference() {
        assertEquals(Vector2D.of(3, -4), Point.of(1, 2).vectorTo(Point.of(4, -2)));
    }

    @Test
    void translateMovesByVector() {
        assertEquals(Point.of(3, -2), Point.of(1, 1).translate(Vector2D.of(2, -3)));
    }

    @Test
    void midpointIsAverage() {
        assertEquals(Point.of(2, 2), Point.of(-2, 4).midpoint(Point.of(6, 0)));
    }

    @Test
    void approxEqualsRespectsTolerance() {
        assertTrue(Point.of(1, 1).approxEquals(Point.of(1 + 1e-12, 1), EPS));
        assertFalse(Point.of(1, 1).approxEquals(Point.of(1.1, 1), EPS));
    }
}
