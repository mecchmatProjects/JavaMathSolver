package solver.geometry;

import java.util.Locale;
import static solver.core.Output.*;
import static solver.core.Messages.*;

/**
 * GEO: периметр і опуклість багатокутника.
 */
public final class Polygon {

    private Polygon() {
    }

    // Polygon task: perimeter and convexity
    public static void processPolygon(double... coords) {
        if (coords == null || coords.length < 6 || coords.length % 2 != 0) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        int count = coords.length / 2;
        double perimeter = 0;
        for (int i = 0; i < count; i++) {
            double x1 = coords[2 * i], y1 = coords[2 * i + 1];
            double x2 = coords[2 * ((i + 1) % count)], y2 = coords[2 * ((i + 1) % count) + 1];
            perimeter += Math.hypot(x2 - x1, y2 - y1);
        }

        int initialSign = 0;
        boolean signConsistent = true;
        double turningSum = 0;
        for (int i = 0; i < count; i++) {
            double x1 = coords[2 * i], y1 = coords[2 * i + 1];
            double x2 = coords[2 * ((i + 1) % count)], y2 = coords[2 * ((i + 1) % count) + 1];
            double x3 = coords[2 * ((i + 2) % count)], y3 = coords[2 * ((i + 2) % count) + 1];

            double edge1x = x2 - x1, edge1y = y2 - y1;
            double edge2x = x3 - x2, edge2y = y3 - y2;
            double crossProduct = edge1x * edge2y - edge1y * edge2x;
            double dotProduct = edge1x * edge2x + edge1y * edge2y;
            double len1 = Math.hypot(edge1x, edge1y);
            double len2 = Math.hypot(edge2x, edge2y);
            turningSum += Math.atan2(crossProduct, dotProduct);

            if (len1 * len2 > 0 && Math.abs(crossProduct) <= 1e-9 * (len1 * len2)) {
                continue;
            }
            int sign = crossProduct > 0 ? 1 : -1;
            if (initialSign == 0) {
                initialSign = sign;
            } else if (sign != initialSign) {
                signConsistent = false;
                break;
            }
        }
        boolean turningSumOk = Math.abs(Math.abs(turningSum) - 2 * Math.PI) < 1e-6;
        boolean isConvex = signConsistent && initialSign != 0 && turningSumOk;

        printResult(String.format(Locale.ROOT, "perimeter=%f, convex=%b", perimeter, isConvex));
    }
}
