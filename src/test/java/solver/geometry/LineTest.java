package solver.geometry;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LineTest {

    private static final double EPS = 1e-9;

    @Test
    void throughSamePointThrows() {
        assertThrows(IllegalArgumentException.class, () -> Line.through(Point.of(1, 1), Point.of(1, 1)));
    }

    @Test
    void zeroDirectionThrows() {
        assertThrows(IllegalArgumentException.class, () -> Line.of(Point.of(0, 0), Vector2D.ZERO));
    }

    @Test
    void fromEquationWithZeroNormalThrows() {
        assertThrows(IllegalArgumentException.class, () -> Line.fromEquation(0, 0, 5));
    }

    @Test
    void fromEquationContainsSolutions() {
        Line l = Line.fromEquation(2, 3, -6);
        assertTrue(l.contains(Point.of(3, 0)));
        assertTrue(l.contains(Point.of(0, 2)));
        assertFalse(l.contains(Point.of(1, 1)));
    }

    @Test
    void coefficientsDescribeTheLine() {
        Line l = Line.through(Point.of(0, 1), Point.of(1, 2)); 
        double[] abc = l.coefficients();
        assertEquals(0.0, abc[0] * 5 + abc[1] * 6 + abc[2], EPS);
        assertEquals(1.0, Math.hypot(abc[0], abc[1]), EPS);
    }

    @Test
    void slope() {
        assertEquals(2.0, Line.through(Point.of(0, 0), Point.of(1, 2)).slope(), EPS);
        assertEquals(0.0, Line.through(Point.of(0, 3), Point.of(5, 3)).slope(), EPS);
        assertEquals(Double.POSITIVE_INFINITY, Line.through(Point.of(2, 0), Point.of(2, 5)).slope());
    }

    @Test
    void containsPointsOnLine() {
        Line l = Line.through(Point.of(0, 0), Point.of(2, 2));
        assertTrue(l.contains(Point.of(-5, -5)));
        assertTrue(l.contains(Point.of(100, 100)));
        assertFalse(l.contains(Point.of(1, 2)));
    }

    @Test
    void distanceToPoint() {
        Line xAxis = Line.through(Point.of(0, 0), Point.of(1, 0));
        assertEquals(3.0, xAxis.distanceTo(Point.of(7, -3)), EPS);
        Line diag = Line.through(Point.of(0, 0), Point.of(1, 1));
        assertEquals(Math.sqrt(2), diag.distanceTo(Point.of(2, 0)), EPS);
    }

    @Test
    void projection() {
        Line diag = Line.through(Point.of(0, 0), Point.of(1, 1));
        Point p = diag.projection(Point.of(2, 0));
        assertEquals(1.0, p.x, EPS);
        assertEquals(1.0, p.y, EPS);
    }

    @Test
    void intersectionOfCrossingLines() {
        Line a = Line.through(Point.of(0, 0), Point.of(2, 2));
        Line b = Line.through(Point.of(0, 2), Point.of(2, 0));
        Point p = a.intersection(b);
        assertNotNull(p);
        assertEquals(1.0, p.x, EPS);
        assertEquals(1.0, p.y, EPS);
    }

    @Test
    void intersectionWithVerticalLine() {
        Line v = Line.through(Point.of(3, -1), Point.of(3, 10));
        Line h = Line.fromEquation(0, 1, -4); 
        Point p = v.intersection(h);
        assertEquals(3.0, p.x, EPS);
        assertEquals(4.0, p.y, EPS);
    }

    @Test
    void parallelLinesHaveNoIntersection() {
        Line a = Line.through(Point.of(0, 0), Point.of(1, 1));
        Line b = Line.through(Point.of(0, 1), Point.of(1, 2));
        assertTrue(a.isParallelTo(b));
        assertNull(a.intersection(b));
        assertEquals(Math.sqrt(2) / 2, a.distanceTo(b), EPS);
    }

    @Test
    void coincidentLines() {
        Line a = Line.through(Point.of(0, 0), Point.of(1, 1));
        Line b = Line.through(Point.of(5, 5), Point.of(-3, -3));
        assertTrue(a.coincidesWith(b));
        assertNull(a.intersection(b));
        assertEquals(0.0, a.distanceTo(b), EPS);
    }

    @Test
    void crossingLinesHaveZeroDistance() {
        Line a = Line.through(Point.of(0, 0), Point.of(1, 0));
        Line b = Line.through(Point.of(0, 0), Point.of(0, 1));
        assertEquals(0.0, a.distanceTo(b));
    }

    @Test
    void perpendicular() {
        Line a = Line.through(Point.of(0, 0), Point.of(1, 2));
        Line b = Line.through(Point.of(0, 0), Point.of(-2, 1));
        assertTrue(a.isPerpendicularTo(b));
        assertFalse(a.isPerpendicularTo(a));
    }

    @Test
    void linesBuiltFromSamePointsAreEqual() {
        Line a = Line.through(Point.of(0, 0), Point.of(1, 2));
        Line b = Line.through(Point.of(0, 0), Point.of(1, 2));
        assertEquals(a, b);
    }

    @Test
    void throughAndOfGiveEqualLines() {
        Line a = Line.through(Point.of(1, 1), Point.of(4, 5));
        Line b = Line.of(Point.of(1, 1), Vector2D.of(3, 4));
        assertEquals(a, b);
    }

    @Test
    void sameLineWithDifferentRepresentationIsNotEqualButCoincides() {
        Line a = Line.through(Point.of(0, 0), Point.of(1, 1));
        Line b = Line.through(Point.of(2, 2), Point.of(5, 5));
        assertNotEquals(a, b);
        assertTrue(a.coincidesWith(b));
    }

    @Test
    void differentLinesAreNotEqual() {
        Line a = Line.through(Point.of(0, 0), Point.of(1, 1));
        assertNotEquals(a, Line.through(Point.of(0, 0), Point.of(1, 2)));
        assertNotEquals(a, Line.through(Point.of(0, 1), Point.of(1, 2)));
        assertFalse(a.equals(null));
        assertFalse(a.equals(Point.of(0, 0)));
    }

    @Test
    void toStringShowsFields() {
        assertEquals("Line(Point(0.0, 0.0), dir=Vector2D(1.0, 1.0))",
                Line.through(Point.of(0, 0), Point.of(1, 1)).toString());
    }
}
