package solver.geometry;

public final class Point implements Figure {

    public final double x;
    public final double y;

    private Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static Point of(double x, double y) {
        return new Point(x, y);
    }

    @Override
    public double distanceTo(Point other) {
        return Math.hypot(other.x - x, other.y - y);
    }

    public Vector2D vectorTo(Point other) {
        return Vector2D.between(this, other);
    }

    @Override
    public Point translate(Vector2D v) {
        return new Point(x + v.x, y + v.y);
    }

    @Override
    public Point rotate(double angle, Point center) {
        return center.translate(center.vectorTo(this).rotate(angle));
    }

    @Override
    public Point scale(double factor, Point center) {
        return center.translate(center.vectorTo(this).scale(factor));
    }

    /** A point "contains" another point when they coincide (within {@link GeoMath#EPS}). */
    @Override
    public boolean contains(Point p) {
        return approxEquals(p, GeoMath.EPS);
    }

    public Point midpoint(Point other) {
        return new Point((x + other.x) / 2, (y + other.y) / 2);
    }

    public boolean approxEquals(Point other, double eps) {
        return Math.abs(x - other.x) <= eps && Math.abs(y - other.y) <= eps;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Point)) {
            return false;
        }
        Point other = (Point) o;
        return Double.compare(x, other.x) == 0 && Double.compare(y, other.y) == 0;
    }

    @Override
    public int hashCode() {
        return 31 * Double.hashCode(x) + Double.hashCode(y);
    }

    @Override
    public String toString() {
        return "Point(" + x + ", " + y + ")";
    }
}
