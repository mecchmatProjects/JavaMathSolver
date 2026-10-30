package solver.geometry;


public final class Line {

    private static final double EPS = 1e-9;

    private final Point point;
    private final Vector2D direction;

    private Line(Point point, Vector2D direction) {
        if (direction.lengthSquared() == 0) {
            throw new IllegalArgumentException("Line direction must be non-zero");
        }
        this.point = point;
        this.direction = direction;
    }

    public static Line through(Point p, Point q) {
        if (p.x == q.x && p.y == q.y) {
            throw new IllegalArgumentException("Points must be distinct");
        }
        return new Line(p, Vector2D.between(p, q));
    }

    public static Line of(Point point, Vector2D direction) {
        return new Line(point, direction);
    }

    public static Line fromEquation(double a, double b, double c) {
        if (a == 0 && b == 0) {
            throw new IllegalArgumentException("a and b cannot both be zero");
        }
        double d = a * a + b * b;
        Point p = Point.of(-a * c / d, -b * c / d);
        return new Line(p, Vector2D.of(-b, a));
    }

    public Point point() {
        return point;
    }

    public Vector2D direction() {
        return direction;
    }

    public double[] coefficients() {
        Vector2D n = direction.perpendicular().normalize();
        double c = -(n.x * point.x + n.y * point.y);
        return new double[]{n.x, n.y, c};
    }

    public double slope() {
        if (direction.x == 0) {
            return Double.POSITIVE_INFINITY;
        }
        return direction.y / direction.x;
    }

    public double distanceTo(Point p) {
        return Math.abs(direction.cross(Vector2D.between(point, p))) / direction.length();
    }

    public boolean contains(Point p) {
        return distanceTo(p) <= EPS;
    }

    public Point projection(Point p) {
        double t = Vector2D.between(point, p).dot(direction) / direction.lengthSquared();
        return point.translate(direction.scale(t));
    }

    public boolean isParallelTo(Line other) {
        return direction.isParallelTo(other.direction, EPS);
    }

    public boolean isPerpendicularTo(Line other) {
        return Math.abs(direction.dot(other.direction)) <= EPS * direction.length() * other.direction.length();
    }

    public boolean coincidesWith(Line other) {
        return isParallelTo(other) && contains(other.point);
    }

    public Point intersection(Line other) {
        double denom = direction.cross(other.direction);
        if (isParallelTo(other)) {
            return null;
        }
        double t = Vector2D.between(point, other.point).cross(other.direction) / denom;
        return point.translate(direction.scale(t));
    }

    public double distanceTo(Line other) {
        return isParallelTo(other) ? distanceTo(other.point) : 0.0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Line)) {
            return false;
        }
        Line other = (Line) o;
        return point.equals(other.point) && direction.equals(other.direction);
    }

    @Override
    public String toString() {
        return "Line(" + point + ", dir=" + direction + ")";
    }
}