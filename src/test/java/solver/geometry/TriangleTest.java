package solver.geometry;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TriangleTest {

    private static final double EPS = 1e-9;

    private static Triangle t345() {
        return Triangle.of(Point.of(0, 0), Point.of(4, 0), Point.of(0, 3));
    }

    @Test
    void nullVertexThrows() {
        assertThrows(IllegalArgumentException.class, () -> Triangle.of(null, Point.of(1, 0), Point.of(0, 1)));
    }

    @Test
    void areaAndPerimeterOf345Triangle() {
        assertEquals(6.0, t345().area(), EPS);
        assertEquals(12.0, t345().perimeter(), EPS);
    }

    @Test
    void areaDoesNotDependOnVertexOrder() {
        Triangle clockwise = Triangle.of(Point.of(0, 0), Point.of(0, 3), Point.of(4, 0));
        assertEquals(6.0, clockwise.area(), EPS);
    }

    @Test
    void degenerateTriangles() {
        assertFalse(t345().isDegenerate());
        assertTrue(Triangle.of(Point.of(0, 0), Point.of(1, 1), Point.of(3, 3)).isDegenerate());
        assertTrue(Triangle.of(Point.of(1, 1), Point.of(1, 1), Point.of(2, 5)).isDegenerate());
        assertEquals(0.0, Triangle.of(Point.of(0, 0), Point.of(1, 1), Point.of(3, 3)).area(), EPS);
    }

    @Test
    void containsInsideVerticesAndEdges() {
        Triangle t = t345();
        assertTrue(t.contains(Point.of(1, 1)));
        assertTrue(t.contains(Point.of(0, 0)));
        assertTrue(t.contains(Point.of(4, 0)));
        assertTrue(t.contains(Point.of(2, 0)));
        assertTrue(t.contains(Point.of(2, 1.5)));
        assertFalse(t.contains(Point.of(3, 3)));
        assertFalse(t.contains(Point.of(-0.1, 1)));
        assertFalse(t.contains(Point.of(5, 0)));
    }

    @Test
    void containsWorksForClockwiseOrder() {
        Triangle t = Triangle.of(Point.of(0, 0), Point.of(0, 3), Point.of(4, 0));
        assertTrue(t.contains(Point.of(1, 1)));
        assertFalse(t.contains(Point.of(3, 3)));
    }

    @Test
    void degenerateTriangleContainsOnlyPointsOfItsSides() {
        Triangle t = Triangle.of(Point.of(0, 0), Point.of(1, 1), Point.of(3, 3));
        assertTrue(t.contains(Point.of(2, 2)));
        assertFalse(t.contains(Point.of(4, 4)));
        assertFalse(t.contains(Point.of(1, 0)));
    }

    @Test
    void distanceToPoint() {
        Triangle t = t345();
        assertEquals(0.0, t.distanceTo(Point.of(1, 1)), EPS);
        assertEquals(2.0, t.distanceTo(Point.of(2, -2)), EPS);
        assertEquals(5.0, t.distanceTo(Point.of(7, -4)), EPS);
        assertEquals(1.0, t.distanceTo(Point.of(-1, 1)), EPS);
    }

    @Test
    void centroidAndSides() {
        Triangle t = t345();
        assertEquals(4.0 / 3, t.centroid().x, EPS);
        assertEquals(1.0, t.centroid().y, EPS);
        Segment[] sides = t.sides();
        assertEquals(3, sides.length);
        assertEquals(4.0, sides[0].length(), EPS);
        assertEquals(5.0, sides[1].length(), EPS);
        assertEquals(3.0, sides[2].length(), EPS);
    }

    @Test
    void toPolygonKeepsArea() {
        assertEquals(6.0, t345().toPolygon().area(), EPS);
        assertThrows(IllegalArgumentException.class,
                () -> Triangle.of(Point.of(0, 0), Point.of(1, 1), Point.of(2, 2)).toPolygon());
    }

    @Test
    void transformsKeepOrScaleAreaAndPerimeter() {
        Triangle t = t345();
        Triangle moved = t.translate(Vector2D.of(10, -5));
        assertEquals(Point.of(10, -5), moved.a());
        assertEquals(6.0, moved.area(), EPS);
        Triangle rotated = t.rotate(1.1, Point.of(2, 2));
        assertEquals(6.0, rotated.area(), EPS);
        assertEquals(12.0, rotated.perimeter(), EPS);
        Triangle scaled = t.scale(-2, Point.of(0, 0));
        assertEquals(24.0, scaled.area(), EPS);
        assertEquals(24.0, scaled.perimeter(), EPS);
    }

    @Test
    void equalsHashCodeAndToString() {
        assertEquals(t345(), t345());
        assertEquals(t345().hashCode(), t345().hashCode());
        assertNotEquals(t345(), Triangle.of(Point.of(4, 0), Point.of(0, 0), Point.of(0, 3)));
        assertEquals("Triangle(Point(0.0, 0.0), Point(4.0, 0.0), Point(0.0, 3.0))", t345().toString());
    }
}
