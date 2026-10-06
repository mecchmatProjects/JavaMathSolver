package solver.geometry;

import static solver.core.Messages.*;
import static solver.core.Output.*;

/**
 * GEO: робота з  масивом точок Point[] (Lab 3, Tasks G2-G5).
 */
public final class Points {

    // Task G2: distance between two points
    public static double distance(Point a, Point b) {
        return Math.hypot(b.x - a.x, b.y - a.y);
    }

    // Task G3: bounding box -> {minX, maxX, minY, maxY}
    public static double[] boundingBox(Point[] points) {
        if (!checkNotEmpty(points)) {
            return null;
        }
        double minX = points[0].x, maxX = minX;
        double minY = points[0].y, maxY = minY;
        for (int i = 1; i < points.length; i++) {
            minX = Math.min(minX, points[i].x);
            maxX = Math.max(maxX, points[i].x);
            minY = Math.min(minY, points[i].y);
            maxY = Math.max(maxY, points[i].y);
        }
        return new double[]{minX, maxX, minY, maxY};
    }

    // Task G4: centroid Cx = sum(xi) / n, Cy = sum(yi) / n
    public static Point centroid(Point[] points) {
        if (!checkNotEmpty(points)) {
            return null;
        }
        double sumX = 0, sumY = 0;
        for (int i = 0; i < points.length; i++) {
            sumX += points[i].x;
            sumY += points[i].y;
        }
        return Point.of(sumX / points.length, sumY / points.length);
    }

    static boolean checkNotEmpty(Point[] points) {
        if (points == null || points.length == 0) {
            printError(ERR_POINTS_EMPTY);
            return false;
        }
        for (int i = 0; i < points.length; i++) {
            if (points[i] == null) {
                printError(ERR_POINTS_NULL_ELEMENT);
                return false;
            }
        }
        return true;
    }

    // Task G5: nearest point to p
    public static Point nearest(Point p, Point[] points) {
        if (!checkNotEmpty(points)) {
            return null;
        }
        Point best = points[0];
        double bestDistance = distance(p, best);
        for (int i = 1; i < points.length; i++) {
            double d = distance(p, points[i]);
            if (d < bestDistance) {
                bestDistance = d;
                best = points[i];
            }
        }
        return best;
    }
}
