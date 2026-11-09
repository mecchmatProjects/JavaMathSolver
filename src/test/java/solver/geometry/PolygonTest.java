package solver.geometry;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static solver.geometry.TestPoints.pts;

class PolygonTest {

    private static final double EPS = 1e-9;

    private static Polygon square() {
        return Polygon.of(pts(0, 0, 2, 0, 2, 2, 0, 2));
    }

    private static Polygon lShape() {
        return Polygon.of(pts(0, 0, 2, 0, 2, 1, 1, 1, 1, 2, 0, 2));
    }

    @Test
    void invalidVerticesThrow() {
        assertThrows(IllegalArgumentException.class, () -> Polygon.of((Point[]) null));
        assertThrows(IllegalArgumentException.class, () -> Polygon.of(pts(0, 0, 1, 1)));
        assertThrows(IllegalArgumentException.class, () -> Polygon.of(Point.of(0, 0), null, Point.of(1, 1)));
        assertThrows(IllegalArgumentException.class, () -> Polygon.of(pts(0, 0, Double.NaN, 0, 1, 1)));
        assertThrows(IllegalArgumentException.class, () -> Polygon.of(pts(0, 0, 2, 2, 2, 0, 0, 2)));
        assertThrows(IllegalArgumentException.class, () -> Polygon.of(pts(0, 0, 1, 0, 2, 0)));
    }

    @Test
    void closingVertexIsDropped() {
        Polygon closed = Polygon.of(pts(0, 0, 2, 0, 2, 2, 0, 2, 0, 0));
        assertEquals(4, closed.size());
        assertEquals(square(), closed);
    }

    @Test
    void verticesReturnsACopy() {
        Polygon p = square();
        p.vertices()[0] = Point.of(100, 100);
        assertEquals(Point.of(0, 0), p.vertices()[0]);
    }

    @Test
    void areaPerimeterAndConvexity() {
        assertEquals(4.0, square().area(), EPS);
        assertEquals(8.0, square().perimeter(), EPS);
        assertTrue(square().isConvex());
        assertEquals(3.0, lShape().area(), EPS);
        assertEquals(8.0, lShape().perimeter(), EPS);
        assertFalse(lShape().isConvex());
    }

    @Test
    void edgesCloseTheContour() {
        Segment[] edges = square().edges();
        assertEquals(4, edges.length);
        assertEquals(Segment.of(0, 2, 0, 0), edges[3]);
    }

    @Test
    void containsInsideBoundaryAndNotchOfConcavePolygon() {
        Polygon l = lShape();
        assertTrue(l.contains(Point.of(0.5, 0.5)));
        assertTrue(l.contains(Point.of(0.5, 1.5)));
        assertTrue(l.contains(Point.of(1, 1.5)));
        assertTrue(l.contains(Point.of(2, 0)));
        assertFalse(l.contains(Point.of(1.5, 1.5)));
        assertFalse(l.contains(Point.of(3, 0.5)));
    }

    @Test
    void distanceToPoint() {
        Polygon l = lShape();
        assertEquals(0.0, l.distanceTo(Point.of(0.5, 0.5)), EPS);
        assertEquals(0.5, l.distanceTo(Point.of(1.5, 1.5)), EPS);
        assertEquals(Math.sqrt(0.5), l.distanceTo(Point.of(2.5, 1.5)), EPS);
        assertEquals(1.0, l.distanceTo(Point.of(3, 0.5)), EPS);
    }

    @Test
    void triangulationCoversTheArea() {
        Triangle[] triangles = lShape().triangulate();
        assertEquals(4, triangles.length);
        double sum = 0;
        for (Triangle t : triangles) {
            sum += t.area();
        }
        assertEquals(3.0, sum, EPS);
    }

    @Test
    void transforms() {
        assertEquals(Polygon.of(pts(1, 1, 3, 1, 3, 3, 1, 3)), square().translate(Vector2D.of(1, 1)));
        Polygon rotated = lShape().rotate(0.7, Point.of(5, -1));
        assertEquals(3.0, rotated.area(), EPS);
        assertEquals(8.0, rotated.perimeter(), EPS);
        assertEquals(12.0, lShape().scale(2, Point.of(1, 1)).area(), EPS);
        assertThrows(IllegalArgumentException.class, () -> square().scale(0, Point.of(0, 0)));
    }

    @Test
    void equalsHashCodeAndToString() {
        assertEquals(square(), square());
        assertEquals(square().hashCode(), square().hashCode());
        assertNotEquals(square(), lShape());
        assertEquals("Polygon[Point(0.0, 0.0), Point(2.0, 0.0), Point(2.0, 2.0), Point(0.0, 2.0)]",
                square().toString());
    }
}
