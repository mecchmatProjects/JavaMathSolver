package solver.geometry;

import java.util.Arrays;

import static solver.core.Messages.*;
import static solver.core.Output.*;
import static solver.geometry.GeoMath.*;

/**
 * GEO: polygon as a Point[] array of vertices in traversal order (Lab 3, Task G6).
 */
public final class Polygon2D {

    // Task G6: perimeter
    public static double perimeter(Point[] polygon) {
        polygon = open(polygon);
        if (!check(polygon)) {
            return Double.NaN;
        }
        double sum = 0;
        for (int i = 0; i < polygon.length; i++) {
            sum += polygon[i].distanceTo(polygon[(i + 1) % polygon.length]);
        }
        return sum;
    }

    // Task G6: area
    public static double area(Point[] polygon) {
        polygon = open(polygon);
        if (!check(polygon)) {
            return Double.NaN;
        }
        return Math.abs(signedArea(polygon));
    }

    // Task G6: is the polygon convex
    public static boolean isConvex(Point[] polygon) {
        polygon = open(polygon);
        if (!check(polygon)) {
            return false;
        }
        int n = polygon.length;
        int sign = 0;
        for (int i = 0; i < n; i++) {
            int c = orient(polygon[i], polygon[(i + 1) % n], polygon[(i + 2) % n]);
            if (c == 0) {
                continue;
            }
            if (sign != 0 && c != sign) {
                return false;
            }
            sign = c;
        }
        return sign != 0 && isSimple(polygon);
    }

    // Task G6: convex hull
    public static Point[] convexHull(Point[] points) {
        if (!Points.checkNotEmpty(points)) {
            return null;
        }
        if (points.length < 3) {
            printError(ERR_HULL_MIN_POINTS);
            return null;
        }
        Point[] sorted = points.clone();
        for (int i = 1; i < sorted.length; i++) {
            Point key = sorted[i];
            int j = i - 1;
            while (j >= 0 && (sorted[j].x > key.x || (sorted[j].x == key.x && sorted[j].y > key.y))) {
                sorted[j + 1] = sorted[j];
                j--;
            }
            sorted[j + 1] = key;
        }
        Point[] hull = new Point[2 * sorted.length];
        int k = 0;
        for (int i = 0; i < sorted.length; i++) {
            while (k >= 2 && orient(hull[k - 2], hull[k - 1], sorted[i]) <= 0) {
                k--;
            }
            hull[k++] = sorted[i];
        }
        for (int i = sorted.length - 2, lower = k + 1; i >= 0; i--) {
            while (k >= lower && orient(hull[k - 2], hull[k - 1], sorted[i]) <= 0) {
                k--;
            }
            hull[k++] = sorted[i];
        }
        if (k < 4) {
            printError(ERR_HULL_DEGENERATE);
            return null;
        }
        return Arrays.copyOf(hull, k - 1);
    }

