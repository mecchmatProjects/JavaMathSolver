package solver.geometry;

import java.util.Locale;
import static solver.core.Output.*;
import static solver.core.Messages.*;
import static solver.geometry.GeoMath.EPS;

/**
 * GEO: intersections and bounding rectangles.
 */
public final class Intersections {

    private Intersections() {
    }

    // Task 14: Circle and segment intersection
    public static void calculateCircleSegmentIntersections(double radius, double lineX, double yMin, double lengthC) {
        if (radius < 0) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        double yMax = yMin + lengthC * lengthC;
        double discr = radius * radius - lineX * lineX;
        if (discr < -EPS) {
            printResult(0);
            return;
        }
        if (Math.abs(discr) <= EPS) {
            int count = (0 >= yMin - EPS && 0 <= yMax + EPS) ? 1 : 0;
            printResult(count);
            return;
        }
        double y = Math.sqrt(discr);
        int count = 0;
        if (y >= yMin - EPS && y <= yMax + EPS) {
            count++;
        }
        if (-y >= yMin - EPS && -y <= yMax + EPS) {
            count++;
        }
        printResult(count);
    }

    // Task 15: Circle and line intersection classification
    public static void calculateCircleLine(double centerX, double centerY, double radius, double lineA, double lineB, double lineC) {
        if (radius < 0 || (lineA == 0 && lineB == 0)) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        double dist = Math.abs(lineA * centerX + lineB * centerY + lineC) / Math.hypot(lineA, lineB);
        if (Math.abs(dist - radius) < EPS) {
            printResult("One point of tangency");
        } else if (dist < radius) {
            printResult("Two points of intersection");
        } else {
            printResult("No common points");
        }
    }

    // Task 16: Intersection of two circles
    public static void calculateCirclesIntersect(double x1, double y1, double r1, double x2, double y2, double r2) {
        if (r1 < 0 || r2 < 0) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        printResult(Circle.intersects(Point.of(x1, y1).distanceTo(Point.of(x2, y2)), r1, r2));
    }

    // Task 17: Intersection of two squares
    public static void calculateSquaresIntersect(double x1, double y1, double side1, double x2, double y2, double side2) {
        if (side1 < 0 || side2 < 0) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        double leftX = Math.max(x1, x2);
        double bottomY = Math.max(y1, y2);
        double rightX = Math.min(x1 + side1, x2 + side2);
        double topY = Math.min(y1 + side1, y2 + side2);
        if (leftX > rightX || bottomY > topY) {
            printResult("Squares do not intersect");
        } else {
            printResult(String.format(Locale.ROOT, "Intersect rect: Bottom-Left (%f, %f), Top-Right (%f, %f)", leftX, bottomY, rightX, topY));
        }
    }

    // Task 18: Minimum bounding box for two rectangles
    public static void calculateRectBoundingBox(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        if (x1 > x2 || y1 > y2 || x3 > x4 || y3 > y4) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        double minX = Math.min(x1, x3);
        double minY = Math.min(y1, y3);
        double maxX = Math.max(x2, x4);
        double maxY = Math.max(y2, y4);
        printResult(String.format(Locale.ROOT, "Bounding box: Bottom-Left (%f, %f), Top-Right (%f, %f)", minX, minY, maxX, maxY));
    }
}
