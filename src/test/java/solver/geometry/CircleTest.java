package solver.geometry;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

class CircleTest {

    private static final double EPS = 1e-9;

    private static Point[] sorted(Point[] pts) {
        Point[] copy = pts.clone();
        Arrays.sort(copy, Comparator.<Point>comparingDouble(p -> p.x).thenComparingDouble(p -> p.y));
        return copy;
    }

    private static void assertPoint(double x, double y, Point p) {
        assertEquals(x, p.x, 1e-7, "x");
        assertEquals(y, p.y, 1e-7, "y");
    }

    @Test
    void negativeOrNaNRadiusThrows() {
        assertThrows(IllegalArgumentException.class, () -> Circle.of(0, 0, -1));
        assertThrows(IllegalArgumentException.class, () -> Circle.of(0, 0, Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> Circle.of(0, 0, Double.POSITIVE_INFINITY));
    }

    @Test
    void areaCircumferenceDiameter() {
        Circle c = Circle.of(1, 1, 2);
        assertEquals(4 * Math.PI, c.area(), EPS);
        assertEquals(4 * Math.PI, c.circumference(), EPS);
        assertEquals(4.0, c.diameter(), EPS);
        assertEquals(0.0, Circle.of(0, 0, 0).area());
    }

    @Test
    void containsInsideBoundaryOutside() {
        Circle c = Circle.of(0, 0, 5);
        assertTrue(c.contains(Point.of(0, 0)));
        assertTrue(c.contains(Point.of(3, 4)));   
        assertFalse(c.contains(Point.of(4, 4)));
    }

    @Test
    void onBoundary() {
        Circle c = Circle.of(0, 0, 5);
        assertTrue(c.onBoundary(Point.of(-3, 4)));
        assertFalse(c.onBoundary(Point.of(1, 1)));
    }

    @Test
    void distanceToPoint() {
        Circle c = Circle.of(0, 0, 5);
        assertEquals(5.0, c.distanceTo(Point.of(6, 8)), EPS);
        assertEquals(0.0, c.distanceTo(Point.of(1, 1)), EPS);
        assertEquals(0.0, c.distanceTo(Point.of(5, 0)), EPS);
    }

    @Test
    void distanceToBoundary() {
        Circle c = Circle.of(0, 0, 5);
        assertEquals(5.0, c.distanceToBoundary(Point.of(0, 0)), EPS);
        assertEquals(5.0, c.distanceToBoundary(Point.of(6, 8)), EPS);
    }

    @Test
    void twoIntersectionPoints() {
        Circle a = Circle.of(0, 0, 5);
        Circle b = Circle.of(8, 0, 5);
        assertTrue(a.intersects(b));
        Point[] pts = sorted(a.intersection(b));
        assertEquals(2, pts.length);
        assertPoint(4, -3, pts[0]);
        assertPoint(4, 3, pts[1]);
        for (Point p : pts) {
            assertTrue(a.onBoundary(p));
            assertTrue(b.onBoundary(p));
        }
    }

    @Test
    void intersectionIsSymmetric() {
        Circle a = Circle.of(1, 2, 3);
        Circle b = Circle.of(3, -1, 2.5);
        Point[] ab = sorted(a.intersection(b));
        Point[] ba = sorted(b.intersection(a));
        assertEquals(2, ab.length);
        for (int i = 0; i < 2; i++) {
            assertPoint(ab[i].x, ab[i].y, ba[i]);
        }
    }

    @Test
    void externalTangency() {
        Point[] pts = Circle.of(0, 0, 2).intersection(Circle.of(5, 0, 3));
        assertEquals(1, pts.length);
        assertPoint(2, 0, pts[0]);
    }

    @Test
    void internalTangency() {
        Point[] pts = Circle.of(0, 0, 5).intersection(Circle.of(2, 0, 3));
        assertEquals(1, pts.length);
        assertPoint(5, 0, pts[0]);
    }

    @Test
    void separateCirclesDoNotIntersect() {
        Circle a = Circle.of(0, 0, 1);
        Circle b = Circle.of(10, 0, 1);
        assertFalse(a.intersects(b));
        assertEquals(0, a.intersection(b).length);
    }

    @Test
    void nestedCirclesDoNotIntersect() {
        Circle a = Circle.of(0, 0, 10);
        Circle b = Circle.of(1, 0, 2);
        assertFalse(a.intersects(b));
        assertEquals(0, a.intersection(b).length);
    }

    @Test
    void concentricDifferentRadii() {
        assertEquals(0, Circle.of(0, 0, 1).intersection(Circle.of(0, 0, 2)).length);
    }

    @Test
    void coincidentCirclesThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> Circle.of(1, 1, 3).intersection(Circle.of(1, 1, 3)));
    }

    @Test
    void lineThroughCircle() {
        Circle c = Circle.of(0, 0, 5);
        Line l = Line.fromEquation(0, 1, -3); 
        Point[] pts = sorted(c.intersection(l));
        assertEquals(2, pts.length);
        assertPoint(-4, 3, pts[0]);
        assertPoint(4, 3, pts[1]);
    }

    @Test
    void tangentLine() {
        Point[] pts = Circle.of(0, 0, 5).intersection(Line.fromEquation(1, 0, -5)); 
        assertEquals(1, pts.length);
        assertPoint(5, 0, pts[0]);
    }

    @Test
    void lineMissesCircle() {
        assertEquals(0, Circle.of(0, 0, 1).intersection(Line.fromEquation(0, 1, -2)).length);
    }

    @Test
    void intersectionAreaCases() {
        Circle a = Circle.of(0, 0, 1);
        assertEquals(0.0, a.intersectionArea(Circle.of(5, 0, 1)), EPS);
        assertEquals(Math.PI, a.intersectionArea(Circle.of(0, 0, 3)), EPS);
        assertEquals(Math.PI, a.intersectionArea(a), EPS);
        assertEquals(2 * Math.PI / 3 - Math.sqrt(3) / 2, a.intersectionArea(Circle.of(1, 0, 1)), EPS);
    }

    @Test
    void circlesWithSameCenterAndRadiusAreEqual() {
        Circle a = Circle.of(1, 2, 3);
        Circle b = Circle.of(Point.of(1, 2), 3);
        assertEquals(a, b);
    }

    @Test
    void circlesWithDifferentCenterOrRadiusAreNotEqual() {
        Circle c = Circle.of(0, 0, 1);
        assertNotEquals(c, Circle.of(0, 0, 2));
        assertNotEquals(c, Circle.of(1, 0, 1));
        assertFalse(c.equals(null));
        assertFalse(c.equals(Point.of(0, 0)));
    }

    @Test
    void toStringShowsFields() {
        assertEquals("Circle(Point(0.0, 0.0), r=5.0)", Circle.of(0, 0, 5).toString());
    }
}
