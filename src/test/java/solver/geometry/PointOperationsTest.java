package solver.geometry;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointOperationsTest {

    private static final double EPS = 1e-9;

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
        Vector2D v = Point.of(1, 2).vectorTo(Point.of(4, -2));
        assertEquals(3.0, v.x, EPS);
        assertEquals(-4.0, v.y, EPS);
    }

    @Test
    void translateMovesByVector() {
        Point p = Point.of(1, 1).translate(Vector2D.of(2, -3));
        assertEquals(3.0, p.x, EPS);
        assertEquals(-2.0, p.y, EPS);
    }

    @Test
    void midpointIsAverage() {
        Point m = Point.of(-2, 4).midpoint(Point.of(6, 0));
        assertEquals(2.0, m.x, EPS);
        assertEquals(2.0, m.y, EPS);
    }

    @Test
    void approxEqualsRespectsTolerance() {
        assertTrue(Point.of(1, 1).approxEquals(Point.of(1 + 1e-12, 1), EPS));
        assertFalse(Point.of(1, 1).approxEquals(Point.of(1.1, 1), EPS));
    }
}
