package solver.geometry;


public final class Circle {

    private static final double EPS = 1e-9;

    private final Point center;
    private final double radius;

    private Circle(Point center, double radius) {
        if (!(radius >= 0) || Double.isInfinite(radius)) {
            throw new IllegalArgumentException("Radius must be a finite non-negative number: " + radius);
        }
        this.center = center;
        this.radius = radius;
    }

    public static Circle of(Point center, double radius) {
        return new Circle(center, radius);
    }

    public static Circle of(double cx, double cy, double radius) {
        return new Circle(Point.of(cx, cy), radius);
    }

    public Point center() {
        return center;
    }

    public double radius() {
        return radius;
    }

    public double area() {
        return Math.PI * radius * radius;
    }

    public double circumference() {
        return 2 * Math.PI * radius;
    }

    public double diameter() {
        return 2 * radius;
    }

    public boolean contains(Point p) {
        return center.distanceTo(p) <= radius + EPS;
    }

    public boolean onBoundary(Point p) {
        return Math.abs(center.distanceTo(p) - radius) <= EPS;
    }

    public double distanceTo(Point p) {
        return Math.max(0.0, center.distanceTo(p) - radius);
    }

    public double distanceToBoundary(Point p) {
        return Math.abs(center.distanceTo(p) - radius);
    }

    public boolean intersects(Circle c) {
        double d = center.distanceTo(c.center);
        return d <= radius + c.radius + EPS && d >= Math.abs(radius - c.radius) - EPS;
    }

    public Point[] intersection(Circle c) {
        double d = center.distanceTo(c.center);
        if (d <= EPS && Math.abs(radius - c.radius) <= EPS) {
            if (radius <= EPS) {
                return new Point[]{center};
            }
            throw new IllegalArgumentException("Circles coincide: infinitely many intersection points");
        }
        if (d > radius + c.radius + EPS || d < Math.abs(radius - c.radius) - EPS || d <= EPS) {
            return new Point[0];
        }
        double a = (radius * radius - c.radius * c.radius + d * d) / (2 * d);
        double h2 = radius * radius - a * a;
        Vector2D u = Vector2D.between(center, c.center).scale(1 / d);
        Point base = center.translate(u.scale(a));
        if (h2 <= EPS * Math.max(1.0, radius * radius)) {
            return new Point[]{base};
        }
        Vector2D offset = u.perpendicular().scale(Math.sqrt(h2));
        return new Point[]{base.translate(offset), base.translate(offset.negate())};
    }

    public Point[] intersection(Line line) {
        Point foot = line.projection(center);
        double dist = center.distanceTo(foot);
        if (dist > radius + EPS) {
            return new Point[0];
        }
        double h2 = radius * radius - dist * dist;
        if (h2 <= EPS * Math.max(1.0, radius * radius)) {
            return new Point[]{foot};
        }
        Vector2D offset = line.direction().normalize().scale(Math.sqrt(h2));
        return new Point[]{foot.translate(offset.negate()), foot.translate(offset)};
    }

    public double intersectionArea(Circle c) {
        double d = center.distanceTo(c.center);
        double r1 = radius;
        double r2 = c.radius;
        if (d >= r1 + r2) {
            return 0.0;
        }
        if (d <= Math.abs(r1 - r2)) {
            double r = Math.min(r1, r2);
            return Math.PI * r * r;
        }
        double alpha = Math.acos(clamp((d * d + r1 * r1 - r2 * r2) / (2 * d * r1)));
        double beta = Math.acos(clamp((d * d + r2 * r2 - r1 * r1) / (2 * d * r2)));
        return r1 * r1 * (alpha - Math.sin(2 * alpha) / 2) + r2 * r2 * (beta - Math.sin(2 * beta) / 2);
    }

    private static double clamp(double v) {
        return Math.max(-1.0, Math.min(1.0, v));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Circle)) {
            return false;
        }
        Circle other = (Circle) o;
        return center.equals(other.center) && Double.compare(radius, other.radius) == 0;
    }

    @Override
    public String toString() {
        return "Circle(" + center + ", r=" + radius + ")";
    }
}
