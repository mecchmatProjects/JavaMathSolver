package solver.geometry;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointsTest {

    private static final double DELTA = 1e-9;

    private static Point[] pts(double... xy) {
        Point[] points = new Point[xy.length / 2];
        for (int i = 0; i < points.length; i++) {
            points[i] = Point.of(xy[2 * i], xy[2 * i + 1]);
        }
        return points;
    }

    // ---------- distance ----------

    @Test
    void distanceOfPythagoreanTriplesIsSymmetric() {
        Point a = Point.of(1, 1);
        Point b = Point.of(6, 13);
        assertEquals(13.0, Points.distance(a, b), DELTA);
        assertEquals(Points.distance(a, b), Points.distance(b, a), 0.0);
        assertEquals(5.0, Points.distance(Point.of(0, 0), Point.of(3, 4)), DELTA);
    }

    @Test
    void distanceToItselfIsZero() {
        Point p = Point.of(2.5, -7);
        assertEquals(0.0, Points.distance(p, p), 0.0);
        assertEquals(0.0, Points.distance(p, Point.of(2.5, -7)), 0.0);
    }

    @Test
    void distanceAcrossQuadrantsAndAlongAxes() {
        assertEquals(10.0, Points.distance(Point.of(-3, -4), Point.of(3, 4)), DELTA);
        assertEquals(7.0, Points.distance(Point.of(1, 5), Point.of(8, 5)), DELTA);
        assertEquals(4.0, Points.distance(Point.of(2, -1), Point.of(2, 3)), DELTA);
    }

    @Test
    void distanceSurvivesHugeAndTinyCoordinates() {
        assertEquals(5e200, Points.distance(Point.of(0, 0), Point.of(3e200, 4e200)), 1e186);
        assertEquals(5e-200, Points.distance(Point.of(0, 0), Point.of(3e-200, 4e-200)), 1e-214);
    }

    @Test
    void distancePropagatesNaNAndInfinity() {
        assertTrue(Double.isNaN(Points.distance(Point.of(Double.NaN, 0), Point.of(1, 1))));
        assertEquals(Double.POSITIVE_INFINITY,
                Points.distance(Point.of(0, 0), Point.of(Double.POSITIVE_INFINITY, 0)));
    }

    // ---------- boundingBox ----------

    @Test
    void boundingBoxReturnsMinXMaxXMinYMaxYInThatOrder() {
        double[] box = Points.boundingBox(pts(0, 10, 20, 30));
        assertEquals(4, box.length);
        assertEquals(0.0, box[0], DELTA, "minX");
        assertEquals(20.0, box[1], DELTA, "maxX");
        assertEquals(10.0, box[2], DELTA, "minY");
        assertEquals(30.0, box[3], DELTA, "maxY");
    }

    @Test
    void boundingBoxFindsExtremesAtFirstMiddleAndLastIndex() {
        double[] expected = {-5, 5, -6, 6};
        assertArrayEquals(expected, Points.boundingBox(pts(-5, 6, 0, 0, 5, -6)), DELTA);
        assertArrayEquals(expected, Points.boundingBox(pts(5, -6, 0, 0, -5, 6)), DELTA);
        assertArrayEquals(expected, Points.boundingBox(pts(0, 0, -5, 6, 5, -6)), DELTA);
    }

    @Test
    void boundingBoxOfSingleAndIdenticalPointsIsDegenerate() {
        assertArrayEquals(new double[]{3, 3, -4, -4}, Points.boundingBox(pts(3, -4)), DELTA);
        assertArrayEquals(new double[]{2, 2, 2, 2}, Points.boundingBox(pts(2, 2, 2, 2, 2, 2)), DELTA);
    }

    @Test
    void boundingBoxOfAllNegativePointsDoesNotStartFromZero() {
        assertArrayEquals(new double[]{-9, -1, -8, -2}, Points.boundingBox(pts(-1, -2, -9, -8, -5, -5)), DELTA);
    }

    @Test
    void boundingBoxRejectsInvalidInput() {
        assertNull(Points.boundingBox(null));
        assertNull(Points.boundingBox(new Point[0]));
        assertNull(Points.boundingBox(new Point[]{null, Point.of(1, 1)}));
        assertNull(Points.boundingBox(new Point[]{Point.of(1, 1), null}));
    }

    // ---------- centroid ----------

    @Test
    void centroidOfTriangleAndSquare() {
        Point triangle = Points.centroid(pts(0, 0, 6, 0, 0, 3));
        assertEquals(2.0, triangle.x, DELTA);
        assertEquals(1.0, triangle.y, DELTA);

        Point square = Points.centroid(pts(0, 0, 4, 0, 4, 4, 0, 4));
        assertEquals(2.0, square.x, DELTA);
        assertEquals(2.0, square.y, DELTA);
    }

    @Test
    void centroidOfSinglePointIsEqualButNotTheSameInstance() {
        Point[] points = pts(-3.5, 8);
        Point centroid = Points.centroid(points);
        assertEquals(-3.5, centroid.x, DELTA);
        assertEquals(8.0, centroid.y, DELTA);
        assertNotSame(points[0], centroid);
    }

    @Test
    void centroidOfSymmetricPointsIsOrigin() {
        Point centroid = Points.centroid(pts(-1, -2, 1, 2, -5, 7, 5, -7));
        assertEquals(0.0, centroid.x, DELTA);
        assertEquals(0.0, centroid.y, DELTA);
    }

    @Test
    void centroidDividesByPointCount() {
        Point third = Points.centroid(pts(0, 0, 1, 0, 0, 1));
        assertEquals(1.0 / 3, third.x, DELTA);
        assertEquals(1.0 / 3, third.y, DELTA);

        Point mixed = Points.centroid(pts(-10, 4, 2, -8, 2, 1));
        assertEquals(-2.0, mixed.x, DELTA);
        assertEquals(-1.0, mixed.y, DELTA);
    }

    @Test
    void centroidRejectsInvalidInput() {
        assertNull(Points.centroid(null));
        assertNull(Points.centroid(new Point[0]));
        assertNull(Points.centroid(new Point[]{Point.of(1, 1), null}));
    }

    // ---------- nearest ----------

    @Test
    void nearestReturnsSameInstanceWhetherClosestIsFirstMiddleOrLast() {
        Point origin = Point.of(0, 0);
        Point[] first = pts(1, 0, 5, 0, 9, 0);
        Point[] middle = pts(9, 0, 1, 0, 5, 0);
        Point[] last = pts(9, 0, 5, 0, 1, 0);
        assertSame(first[0], Points.nearest(origin, first));
        assertSame(middle[1], Points.nearest(origin, middle));
        assertSame(last[2], Points.nearest(origin, last));
    }

    @Test
    void nearestWithTieOrDuplicatesReturnsFirstOccurrence() {
        Point[] ring = pts(1, 0, -1, 0, 0, 1, 0, -1);
        assertSame(ring[0], Points.nearest(Point.of(0, 0), ring));

        Point[] duplicates = pts(5, 5, 2, 2, 2, 2);
        assertSame(duplicates[1], Points.nearest(Point.of(0, 0), duplicates));
    }

    @Test
    void nearestUsesEuclideanDistanceNotAxisDistance() {
        Point[] points = pts(3, 3, 0, 4);
        assertSame(points[1], Points.nearest(Point.of(0, 0), points));
    }

    @Test
    void nearestWhenQueryCoincidesWithAPointOrIsFarAway() {
        Point[] points = pts(3, 3, 4, 4, 5, 5);
        assertSame(points[1], Points.nearest(Point.of(4, 4), points));
        assertSame(points[2], Points.nearest(Point.of(1000, 1000), points));
        assertSame(points[0], Points.nearest(Point.of(-1000, -1000), points));
    }

    @Test
    void nearestRejectsInvalidInput() {
        Point origin = Point.of(0, 0);
        assertNull(Points.nearest(origin, null));
        assertNull(Points.nearest(origin, new Point[0]));
        assertNull(Points.nearest(origin, new Point[]{null}));
    }

    // ---------- checkNotEmpty ----------

    @Test
    void checkNotEmptyAcceptsOneOrManyPoints() {
        assertTrue(Points.checkNotEmpty(pts(0, 0)));
        assertTrue(Points.checkNotEmpty(pts(0, 0, 1, 1, 2, 2)));
    }

    @Test
    void checkNotEmptyRejectsNull() {
        assertFalse(Points.checkNotEmpty(null));
    }

    @Test
    void checkNotEmptyRejectsZeroLengthArray() {
        assertFalse(Points.checkNotEmpty(new Point[0]));
    }

    @Test
    void checkNotEmptyRejectsNullAtStartMiddleAndEnd() {
        Point p = Point.of(0, 0);
        assertFalse(Points.checkNotEmpty(new Point[]{null, p, p}));
        assertFalse(Points.checkNotEmpty(new Point[]{p, null, p}));
        assertFalse(Points.checkNotEmpty(new Point[]{p, p, null}));
    }

    @Test
    void checkNotEmptyTreatsNonEmptyArrayOfNullsAsNullElementNotEmpty() {
        assertFalse(Points.checkNotEmpty(new Point[2]));
    }
}
