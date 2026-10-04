package solver.geometry;

import java.util.Locale;
import static solver.core.Output.*;

/**
 * GEO: відстані, середина відрізка, колінеарність, чверть.
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
        double midX = (x1 + x2) / 2;
        double midY = (y1 + y2) / 2;
        printResult(String.format(Locale.ROOT, "(%f, %f)", midX, midY));
    }

    public static void calculateCollinear(double x1, double y1, double x2, double y2, double x3, double y3) {
        double crossProduct = (x2 - x1) * (y3 - y1) - (y2 - y1) * (x3 - x1);
        double len1 = Math.hypot(x2 - x1, y2 - y1);
        double len2 = Math.hypot(x3 - x1, y3 - y1);
        double len3 = Math.hypot(x3 - x2, y3 - y2);
        if (len1 == 0 || len2 == 0 || len3 == 0) {
            printResult(true);
            return;
        }
        double scale = len1 * len2;
        printResult(Math.abs(crossProduct) <= 1e-9 * scale);
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
