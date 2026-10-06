package solver.geometry;

/**
 * GEO: точка на площині (Lab 3, Task G1).
 */
public final class Point {

    public double x;
    public double y;

    public static Point of(double x, double y) {
        Point point = new Point();
        point.x = x;
        point.y = y;
        return point;
    }
}
