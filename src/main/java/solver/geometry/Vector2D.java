package solver.geometry;


public final class Vector2D {

    public static final Vector2D ZERO = new Vector2D(0, 0);

    public final double x;
    public final double y;

    private Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static Vector2D of(double x, double y) {
        return new Vector2D(x, y);
    }

    public static Vector2D between(Point from, Point to) {
        return new Vector2D(to.x - from.x, to.y - from.y);
    }

    public Vector2D add(Vector2D o) {
        return new Vector2D(x + o.x, y + o.y);
    }

    public Vector2D subtract(Vector2D o) {
        return new Vector2D(x - o.x, y - o.y);
    }

    public Vector2D scale(double k) {
        return new Vector2D(x * k, y * k);
    }

    public Vector2D negate() {
        return new Vector2D(-x, -y);
    }

    public double dot(Vector2D o) {
        return x * o.x + y * o.y;
    }

    public double cross(Vector2D o) {
        return x * o.y - y * o.x;
    }

    public double length() {
        return Math.hypot(x, y);
    }

    public double lengthSquared() {
        return x * x + y * y;
    }

    public Vector2D normalize() {
        double len = length();
        if (len == 0) {
            throw new ArithmeticException("Cannot normalize zero vector");
        }
        return new Vector2D(x / len, y / len);
    }

    public Vector2D perpendicular() {
        return new Vector2D(-y, x);
    }

    public Vector2D rotate(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new Vector2D(x * cos - y * sin, x * sin + y * cos);
    }

    public double angleTo(Vector2D o) {
        if (lengthSquared() == 0 || o.lengthSquared() == 0) {
            throw new ArithmeticException("Angle with zero vector is undefined");
        }
        return Math.atan2(Math.abs(cross(o)), dot(o));
    }

    public boolean isParallelTo(Vector2D o, double eps) {
        return Math.abs(cross(o)) <= eps * length() * o.length();
    }

    public boolean approxEquals(Vector2D o, double eps) {
        return Math.abs(x - o.x) <= eps && Math.abs(y - o.y) <= eps;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Vector2D)) {
            return false;
        }
        Vector2D other = (Vector2D) o;
        return Double.compare(x, other.x) == 0 && Double.compare(y, other.y) == 0;
    }

    @Override
    public String toString() {
        return "Vector2D(" + x + ", " + y + ")";
    }
}