    public static boolean isSimple(Point[] polygon) {
        polygon = open(polygon);
        if (!check(polygon)) {
            return false;
        }
        int n = polygon.length;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                Point a1 = polygon[i];
                Point a2 = polygon[(i + 1) % n];
                Point b1 = polygon[j];
                Point b2 = polygon[(j + 1) % n];
                if (j == i + 1 || (i == 0 && j == n - 1)) {
                    Point shared = j == i + 1 ? a2 : a1;
                    Point farA = j == i + 1 ? a1 : a2;
                    Point farB = j == i + 1 ? b2 : b1;
                    double dot = shared.vectorTo(farA).dot(shared.vectorTo(farB));
                    if (orient(farA, shared, farB) == 0 && dot > 0) {
                        return false;
                    }
                } else if (segmentsIntersect(a1, a2, b1, b2)) {
                    return false;
                }
            }
        }
        return true;
    }

    // Task G6: triangulation
    public static Point[][] triangulate(Point[] polygon) {
        polygon = open(polygon);
        if (!check(polygon)) {
            return null;
        }
        if (!isSimple(polygon)) {
            printError(ERR_POLYGON_NOT_SIMPLE);
            return null;
        }
        Point[] ordered = polygon.clone();
        if (signedArea(ordered) < 0) {
            reverse(ordered);
        }
        int count = ordered.length;
        Point[][] triangles = new Point[ordered.length][];
        int triangleCount = 0;
        while (count > 3) {
            boolean clipped = false;
            for (int i = 0; i < count && !clipped; i++) {
                Point prev = ordered[(i + count - 1) % count];
                Point cur = ordered[i];
                Point next = ordered[(i + 1) % count];
                int c = orient(prev, cur, next);
                if (c == 0 || (c > 0 && noVertexInside(ordered, count, prev, cur, next))) {
                    if (c != 0) {
                        triangles[triangleCount++] = new Point[]{prev, cur, next};
                    }
                    System.arraycopy(ordered, i + 1, ordered, i, count - 1 - i);
                    count--;
                    clipped = true;
                }
            }
            if (!clipped) {
                printError(ERR_POLYGON_NOT_TRIANGULABLE);
                return null;
            }
        }
        if (orient(ordered[0], ordered[1], ordered[2]) != 0) {
            triangles[triangleCount++] = new Point[]{ordered[0], ordered[1], ordered[2]};
        }
        return Arrays.copyOf(triangles, triangleCount);
    }

    // Task G6: affine transformations (translation, scaling, rotation)
    public static Point[] translate(Point[] polygon, double dx, double dy) {
        return transform(polygon, 1, 0, 0, 1, dx, dy);
    }

    public static Point[] scale(Point[] polygon, double sx, double sy) {
        return transform(polygon, sx, 0, 0, sy, 0, 0);
    }

    /** Task G6: rotates the polygon counter-clockwise by {@code angle} radians about the origin (0, 0). */
    public static Point[] rotate(Point[] polygon, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return transform(polygon, cos, -sin, sin, cos, 0, 0);
    }

    // Task G6: is the given point inside the polygon
    public static boolean contains(Point[] polygon, Point p) {
        polygon = open(polygon);
        if (!check(polygon) || !Points.checkPoint(p)) {
            return false;
        }
        boolean inside = false;
        for (int i = 0, j = polygon.length - 1; i < polygon.length; j = i++) {
            Point a = polygon[i];
            Point b = polygon[j];
            if (onSegment(a, b, p)) {
                return true;
            }
            if ((a.y > p.y) != (b.y > p.y) && p.x < (b.x - a.x) * (p.y - a.y) / (b.y - a.y) + a.x) {
                inside = !inside;
            }
        }
        return inside;
    }

    /**
     * Task G6: intersection of two polygons.
     * The result is a set of convex fragments (triangle-pair intersections), not a single merged contour;
     * adjacent fragments may share edges. Their total area equals the intersection area.
     */
    public static Point[][] intersection(Point[] first, Point[] second) {
        Point[][] firstTriangles = triangulate(first);
        Point[][] secondTriangles = triangulate(second);
        if (firstTriangles == null || secondTriangles == null) {
            return null;
        }
        Point[][] pieces = new Point[firstTriangles.length * secondTriangles.length][];
        int pieceCount = 0;
        for (int i = 0; i < firstTriangles.length; i++) {
            for (int j = 0; j < secondTriangles.length; j++) {
                Point[] clipped = firstTriangles[i];
                for (int k = 0; k < 3 && clipped.length > 0; k++) {
                    clipped = clipByEdge(clipped, secondTriangles[j][k], secondTriangles[j][(k + 1) % 3]);
                }
                if (clipped.length >= 3
                        && Math.abs(signedArea(clipped)) > EPS * Math.abs(signedArea(firstTriangles[i]))) {
                    pieces[pieceCount++] = clipped;
                }
            }
        }
        return Arrays.copyOf(pieces, pieceCount);
    }

    static Point[] clipByEdge(Point[] polygon, Point a, Point b) {
        Point[] buffer = new Point[2 * polygon.length];
        int count = 0;
        for (int i = 0; i < polygon.length; i++) {
            Point prev = polygon[(i + polygon.length - 1) % polygon.length];
            Point cur = polygon[i];
            boolean curIn = orient(a, b, cur) >= 0;
            boolean prevIn = orient(a, b, prev) >= 0;
            if (curIn != prevIn) {
                double dPrev = cross(a, b, prev);
                double dCur = cross(a, b, cur);
                double t = dPrev == dCur ? 1 : dPrev / (dPrev - dCur);
                buffer[count++] = prev.translate(prev.vectorTo(cur).scale(t));
            }
            if (curIn) {
                buffer[count++] = cur;
            }
        }
        return Arrays.copyOf(buffer, count);
    }

    static Point[] transform(Point[] polygon, double a, double b, double c, double d, double e, double f) {
        if (!check(polygon)) {
            return null;
        }
        Point[] result = new Point[polygon.length];
        for (int i = 0; i < polygon.length; i++) {
            double x = polygon[i].x;
            double y = polygon[i].y;
            result[i] = Point.of(a * x + b * y + e, c * x + d * y + f);
        }
        return result;
    }

    static boolean noVertexInside(Point[] points, int count, Point a, Point b, Point c) {
        for (int i = 0; i < count; i++) {
            Point p = points[i];
            if (p != a && p != b && p != c
                    && orient(a, b, p) >= 0 && orient(b, c, p) >= 0 && orient(c, a, p) >= 0) {
                return false;
            }
        }
        return true;
    }

    static double signedArea(Point[] polygon) {
        double sum = 0;
        for (int i = 0; i < polygon.length; i++) {
            Point a = polygon[i];
            Point b = polygon[(i + 1) % polygon.length];
            sum += a.x * b.y - b.x * a.y;
        }
        return sum / 2.0;
    }

    static void reverse(Point[] points) {
        for (int i = 0, j = points.length - 1; i < j; i++, j--) {
            Point tmp = points[i];
            points[i] = points[j];
            points[j] = tmp;
        }
    }

    /** Drops the closing vertex when the last vertex repeats the first, so closed contours behave like open ones. */
    static Point[] open(Point[] polygon) {
        if (polygon == null || polygon.length < 2) {
            return polygon;
        }
        Point first = polygon[0];
        Point last = polygon[polygon.length - 1];
        if (first == null || last == null || first.x != last.x || first.y != last.y) {
            return polygon;
        }
        return Arrays.copyOf(polygon, polygon.length - 1);
    }

    static boolean check(Point[] polygon) {
        if (polygon == null || polygon.length < 3) {
            printError(ERR_POLYGON_MIN_VERTICES);
            return false;
        }
        return Points.checkNotEmpty(polygon);
    }
}
