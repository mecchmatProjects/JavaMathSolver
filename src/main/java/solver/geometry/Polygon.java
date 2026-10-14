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
        Point[] polygon = new Point[coords.length / 2];
        for (int i = 0; i < polygon.length; i++) {
            polygon[i] = Point.of(coords[2 * i], coords[2 * i + 1]);
        }
        double perimeter = Polygon2D.perimeter(polygon);
        if (Double.isNaN(perimeter)) {
            return;
        }
        boolean isConvex = Polygon2D.isConvex(polygon);

        printResult(String.format(Locale.ROOT, "perimeter=%f, convex=%b", perimeter, isConvex));
    }
}
