package solver.geometry;

/**
 * GEO (Lab 5): triangle built by composition of three points.
 * Degenerate triangles (all vertices on one line) are allowed; their area is 0.
 */
public final class Triangle implements AreaFigure {

    private final Point a;
    private final Point b;
    private final Point c;

    private Triangle(Point a, Point b, Point c) {
        if (a == null || b == null || c == null) {
            throw new IllegalArgumentException("Triangle vertices must not be null");
        }
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public static Triangle of(Point a, Point b, Point c) {
        return new Triangle(a, b, c);
    }

    public Point a() {
        return a;
    }

    public Point b() {
        return b;
    }

    public Point c() {
        return c;
    }

    public Point[] vertices() {
        return new Point[]{a, b, c};
    }

    /** Sides in order AB, BC, CA. */
    public Segment[] sides() {
        return new Segment[]{Segment.of(a, b), Segment.of(b, c), Segment.of(c, a)};
    }

    @Override
    public double area() {
        return Math.abs(GeoMath.cross(a, b, c)) / 2;
    }

    @Override
    public double perimeter() {
        return a.distanceTo(b) + b.distanceTo(c) + c.distanceTo(a);
    }

    /** True if the vertices are collinear (within the shared tolerance). */
    public boolean isDegenerate() {
        return GeoMath.orient(a, b, c) == 0;
    }

    /** Intersection point of the medians. */
    public Point centroid() {
        return Point.of((a.x + b.x + c.x) / 3, (a.y + b.y + c.y) / 3);
    }

    /** Task 1: whether p lies inside the triangle or on its boundary. */
    @Override
    public boolean contains(Point p) {
        if (isDegenerate()) {
            for (Segment side : sides()) {
                if (side.contains(p)) {
                    return true;
                }
            }
            return false;
        }
        int o1 = GeoMath.orient(a, b, p);
        int o2 = GeoMath.orient(b, c, p);
        int o3 = GeoMath.orient(c, a, p);
        boolean hasLeft = o1 > 0 || o2 > 0 || o3 > 0;
        boolean hasRight = o1 < 0 || o2 < 0 || o3 < 0;
        return !(hasLeft && hasRight);
    }

    @Override
    public double distanceTo(Point p) {
        if (contains(p)) {
            return 0.0;
        }
        double best = Double.POSITIVE_INFINITY;
        for (Segment side : sides()) {
            best = Math.min(best, side.distanceTo(p));
        }
        return best;
    }

    /** The same triangle as a polygon; throws for a degenerate triangle. */
    public Polygon toPolygon() {
        return Polygon.of(a, b, c);
    }

    @Override
    public Triangle translate(Vector2D v) {
        return new Triangle(a.translate(v), b.translate(v), c.translate(v));
    }

    @Override
    public Triangle rotate(double angle, Point center) {
        return new Triangle(a.rotate(angle, center), b.rotate(angle, center), c.rotate(angle, center));
    }

    @Override
    public Triangle scale(double factor, Point center) {
        return new Triangle(a.scale(factor, center), b.scale(factor, center), c.scale(factor, center));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Triangle)) {
            return false;
        }
        Triangle other = (Triangle) o;
        return a.equals(other.a) && b.equals(other.b) && c.equals(other.c);
    }

    @Override
    public int hashCode() {
        return 31 * (31 * a.hashCode() + b.hashCode()) + c.hashCode();
    }

    @Override
    public String toString() {
        return "Triangle(" + a + ", " + b + ", " + c + ")";
    }
}
