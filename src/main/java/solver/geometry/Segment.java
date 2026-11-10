package solver.geometry;

import static solver.geometry.GeoMath.EPS;

/**
 * GEO (Lab 5): line segment [a, b] built by composition of two points.
 * A segment with a == b is allowed and behaves like a single point.
 */
public final class Segment implements Figure {

    private final Point a;
    private final Point b;

    private Segment(Point a, Point b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("Segment endpoints must not be null");
        }
        this.a = a;
        this.b = b;
    }

    public static Segment of(Point a, Point b) {
        return new Segment(a, b);
    }

    public static Segment of(double x1, double y1, double x2, double y2) {
        return new Segment(Point.of(x1, y1), Point.of(x2, y2));
    }

    public Point a() {
        return a;
    }

    public Point b() {
        return b;
    }

    public double length() {
        return a.distanceTo(b);
    }

    /** The vector from a to b (connection between a segment and a vector). */
    public Vector2D toVector() {
        return a.vectorTo(b);
    }

    public Point midpoint() {
        return a.midpoint(b);
    }

    public boolean isDegenerate() {
        return length() <= EPS;
    }

    /** The line through the segment; throws for a degenerate segment. */
    public Line toLine() {
        return Line.through(a, b);
    }

    @Override
    public boolean contains(Point p) {
        return GeoMath.onSegment(a, b, p);
    }

    @Override
    public double distanceTo(Point p) {
        return closestPointTo(p).distanceTo(p);
    }

    /** The point of the segment closest to p (projection clamped to [a, b]). */
    public Point closestPointTo(Point p) {
        Vector2D ab = toVector();
        double len2 = ab.lengthSquared();
        if (len2 == 0) {
            return a;
        }
        double t = Math.max(0.0, Math.min(1.0, a.vectorTo(p).dot(ab) / len2));
        return a.translate(ab.scale(t));
    }

    /** Collinearity: whether p lies on the line through this segment. */
    public boolean isCollinearWith(Point p) {
        return isDegenerate() || GeoMath.orient(a, b, p) == 0;
    }

    /** Collinearity: whether both segments lie on one line. */
    public boolean isCollinearWith(Segment s) {
        if (isDegenerate()) {
            return s.isCollinearWith(a);
        }
        return isCollinearWith(s.a) && isCollinearWith(s.b);
    }

    /** Whether the segments are parallel (degenerate segments have no direction and are never parallel). */
    public boolean isParallelTo(Segment s) {
        if (isDegenerate() || s.isDegenerate()) {
            return false;
        }
        return toVector().isParallelTo(s.toVector(), EPS);
    }

    public boolean intersects(Segment s) {
        return GeoMath.segmentsIntersect(a, b, s.a, s.b);
    }

    /** Whether the segment touches or crosses the line. */
    public boolean intersects(Line line) {
        if (line.contains(a) || line.contains(b)) {
            return true;
        }
        Vector2D d = line.direction();
        double sideA = d.cross(line.point().vectorTo(a));
        double sideB = d.cross(line.point().vectorTo(b));
        return (sideA > 0) != (sideB > 0);
    }

    /**
     * The common point of two segments, or null if they do not meet.
     * Throws if the segments overlap along a piece of positive length.
     */
    public Point intersection(Segment s) {
        if (!intersects(s)) {
            return null;
        }
        Vector2D d1 = toVector();
        Vector2D d2 = s.toVector();
        double denom = d1.cross(d2);
        if (Math.abs(denom) > EPS * d1.length() * d2.length()) {
            double t = a.vectorTo(s.a).cross(d2) / denom;
            return a.translate(d1.scale(t));
        }
        return singleCommonPoint(new Point[]{a, b, s.a, s.b}, this, s);
    }

    /**
     * The common point of the segment and the line, or null if they do not meet.
     * Throws if the segment lies on the line.
     */
    public Point intersection(Line line) {
        if (!intersects(line)) {
            return null;
        }
        boolean onA = line.contains(a);
        boolean onB = line.contains(b);
        if (onA && onB) {
            if (isDegenerate()) {
                return a;
            }
            throw new IllegalArgumentException("Segment lies on the line: infinitely many intersection points");
        }
        if (onA) {
            return a;
        }
        if (onB) {
            return b;
        }
        return line.intersection(toLine());
    }

    private static Point singleCommonPoint(Point[] candidates, Segment s1, Segment s2) {
        Point found = null;
        for (Point p : candidates) {
            if (s1.contains(p) && s2.contains(p)) {
                if (found == null) {
                    found = p;
                } else if (!found.approxEquals(p, EPS * Math.max(1.0, s1.length() + s2.length()))) {
                    throw new IllegalArgumentException("Segments overlap: infinitely many intersection points");
                }
            }
        }
        return found;
    }

    @Override
    public Segment translate(Vector2D v) {
        return new Segment(a.translate(v), b.translate(v));
    }

    @Override
    public Segment rotate(double angle, Point center) {
        return new Segment(a.rotate(angle, center), b.rotate(angle, center));
    }

    @Override
    public Segment scale(double factor, Point center) {
        return new Segment(a.scale(factor, center), b.scale(factor, center));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Segment)) {
            return false;
        }
        Segment other = (Segment) o;
        return a.equals(other.a) && b.equals(other.b);
    }

    @Override
    public int hashCode() {
        return 31 * a.hashCode() + b.hashCode();
    }

    @Override
    public String toString() {
        return "Segment(" + a + ", " + b + ")";
    }
}
