package solver.geometry;

/**
 * GEO: shared constants and primitives for the geometry package
 * (EPS tolerance, orientation of three points, segment checks).
 */
final class GeoMath {

    /** Shared tolerance for floating-point comparisons. */
    static final double EPS = 1e-9;

    private GeoMath() {
    }

    /** Clamps the value to [-1, 1] (argument for acos). */
    static double clamp(double v) {
        return Math.max(-1.0, Math.min(1.0, v));
    }

    /** Cross product (b - a) x (c - a). */
    static double cross(Point a, Point b, Point c) {
        return a.vectorTo(b).cross(a.vectorTo(c));
    }

    /** Orientation of c relative to a->b with a tolerance relative to the edge lengths: 1 left, -1 right, 0 collinear. */
    static int orient(Point a, Point b, Point c) {
        double c0 = cross(a, b, c);
        double scale = a.distanceTo(b) * a.distanceTo(c);
        if (Math.abs(c0) <= EPS * scale) {
            return 0;
        }
        return c0 > 0 ? 1 : -1;
    }

    /** Whether point p lies on segment [a, b]. */
    static boolean onSegment(Point a, Point b, Point p) {
        double tol = EPS * a.distanceTo(b);
        return orient(a, b, p) == 0
                && p.x >= Math.min(a.x, b.x) - tol && p.x <= Math.max(a.x, b.x) + tol
                && p.y >= Math.min(a.y, b.y) - tol && p.y <= Math.max(a.y, b.y) + tol;
    }

    /** Whether segments [p1, p2] and [p3, p4] intersect (touching included). */
    static boolean segmentsIntersect(Point p1, Point p2, Point p3, Point p4) {
        int d1 = orient(p3, p4, p1);
        int d2 = orient(p3, p4, p2);
        int d3 = orient(p1, p2, p3);
        int d4 = orient(p1, p2, p4);
        if (d1 * d2 < 0 && d3 * d4 < 0) {
            return true;
        }
        return onSegment(p3, p4, p1) || onSegment(p3, p4, p2) || onSegment(p1, p2, p3) || onSegment(p1, p2, p4);
    }
}
