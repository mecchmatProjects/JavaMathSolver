package solver.geometry;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Polygon2DTest {

    private static final double DELTA = 1e-9;

    private static Point[] pts(double... xy) {
        Point[] points = new Point[xy.length / 2];
        for (int i = 0; i < points.length; i++) {
            points[i] = Point.of(xy[2 * i], xy[2 * i + 1]);
        }
        return points;
    }

    private static Point[] unitSquare() { return pts(0, 0, 1, 0, 1, 1, 0, 1); }
    private static Point[] clockwiseSquare() { return pts(0, 0, 0, 1, 1, 1, 1, 0); }
    private static Point[] box(double x0, double y0, double x1, double y1) { return pts(x0, y0, x1, y0, x1, y1, x0, y1); }
    private static Point[] triangle() { return pts(0, 0, 4, 0, 0, 3); }
    private static Point[] bowTie() { return pts(0, 0, 2, 2, 2, 0, 0, 2); }
    private static Point[] lShape() { return pts(0, 0, 2, 0, 2, 1, 1, 1, 1, 2, 0, 2); }

    private static Point[] regular(int n) {
        Point[] result = new Point[n];
        for (int i = 0; i < n; i++) {
            result[i] = Point.of(Math.cos(2 * Math.PI * i / n), Math.sin(2 * Math.PI * i / n));
        }
        return result;
    }

    private static void assertSamePoints(Point[] expected, Point[] actual) {
        assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i].x, actual[i].x, DELTA);
            assertEquals(expected[i].y, actual[i].y, DELTA);
        }
    }

    private static double totalArea(Point[][] pieces) {
        double sum = 0;
        for (Point[] piece : pieces) {
            sum += Polygon2D.area(piece);
        }
        return sum;
    }

    @Test
    void perimeterOfSquareTriangleAndConcavePolygon() {
        assertEquals(4.0, Polygon2D.perimeter(unitSquare()), DELTA);
        assertEquals(12.0, Polygon2D.perimeter(triangle()), DELTA);
        assertEquals(8.0, Polygon2D.perimeter(lShape()), DELTA);
    }

    @Test
    void perimeterIncludesTheClosingEdge() {
        assertEquals(2 + Math.sqrt(2), Polygon2D.perimeter(pts(0, 0, 1, 0, 1, 1)), DELTA);
    }

    @Test
    void perimeterOfInvalidPolygonIsNaN() {
        assertTrue(Double.isNaN(Polygon2D.perimeter(null)));
        assertTrue(Double.isNaN(Polygon2D.perimeter(new Point[]{Point.of(0, 0), Point.of(1, 0), null})));
    }

    @Test
    void areaOfSquareTriangleAndConcavePolygon() {
        assertEquals(1.0, Polygon2D.area(unitSquare()), DELTA);
        assertEquals(6.0, Polygon2D.area(triangle()), DELTA);
        assertEquals(3.0, Polygon2D.area(lShape()), DELTA);
    }

    @Test
    void areaOfCollinearPointsAndBowTieIsZero() {
        assertEquals(0.0, Polygon2D.area(pts(0, 0, 1, 1, 2, 2)), DELTA);
        assertEquals(0.0, Polygon2D.area(bowTie()), DELTA);
    }

    @Test
    void areaOfInvalidPolygonIsNaN() {
        assertTrue(Double.isNaN(Polygon2D.area(new Point[0])));
        assertTrue(Double.isNaN(Polygon2D.area(new Point[3])));
    }

    @Test
    void squareTriangleAndRegularPolygonAreConvexInBothOrientations() {
        assertTrue(Polygon2D.isConvex(unitSquare()));
        assertTrue(Polygon2D.isConvex(clockwiseSquare()));
        assertTrue(Polygon2D.isConvex(triangle()));
        assertTrue(Polygon2D.isConvex(regular(5)));
    }

    @Test
    void reflexVertexAtAnyPositionBreaksConvexity() {
        assertFalse(Polygon2D.isConvex(lShape()));
        assertFalse(Polygon2D.isConvex(pts(1, 1, 0, 0, 2, 0, 2, 2, 0, 2)));
        assertFalse(Polygon2D.isConvex(pts(0, 0, 2, 0, 1, 1, 2, 2, 0, 2)));
        assertFalse(Polygon2D.isConvex(pts(0, 0, 2, 0, 2, 2, 0, 2, 1, 1)));
    }

    @Test
    void pentagramIsNotConvexEvenThoughAllTurnsGoTheSameWay() {
        Point[] p = regular(5);
        assertFalse(Polygon2D.isConvex(new Point[]{p[0], p[2], p[4], p[1], p[3]}));
    }

    @Test
    void collinearVertexOnEdgeKeepsConvexityButFullyCollinearIsNotConvex() {
        assertTrue(Polygon2D.isConvex(pts(0, 0, 1, 0, 2, 0, 2, 2, 0, 2)));
        assertFalse(Polygon2D.isConvex(pts(0, 0, 1, 0, 2, 0)));
        assertFalse(Polygon2D.isConvex(pts(1, 1, 1, 1, 1, 1)));
    }

    @Test
    void bowTieAndInvalidPolygonAreNotConvex() {
        assertFalse(Polygon2D.isConvex(bowTie()));
        assertFalse(Polygon2D.isConvex(pts(0, 0, 1, 1)));
    }

    @Test
    void hullDropsInteriorPointAndStartsFromLeftmostLowestCounterClockwise() {
        Point[] input = pts(2, 2, 0, 0, 1, 1, 2, 0, 0, 2);
        assertSamePoints(pts(0, 0, 2, 0, 2, 2, 0, 2), Polygon2D.convexHull(input));
        assertSamePoints(pts(2, 2, 0, 0, 1, 1, 2, 0, 0, 2), input); 
    }

    @Test
    void hullDropsPointsOnEdgesAndDuplicates() {
        assertSamePoints(pts(0, 0, 2, 0, 2, 2, 0, 2),
                Polygon2D.convexHull(pts(0, 0, 1, 0, 2, 0, 2, 2, 0, 2, 0, 1)));
        assertEquals(3, Polygon2D.convexHull(pts(0, 0, 0, 0, 2, 0, 2, 0, 1, 2, 1, 2)).length);
    }

    @Test
    void hullDoesNotDependOnInputOrder() {
        Point[] first = Polygon2D.convexHull(pts(0, 0, 4, 0, 4, 3, 0, 3, 2, 1, 1, 2));
        Point[] second = Polygon2D.convexHull(pts(1, 2, 0, 3, 2, 1, 4, 3, 4, 0, 0, 0));
        assertSamePoints(first, second);
    }

    @Test
    void hullOfDegenerateOrInvalidInputIsNull() {
        assertNull(Polygon2D.convexHull(pts(0, 0, 1, 1, 2, 2, 3, 3)));
        assertNull(Polygon2D.convexHull(pts(4, 4, 4, 4, 4, 4)));
        assertNull(Polygon2D.convexHull(pts(0, 0, 1, 1)));
    }

    @Test
    void convexAndConcavePolygonsWithoutCrossingsAreSimple() {
        assertTrue(Polygon2D.isSimple(unitSquare()));
        assertTrue(Polygon2D.isSimple(clockwiseSquare()));
        assertTrue(Polygon2D.isSimple(lShape()));
        assertTrue(Polygon2D.isSimple(pts(0, 0, 1, 0, 2, 0, 2, 2, 0, 2))); 
    }

    @Test
    void crossingEdgesMakePolygonNotSimple() {
        assertFalse(Polygon2D.isSimple(bowTie()));
        Point[] p = regular(5);
        assertFalse(Polygon2D.isSimple(new Point[]{p[0], p[2], p[4], p[1], p[3]}));
    }

    @Test
    void touchingAndFoldedBackEdgesAreNotSimple() {
        assertFalse(Polygon2D.isSimple(pts(0, 0, 4, 0, 4, 4, 2, 0))); 
        assertFalse(Polygon2D.isSimple(pts(0, 0, 2, 0, 1, 0)));       
        assertFalse(Polygon2D.isSimple(pts(1, 0, 0, 0, 2, 0)));       
    }

    @Test
    void isSimpleOfInvalidPolygonIsFalse() {
        assertFalse(Polygon2D.isSimple(null));
        assertFalse(Polygon2D.isSimple(new Point[]{Point.of(0, 0), Point.of(1, 0), null}));
    }

    @Test
    void triangleStaysOneTriangleAndSquareSplitsIntoTwo() {
        assertEquals(1, Polygon2D.triangulate(triangle()).length);
        assertEquals(2, Polygon2D.triangulate(unitSquare()).length);
        assertEquals(6, Polygon2D.triangulate(regular(8)).length);
    }

    @Test
    void concavePolygonIsSplitIntoTrianglesThatCoverItsArea() {
        Point[][] triangles = Polygon2D.triangulate(lShape());
        assertEquals(4, triangles.length);
        assertEquals(3.0, totalArea(triangles), DELTA);
        for (Point[] t : triangles) {
            assertEquals(3, t.length);
            assertTrue(Polygon2D.contains(lShape(), Points.centroid(t)));
        }
    }

    @Test
    void clockwiseInputAndCollinearVertexDoNotBreakTriangulation() {
        assertEquals(1.0, totalArea(Polygon2D.triangulate(clockwiseSquare())), DELTA);
        Point[][] triangles = Polygon2D.triangulate(pts(0, 0, 1, 0, 2, 0, 2, 2, 0, 2));
        assertEquals(4.0, totalArea(triangles), DELTA);
        for (Point[] t : triangles) {
            assertTrue(Polygon2D.area(t) > DELTA);
        }
    }

    @Test
    void triangulateRejectsNonSimpleAndInvalidPolygons() {
        assertNull(Polygon2D.triangulate(bowTie()));
        assertNull(Polygon2D.triangulate(pts(0, 0, 1, 0, 2, 0)));
        assertNull(Polygon2D.triangulate(null));
    }

    @Test
    void translateShiftsEveryVertex() {
        assertSamePoints(pts(3, -2, 4, -2, 4, -1, 3, -1), Polygon2D.translate(unitSquare(), 3, -2));
        assertSamePoints(unitSquare(), Polygon2D.translate(unitSquare(), 0, 0));
    }

    @Test
    void translatePreservesAreaAndPerimeter() {
        Point[] moved = Polygon2D.translate(lShape(), 100, -50);
        assertEquals(3.0, Polygon2D.area(moved), 1e-6);
        assertEquals(8.0, Polygon2D.perimeter(moved), 1e-6);
    }

    @Test
    void translateReturnsNewPointsAndRejectsInvalidPolygon() {
        Point[] original = unitSquare();
        Point[] moved = Polygon2D.translate(original, 1, 1);
        assertNotSame(original[0], moved[0]);
        assertEquals(0.0, original[0].x, DELTA);
        assertNull(Polygon2D.translate(null, 1, 1));
    }

    @Test
    void scaleMultipliesCoordinatesRelativeToOrigin() {
        assertSamePoints(pts(0, 0, 2, 0, 2, 3, 0, 3), Polygon2D.scale(unitSquare(), 2, 3));
        assertSamePoints(pts(10, 10, 20, 10, 20, 20), Polygon2D.scale(pts(1, 1, 2, 1, 2, 2), 10, 10));
    }

    @Test
    void negativeScaleMirrorsPolygonAndZeroScaleCollapsesIt() {
        Point[] mirrored = Polygon2D.scale(unitSquare(), -1, 1);
        assertSamePoints(pts(0, 0, -1, 0, -1, 1, 0, 1), mirrored);
        assertEquals(1.0, Polygon2D.area(mirrored), DELTA);
        assertEquals(0.0, Polygon2D.area(Polygon2D.scale(unitSquare(), 0, 1)), DELTA);
    }

    @Test
    void scaleRejectsInvalidPolygon() {
        assertNull(Polygon2D.scale(pts(0, 0, 1, 1), 2, 2));
        assertNull(Polygon2D.scale(new Point[]{null, null, null}, 2, 2));
    }

    @Test
    void rotateByQuarterTurnIsCounterClockwise() {
        assertSamePoints(pts(0, 1, 0, 2, -1, 2), Polygon2D.rotate(pts(1, 0, 2, 0, 2, 1), Math.PI / 2));
    }

    @Test
    void rotateByHalfTurnNegatesAndByFullTurnKeepsCoordinates() {
        assertSamePoints(pts(-1, -2, -3, -4, -5, -6), Polygon2D.rotate(pts(1, 2, 3, 4, 5, 6), Math.PI));
        assertSamePoints(lShape(), Polygon2D.rotate(lShape(), 2 * Math.PI));
    }

    @Test
    void rotationPreservesAreaPerimeterAndRejectsInvalidPolygon() {
        Point[] rotated = Polygon2D.rotate(lShape(), 1.234);
        assertEquals(3.0, Polygon2D.area(rotated), DELTA);
        assertEquals(8.0, Polygon2D.perimeter(rotated), DELTA);
        assertNull(Polygon2D.rotate(null, 1));
    }

    @Test
    void containsInteriorAndRejectsExteriorPoints() {
        assertTrue(Polygon2D.contains(unitSquare(), Point.of(0.5, 0.5)));
        assertTrue(Polygon2D.contains(clockwiseSquare(), Point.of(0.5, 0.5)));
        assertFalse(Polygon2D.contains(unitSquare(), Point.of(2, 2)));
        assertFalse(Polygon2D.contains(unitSquare(), Point.of(-0.001, 0.5)));
    }

    @Test
    void pointsOnEdgesAndVerticesCountAsInside() {
        assertTrue(Polygon2D.contains(unitSquare(), Point.of(0.5, 0)));
        assertTrue(Polygon2D.contains(unitSquare(), Point.of(1, 0.5)));
        assertTrue(Polygon2D.contains(unitSquare(), Point.of(0, 0)));
        assertTrue(Polygon2D.contains(unitSquare(), Point.of(1, 1)));
    }

    @Test
    void pointOnExtensionOfAnEdgeIsOutside() {
        assertFalse(Polygon2D.contains(unitSquare(), Point.of(2, 0)));
        assertFalse(Polygon2D.contains(unitSquare(), Point.of(0, -1)));
        assertFalse(Polygon2D.contains(pts(1, 0, 2, 1, 1, 2, 0, 1), Point.of(-1, 1)));
    }

    @Test
    void concaveNotchIsOutsideAndInvalidPolygonGivesFalse() {
        assertFalse(Polygon2D.contains(lShape(), Point.of(1.5, 1.5)));
        assertTrue(Polygon2D.contains(lShape(), Point.of(0.5, 1.5)));
        assertFalse(Polygon2D.contains(null, Point.of(0, 0)));
    }

    @Test
    void overlappingPolygonsIntersectCommutatively() {
        assertEquals(1.0, totalArea(Polygon2D.intersection(box(0, 0, 2, 2), box(1, 1, 3, 3))), DELTA);
        Point[] a = lShape();
        Point[] b = box(0.5, 0.5, 1.5, 1.5);
        assertEquals(totalArea(Polygon2D.intersection(a, b)), totalArea(Polygon2D.intersection(b, a)), DELTA);
    }

    @Test
    void disjointOrTouchingPolygonsHaveNoAreaInCommon() {
        Point[][] disjoint = Polygon2D.intersection(box(0, 0, 1, 1), box(5, 5, 6, 6));
        assertEquals(0, disjoint.length); 
        assertEquals(0.0, totalArea(Polygon2D.intersection(box(0, 0, 1, 1), box(1, 0, 2, 1))), DELTA);
        assertEquals(0.0, totalArea(Polygon2D.intersection(box(0, 0, 1, 1), box(1, 1, 2, 2))), DELTA);
    }

    @Test
    void containedAndIdenticalPolygonsIntersectInTheSmallerOne() {
        assertEquals(4.0, totalArea(Polygon2D.intersection(box(0, 0, 10, 10), box(2, 2, 4, 4))), DELTA);
        assertEquals(3.0, totalArea(Polygon2D.intersection(lShape(), lShape())), DELTA);
        assertEquals(0.75, totalArea(Polygon2D.intersection(lShape(), box(0.5, 0.5, 1.5, 1.5))), DELTA);
    }

    @Test
    void intersectionRejectsInvalidAndSelfIntersectingPolygons() {
        assertNull(Polygon2D.intersection(null, unitSquare()));
        assertNull(Polygon2D.intersection(unitSquare(), bowTie()));
    }

    @Test
    void predicatesWorkOnTinyScaleTriangle() {
        Point[] tiny = {Point.of(0, 0), Point.of(1e-5, 0), Point.of(0, 1e-5)};
        assertTrue(Polygon2D.isConvex(tiny));
        assertTrue(Polygon2D.isSimple(tiny));
        assertEquals(1, Polygon2D.triangulate(tiny).length);
        assertEquals(3, Polygon2D.convexHull(tiny).length);
        assertTrue(Polygon2D.contains(tiny, Point.of(2e-6, 2e-6)));
        assertFalse(Polygon2D.contains(tiny, Point.of(1e-5, 1e-5)));
        assertEquals(1, Polygon2D.intersection(tiny, tiny).length);
    }

    @Test
    void nullPointsAreRejectedWithoutException() {
        Point[] sq = unitSquare();
        assertFalse(Polygon2D.contains(sq, null));
        assertTrue(Double.isNaN(Points.distance(null, Point.of(0, 0))));
        assertTrue(Double.isNaN(Points.distance(Point.of(0, 0), null)));
        assertNull(Points.nearest(null, sq));
    }

    @Test
    void nonFiniteCoordinatesAreRejected() {
        Point[] bad = {Point.of(0, 0), Point.of(1, 0), Point.of(Double.NaN, 1)};
        assertTrue(Double.isNaN(Polygon2D.area(bad)));
        assertNull(Polygon2D.convexHull(bad));
        assertFalse(Polygon2D.contains(unitSquare(), Point.of(Double.POSITIVE_INFINITY, 0)));
        assertNull(Points.nearest(Point.of(Double.NaN, 0), unitSquare()));
        assertNull(Points.centroid(bad));
    }

    @Test
    void hullOfFewerThanThreePointsIsRejected() {
        assertNull(Polygon2D.convexHull(new Point[]{Point.of(0, 0), Point.of(1, 1)}));
    }

    @Test
    void closedContourBehavesLikeOpenOne() {
        Point[] sq = unitSquare();
        Point[] closed = java.util.Arrays.copyOf(sq, sq.length + 1);
        closed[sq.length] = Point.of(sq[0].x, sq[0].y);
        assertTrue(Polygon2D.isSimple(closed));
        assertTrue(Polygon2D.isConvex(closed));
        assertEquals(Polygon2D.area(sq), Polygon2D.area(closed), 1e-12);
        assertEquals(Polygon2D.perimeter(sq), Polygon2D.perimeter(closed), 1e-12);
        assertEquals(2, Polygon2D.triangulate(closed).length);
        assertTrue(Polygon2D.contains(closed, Point.of(0.5, 0.5)));
    }
}
