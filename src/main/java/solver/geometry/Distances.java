package solver.geometry;

import java.util.Locale;
import static solver.core.Output.*;

/**
 * GEO: distances, midpoint, collinearity, quadrant.
 */
public final class Distances {

    private Distances() {
    }

    // GEOMETRY (GEO-01 ... GEO-06)
    public static void calculateDistance(double x1, double y1, double x2, double y2) {
        printResult(Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)));
    }

    public static void calculateOriginDistance(double x, double y) {
        printResult(Math.sqrt(x * x + y * y));
    }

    public static void calculateManhattanDistance(double x1, double y1, double x2, double y2) {
        printResult(Math.abs(x2 - x1) + Math.abs(y2 - y1));
    }

    public static void calculateMidpoint(double x1, double y1, double x2, double y2) {
        Point mid = Point.of(x1, y1).midpoint(Point.of(x2, y2));
        printResult(String.format(Locale.ROOT, "(%f, %f)", mid.x, mid.y));
    }

    public static void calculateCollinear(double x1, double y1, double x2, double y2, double x3, double y3) {
        Point a = Point.of(x1, y1);
        Point b = Point.of(x2, y2);
        Point c = Point.of(x3, y3);
        if (a.distanceTo(b) == 0 || a.distanceTo(c) == 0 || b.distanceTo(c) == 0) {
            printResult(true);
            return;
        }
        printResult(GeoMath.orient(a, b, c) == 0);
    }

    public static void calculateQuadrant(double x, double y) {
        if (x == 0 && y == 0) {
            printResult("ORIGIN");
        } else if (x == 0 || y == 0) {
            printResult("AXIS");
        } else if (x > 0 && y > 0) {
            printResult("I");
        } else if (x < 0 && y > 0) {
            printResult("II");
        } else if (x < 0 && y < 0) {
            printResult("III");
        } else {
            printResult("IV");
        }
    }
}
