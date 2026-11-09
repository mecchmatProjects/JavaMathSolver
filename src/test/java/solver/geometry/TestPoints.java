package solver.geometry;

/**
 * Shared helpers for geometry package tests.
 */
final class TestPoints {

    private TestPoints() {
    }

    /** pts(x0, y0, x1, y1, ...) -> array of points. */
    static Point[] pts(double... xy) {
        Point[] points = new Point[xy.length / 2];
        for (int i = 0; i < points.length; i++) {
            points[i] = Point.of(xy[2 * i], xy[2 * i + 1]);
        }
        return points;
    }
}
