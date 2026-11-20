package solver.geometry;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SegmentTest {

    private static final double EPS = 1e-9;

    private static void assertPoint(double x, double y, Point p) {
        assertNotNull(p);
        assertEquals(x, p.x, EPS, "x");
        assertEquals(y, p.y, EPS, "y");
    }

    @Test
    void nullEndpointsThrow() {
        assertThrows(IllegalArgumentException.class, () -> Segment.of(null, Point.of(1, 1)));
        assertThrows(IllegalArgumentException.class, () -> Segment.of(Point.of(1, 1), null));
    }

    @Test
    void lengthVectorAndMidpoint() {
        Segment s = Segment.of(0, 0, 3, 4);
        assertEquals(5.0, s.length(), EPS);
        assertEquals(Vector2D.of(3, 4), s.toVector());
        assertEquals(Point.of(1.5, 2), s.midpoint());
        assertFalse(s.isDegenerate());
        assertTrue(Segment.of(1, 1, 1, 1).isDegenerate());
    }

    @Test
    void toLineContainsBothEndpoints() {
        Line line = Segment.of(1, 2, 4, 6).toLine();
        assertTrue(line.contains(Point.of(1, 2)));
        assertTrue(line.contains(Point.of(4, 6)));
        assertThrows(IllegalArgumentException.class, () -> Segment.of(1, 1, 1, 1).toLine());
    }

    @Test
    void containsEndpointsAndInteriorButNotPointsBeyond() {
        Segment s = Segment.of(0, 0, 4, 2);
        assertTrue(s.contains(Point.of(0, 0)));
        assertTrue(s.contains(Point.of(4, 2)));
        assertTrue(s.contains(Point.of(2, 1)));
        assertFalse(s.contains(Point.of(6, 3)));
        assertFalse(s.contains(Point.of(2, 1.1)));
    }

    @Test
    void distanceToPointUsesProjectionOrNearestEndpoint() {
        Segment s = Segment.of(0, 0, 4, 0);
        assertEquals(3.0, s.distanceTo(Point.of(2, 3)), EPS);
        assertEquals(5.0, s.distanceTo(Point.of(7, 4)), EPS);
        assertEquals(5.0, s.distanceTo(Point.of(-3, -4)), EPS);
        assertEquals(0.0, s.distanceTo(Point.of(1, 0)), EPS);
        assertEquals(5.0, Segment.of(0, 0, 0, 0).distanceTo(Point.of(3, 4)), EPS);
        assertPoint(2, 0, s.closestPointTo(Point.of(2, 3)));
    }

    @Test
    void crossingSegmentsMeetInOnePoint() {
        Segment s = Segment.of(0, 0, 2, 2);
        Segment t = Segment.of(0, 2, 2, 0);
        assertTrue(s.intersects(t));
        assertPoint(1, 1, s.intersection(t));
        assertPoint(1, 1, t.intersection(s));
    }

    @Test
    void segmentsTouchingAtEndpoint() {
        Segment s = Segment.of(0, 0, 1, 0);
        Segment t = Segment.of(1, 0, 2, 5);
        assertPoint(1, 0, s.intersection(t));
        Segment tee = Segment.of(0.5, 0, 0.5, 3);
        assertPoint(0.5, 0, s.intersection(tee));
    }

    @Test
    void disjointAndParallelSegmentsHaveNoIntersection() {
        assertNull(Segment.of(0, 0, 1, 0).intersection(Segment.of(0, 1, 1, 1)));
        assertNull(Segment.of(0, 0, 1, 1).intersection(Segment.of(3, 0, 2, 1)));
        assertNull(Segment.of(0, 0, 1, 0).intersection(Segment.of(2, 0, 3, 0)));
        assertFalse(Segment.of(0, 0, 1, 0).intersects(Segment.of(2, 0, 3, 0)));
    }

    @Test
    void collinearSegmentsSharingOnlyAnEndpointMeetThere() {
        assertPoint(1, 0, Segment.of(0, 0, 1, 0).intersection(Segment.of(1, 0, 3, 0)));
        assertPoint(1, 0, Segment.of(1, 0, 3, 0).intersection(Segment.of(1, 0, 0, 0)));
    }

    @Test
    void overlappingCollinearSegmentsThrow() {
        Segment s = Segment.of(0, 0, 2, 0);
        assertThrows(IllegalArgumentException.class, () -> s.intersection(Segment.of(1, 0, 3, 0)));
        assertThrows(IllegalArgumentException.class, () -> s.intersection(s));
    }

    @Test
    void degenerateSegmentIntersectsWhenPointLiesOnOther() {
        assertPoint(1, 0, Segment.of(1, 0, 1, 0).intersection(Segment.of(0, 0, 2, 0)));
        assertNull(Segment.of(1, 1, 1, 1).intersection(Segment.of(0, 0, 2, 0)));
    }

    @Test
    void intersectionWithLine() {
        Line xAxis = Line.through(Point.of(0, 0), Point.of(1, 0));
        assertPoint(2, 0, Segment.of(1, -1, 3, 1).intersection(xAxis));
        assertNull(Segment.of(0, 1, 5, 2).intersection(xAxis));
        assertPoint(4, 0, Segment.of(4, 0, 5, 3).intersection(xAxis));
        assertThrows(IllegalArgumentException.class, () -> Segment.of(1, 0, 3, 0).intersection(xAxis));
        assertTrue(Segment.of(1, -1, 3, 1).intersects(xAxis));
        assertFalse(Segment.of(0, 1, 5, 2).intersects(xAxis));
    }

    @Test
    void collinearityWithPointsAndSegments() {
        Segment s = Segment.of(0, 0, 2, 2);
        assertTrue(s.isCollinearWith(Point.of(5, 5)));
        assertTrue(s.isCollinearWith(Point.of(-1, -1)));
        assertFalse(s.isCollinearWith(Point.of(1, 2)));
        assertTrue(s.isCollinearWith(Segment.of(3, 3, 7, 7)));
        assertFalse(s.isCollinearWith(Segment.of(0, 1, 2, 3)));
    }

    @Test
    void parallelSegments() {
        Segment s = Segment.of(0, 0, 2, 2);
        assertTrue(s.isParallelTo(Segment.of(0, 1, 2, 3)));
        assertTrue(s.isParallelTo(Segment.of(3, 3, 1, 1)));
        assertFalse(s.isParallelTo(Segment.of(0, 0, 2, 3)));
        assertFalse(s.isParallelTo(Segment.of(1, 1, 1, 1)));
    }

    @Test
    void translateMovesBothEndpointsByVector() {
        Segment s = Segment.of(0, 0, 3, 4).translate(Vector2D.of(1, -2));
        assertEquals(Segment.of(1, -2, 4, 2), s);
        assertEquals(5.0, s.length(), EPS);
    }

    @Test
    void rotateAboutCenterKeepsLength() {
        Segment s = Segment.of(1, 0, 3, 0).rotate(Math.PI / 2, Point.of(0, 0));
        assertPoint(0, 1, s.a());
        assertPoint(0, 3, s.b());
        assertEquals(2.0, s.length(), EPS);
    }

    @Test
    void scaleAboutCenterMultipliesLength() {
        Segment s = Segment.of(2, 1, 3, 1).scale(2, Point.of(1, 1));
        assertPoint(3, 1, s.a());
        assertPoint(5, 1, s.b());
        assertEquals(2.0, s.length(), EPS);
        assertEquals(3.0, Segment.of(0, 0, 1, 0).scale(-3, Point.of(0, 0)).length(), EPS);
    }

    @Test
    void equalsHashCodeAndToString() {
        assertEquals(Segment.of(1, 2, 3, 4), Segment.of(Point.of(1, 2), Point.of(3, 4)));
        assertEquals(Segment.of(1, 2, 3, 4).hashCode(), Segment.of(1, 2, 3, 4).hashCode());
        assertNotEquals(Segment.of(1, 2, 3, 4), Segment.of(3, 4, 1, 2));
        assertEquals("Segment(Point(1.0, 2.0), Point(3.0, 4.0))", Segment.of(1, 2, 3, 4).toString());
    }
}
