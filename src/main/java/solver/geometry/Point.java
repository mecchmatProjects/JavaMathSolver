package solver.geometry;

/**
 * GEO: точка на площині (Lab 3, Task G1).
 */
public final class Point {

    public final double x;
    public final double y;

    private Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static Point of(double x, double y) {
        return new Point(x, y);
    }
}
