package solver.geometry;

import static solver.core.Messages.*;
import static solver.core.Output.*;

/**
 * GEO: багатокутник як масив вершин Point[] у порядку обходу (Lab 3, Task G6).
 */
public final class Polygon2D {

    static final double EPS = 1e-9;

    // Task G6: perimeter
    public static double perimeter(Point[] polygon) {
        if (!check(polygon)) {
            return Double.NaN;
        }
        double sum = 0;
        for (int i = 0; i < polygon.length; i++) {
            sum += Points.distance(polygon[i], polygon[(i + 1) % polygon.length]);
        }
        return sum;
    }

    // Task G6: area
    public static double area(Point[] polygon) {
        if (!check(polygon)) {
            return Double.NaN;
        }
        return Math.abs(signedArea(polygon));
    }

    // Task G6: is the polygon convex
    public static boolean isConvex(Point[] polygon) {
        if (!check(polygon)) {
            return false;
        }
        int n = polygon.length;
        int sign = 0;
        for (int i = 0; i < n; i++) {
            double c = cross(polygon[i], polygon[(i + 1) % n], polygon[(i + 2) % n]);
            if (Math.abs(c) <= EPS) {
                continue;
            }
            if (sign != 0 && (c > 0) != (sign > 0)) {
                return false;
            }
            sign = c > 0 ? 1 : -1;
        }
        return sign != 0;
    }

    // Task G6: convex hull
    public static Point[] convexHull(Point[] points) {
        if (!check(points)) {
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
            while (k >= 2 && cross(hull[k - 2], hull[k - 1], sorted[i]) <= EPS) {
                k--;
            }
            hull[k++] = sorted[i];
        }
        for (int i = sorted.length - 2, lower = k + 1; i >= 0; i--) {
            while (k >= lower && cross(hull[k - 2], hull[k - 1], sorted[i]) <= EPS) {
                k--;
            }
            hull[k++] = sorted[i];
        }
        if (k < 4) {
            printError(ERR_HULL_DEGENERATE);
            return null;
        }
        Point[] result = new Point[k - 1];
        for (int i = 0; i < k - 1; i++) {
            result[i] = hull[i];
        }
        return result;
    }

    public static boolean isSimple(Point[] polygon) {
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
                    double dot = (farA.x - shared.x) * (farB.x - shared.x) + (farA.y - shared.y) * (farB.y - shared.y);
                    if (Math.abs(cross(farA, shared, farB)) <= EPS && dot > 0) {
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
                double c = cross(prev, cur, next);
                if (Math.abs(c) <= EPS || (c > 0 && noVertexInside(ordered, count, prev, cur, next))) {
                    if (Math.abs(c) > EPS) {
                        triangles[triangleCount++] = new Point[]{prev, cur, next};
                    }
                    for (int j = i; j < count - 1; j++) {
                        ordered[j] = ordered[j + 1];
                    }
                    count--;
                    clipped = true;
                }
            }
            if (!clipped) {
                printError(ERR_POLYGON_NOT_TRIANGULABLE);
                return null;
            }
        }
        if (Math.abs(cross(ordered[0], ordered[1], ordered[2])) > EPS) {
            triangles[triangleCount++] = new Point[]{ordered[0], ordered[1], ordered[2]};
        }
        Point[][] result = new Point[triangleCount][];
        for (int i = 0; i < triangleCount; i++) {
            result[i] = triangles[i];
        }
        return result;
    }

    // Task G6: affine transformations (translation, scaling, rotation)
    public static Point[] translate(Point[] polygon, double dx, double dy) {
        return transform(polygon, 1, 0, 0, 1, dx, dy);
    }

    public static Point[] scale(Point[] polygon, double sx, double sy) {
        return transform(polygon, sx, 0, 0, sy, 0, 0);
    }

    public static Point[] rotate(Point[] polygon, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return transform(polygon, cos, -sin, sin, cos, 0, 0);
    }

    // Task G6: is the given point inside the polygon
    public static boolean contains(Point[] polygon, Point p) {
        if (!check(polygon)) {
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

    // Task G6: intersection of two polygons
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
                if (clipped.length >= 3 && Math.abs(signedArea(clipped)) > EPS) {
                    pieces[pieceCount++] = clipped;
                }
            }
        }
        Point[][] result = new Point[pieceCount][];
        for (int i = 0; i < pieceCount; i++) {
            result[i] = pieces[i];
        }
        return result;
    }

    static Point[] clipByEdge(Point[] polygon, Point a, Point b) {
        Point[] buffer = new Point[2 * polygon.length];
        int count = 0;
        for (int i = 0; i < polygon.length; i++) {
            Point prev = polygon[(i + polygon.length - 1) % polygon.length];
            Point cur = polygon[i];
            double dPrev = cross(a, b, prev);
            double dCur = cross(a, b, cur);
            if ((dCur >= -EPS) != (dPrev >= -EPS)) {
                double t = dPrev / (dPrev - dCur);
                buffer[count++] = Point.of(prev.x + t * (cur.x - prev.x), prev.y + t * (cur.y - prev.y));
            }
            if (dCur >= -EPS) {
                buffer[count++] = cur;
            }
        }
        Point[] result = new Point[count];
        for (int i = 0; i < count; i++) {
            result[i] = buffer[i];
        }
        return result;
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
                    && cross(a, b, p) >= -EPS && cross(b, c, p) >= -EPS && cross(c, a, p) >= -EPS) {
                return false;
            }
        }
        return true;
    }

    static boolean onSegment(Point a, Point b, Point p) {
        return Math.abs(cross(a, b, p)) <= EPS
                && p.x >= Math.min(a.x, b.x) - EPS && p.x <= Math.max(a.x, b.x) + EPS
                && p.y >= Math.min(a.y, b.y) - EPS && p.y <= Math.max(a.y, b.y) + EPS;
    }

    static boolean segmentsIntersect(Point p1, Point p2, Point p3, Point p4) {
        double d1 = cross(p3, p4, p1);
        double d2 = cross(p3, p4, p2);
        double d3 = cross(p1, p2, p3);
        double d4 = cross(p1, p2, p4);
        if (((d1 > EPS && d2 < -EPS) || (d1 < -EPS && d2 > EPS))
                && ((d3 > EPS && d4 < -EPS) || (d3 < -EPS && d4 > EPS))) {
            return true;
        }
        return onSegment(p3, p4, p1) || onSegment(p3, p4, p2) || onSegment(p1, p2, p3) || onSegment(p1, p2, p4);
    }

    static double cross(Point a, Point b, Point c) {
        return (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
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

    static boolean check(Point[] polygon) {
        if (polygon == null || polygon.length < 3) {
            printError(ERR_POLYGON_MIN_VERTICES);
            return false;
        }
        return Points.checkNotEmpty(polygon);
    }
}
