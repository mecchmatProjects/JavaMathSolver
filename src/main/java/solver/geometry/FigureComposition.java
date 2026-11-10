package solver.geometry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static solver.geometry.GeoMath.EPS;

/**
 * GEO (Lab 5, Task 2): areas of a composition of figures (Circle, Triangle, Polygon).
 *
 * <ul>
 *   <li>{@link #sumOfAreas()} - area without taking intersections into account
 *       (every figure counted fully);</li>
 *   <li>{@link #unionArea()} - area with intersections taken into account
 *       (overlapping parts counted once);</li>
 *   <li>{@link #intersectionArea()} - area covered by all figures at once.</li>
 * </ul>
 *
 * <p>Union and intersection are exact. They use Green's theorem, area = 1/2 * closed integral of (x dy - y dx):
 * the boundary of every figure is cut at its intersection points with the other figures, and each piece
 * is kept only if it lies on the boundary of the result. For segments and circular arcs the integral
 * has a closed form. Coinciding boundary pieces are kept once (from the figure with the lowest index)
 * when both figures lie on the same side of them, and dropped when the figures lie on opposite sides.
 */
public final class FigureComposition {

    /** Pieces shorter than this (in segment parameter or arc angle) are ignored. */
    private static final double PIECE_EPS = 1e-12;

    private enum Side { INSIDE, OUTSIDE, SAME, OPPOSITE }

    /** A figure's region: either a circle or a counter-clockwise polygon. */
    private static final class Region {
        final Circle circle;
        final Point[] ccw;

        Region(Circle circle, Point[] ccw) {
            this.circle = circle;
            this.ccw = ccw;
        }
    }

    private final AreaFigure[] figures;

    private FigureComposition(AreaFigure[] figures) {
        if (figures == null) {
            throw new IllegalArgumentException("Figures must not be null");
        }
        for (AreaFigure f : figures) {
            if (f == null) {
                throw new IllegalArgumentException("Figures must not be null");
            }
            if (!(f instanceof Circle) && !(f instanceof Triangle) && !(f instanceof Polygon)) {
                throw new IllegalArgumentException("Unsupported figure type: " + f.getClass().getSimpleName());
            }
        }
        this.figures = figures.clone();
    }

    public static FigureComposition of(AreaFigure... figures) {
        return new FigureComposition(figures);
    }

    /** Area of the intersection of two figures. */
    public static double intersectionArea(AreaFigure a, AreaFigure b) {
        return of(a, b).intersectionArea();
    }

    public AreaFigure[] figures() {
        return figures.clone();
    }

    public int size() {
        return figures.length;
    }

    /** Area without intersections: the plain sum of all figure areas. */
    public double sumOfAreas() {
        double sum = 0;
        for (AreaFigure f : figures) {
            sum += f.area();
        }
        return sum;
    }

    /** Area with intersections: the area of the union (overlaps counted once). */
    public double unionArea() {
        return boundaryArea(true);
    }

    /** Area covered by every figure of the composition (0 for an empty composition). */
    public double intersectionArea() {
        if (figures.length == 0) {
            return 0.0;
        }
        return boundaryArea(false);
    }

    /** Whether some figures overlap with positive area. */
    public boolean hasOverlaps() {
        double sum = sumOfAreas();
        return sum - unionArea() > EPS * Math.max(1.0, sum);
    }

    // ---------- Green's theorem over the boundary ----------

    private double boundaryArea(boolean union) {
        List<Region> regions = new ArrayList<>();
        for (AreaFigure f : figures) {
            if (hasZeroArea(f)) {
                if (!union) {
                    return 0.0;
                }
                continue;
            }
            regions.add(toRegion(f));
        }
        double total = 0;
        for (int i = 0; i < regions.size(); i++) {
            total += regions.get(i).circle != null
                    ? circleContribution(regions, i, union)
                    : polygonContribution(regions, i, union);
        }
        return Math.max(0.0, total);
    }

    private static boolean hasZeroArea(AreaFigure f) {
        if (f instanceof Triangle) {
            return ((Triangle) f).isDegenerate();
        }
        return f.area() <= 0;
    }

    private static Region toRegion(AreaFigure f) {
        if (f instanceof Circle) {
            return new Region((Circle) f, null);
        }
        Point[] vertices = f instanceof Triangle ? ((Triangle) f).vertices() : ((Polygon) f).vertices();
        if (Polygon2D.signedArea(vertices) < 0) {
            Polygon2D.reverse(vertices);
        }
        return new Region(null, vertices);
    }

    private static double circleContribution(List<Region> regions, int i, boolean union) {
        Circle c = regions.get(i).circle;
        List<Double> angles = new ArrayList<>();
        for (int j = 0; j < regions.size(); j++) {
            if (j != i) {
                addCircleSplits(c, regions.get(j), angles);
            }
        }
        Collections.sort(angles);
        if (angles.isEmpty()) {
            angles.add(0.0);
        }
        double sum = 0;
        int n = angles.size();
        for (int k = 0; k < n; k++) {
            double from = angles.get(k);
            double to = k + 1 < n ? angles.get(k + 1) : angles.get(0) + 2 * Math.PI;
            if (to - from < PIECE_EPS) {
                continue;
            }
            double mid = (from + to) / 2;
            Point m = pointAt(c, mid);
            Vector2D tangent = Vector2D.of(-Math.sin(mid), Math.cos(mid));
            if (keep(regions, i, m, tangent, union)) {
                sum += arcIntegral(c, from, to);
            }
        }
        return sum;
    }

    private static double polygonContribution(List<Region> regions, int i, boolean union) {
        Point[] v = regions.get(i).ccw;
        double sum = 0;
        for (int e = 0; e < v.length; e++) {
            Point p = v[e];
            Point q = v[(e + 1) % v.length];
            List<Double> params = new ArrayList<>();
            params.add(0.0);
            params.add(1.0);
            for (int j = 0; j < regions.size(); j++) {
                if (j != i) {
                    addSegmentSplits(p, q, regions.get(j), params);
                }
            }
            Collections.sort(params);
            Vector2D direction = p.vectorTo(q);
            for (int k = 0; k + 1 < params.size(); k++) {
                double t1 = params.get(k);
                double t2 = params.get(k + 1);
                if (t2 - t1 < PIECE_EPS) {
                    continue;
                }
                Point m = lerp(p, q, (t1 + t2) / 2);
                if (keep(regions, i, m, direction, union)) {
                    Point from = lerp(p, q, t1);
                    Point to = lerp(p, q, t2);
                    sum += (from.x * to.y - to.x * from.y) / 2;
                }
            }
        }
        return sum;
    }

    /**
     * Union keeps a boundary piece that is outside every other figure;
     * intersection keeps a piece that is inside every other figure.
     * A piece shared with figure j in the same direction is kept only once (lowest index wins).
     */
    private static boolean keep(List<Region> regions, int i, Point m, Vector2D tangent, boolean union) {
        for (int j = 0; j < regions.size(); j++) {
            if (j == i) {
                continue;
            }
            Side side = classify(regions.get(j), m, tangent);
            boolean ok = side == (union ? Side.OUTSIDE : Side.INSIDE) || (side == Side.SAME && j > i);
            if (!ok) {
                return false;
            }
        }
        return true;
    }

    private static Side classify(Region r, Point m, Vector2D tangent) {
        if (r.circle != null) {
            Vector2D fromCenter = r.circle.center().vectorTo(m);
            double d = fromCenter.length();
            double radius = r.circle.radius();
            if (Math.abs(d - radius) <= EPS * Math.max(1.0, radius)) {
                return tangent.dot(fromCenter.perpendicular()) > 0 ? Side.SAME : Side.OPPOSITE;
            }
            return d < radius ? Side.INSIDE : Side.OUTSIDE;
        }
        Point[] v = r.ccw;
        for (int e = 0; e < v.length; e++) {
            Point a = v[e];
            Point b = v[(e + 1) % v.length];
            if (GeoMath.onSegment(a, b, m)) {
                return tangent.dot(a.vectorTo(b)) > 0 ? Side.SAME : Side.OPPOSITE;
            }
        }
        return Polygon2D.containsPoint(v, m) ? Side.INSIDE : Side.OUTSIDE;
    }

    // ---------- split points ----------

    private static void addCircleSplits(Circle c, Region other, List<Double> angles) {
        if (other.circle != null) {
            Circle o = other.circle;
            boolean coincident = c.center().distanceTo(o.center()) <= EPS && Math.abs(c.radius() - o.radius()) <= EPS;
            if (!coincident) {
                for (Point p : c.intersection(o)) {
                    angles.add(angleOf(c, p));
                }
            }
            return;
        }
        Point[] v = other.ccw;
        for (int e = 0; e < v.length; e++) {
            Point p = v[e];
            Point q = v[(e + 1) % v.length];
            for (double t : lineCircleParams(p, q, c)) {
                angles.add(angleOf(c, lerp(p, q, t)));
            }
        }
    }

    private static void addSegmentSplits(Point p, Point q, Region other, List<Double> params) {
        if (other.circle != null) {
            params.addAll(lineCircleParams(p, q, other.circle));
            return;
        }
        Vector2D d1 = p.vectorTo(q);
        Point[] v = other.ccw;
        for (int e = 0; e < v.length; e++) {
            Point r = v[e];
            Point s = v[(e + 1) % v.length];
            Vector2D d2 = r.vectorTo(s);
            double denom = d1.cross(d2);
            if (Math.abs(denom) <= EPS * d1.length() * d2.length()) {
                if (GeoMath.orient(p, q, r) == 0) {
                    addParam(params, p.vectorTo(r).dot(d1) / d1.lengthSquared());
                    addParam(params, p.vectorTo(s).dot(d1) / d1.lengthSquared());
                }
                continue;
            }
            Vector2D pr = p.vectorTo(r);
            double t = pr.cross(d2) / denom;
            double u = pr.cross(d1) / denom;
            if (u >= -EPS && u <= 1 + EPS) {
                addParam(params, t);
            }
        }
    }

    /** Parameters t in [0, 1] where p + t * (q - p) lies on the circle (tangency gives one point). */
    private static List<Double> lineCircleParams(Point p, Point q, Circle c) {
        List<Double> params = new ArrayList<>();
        Vector2D d = p.vectorTo(q);
        double len2 = d.lengthSquared();
        if (len2 == 0) {
            return params;
        }
        double tFoot = p.vectorTo(c.center()).dot(d) / len2;
        double h2 = lerp(p, q, tFoot).vectorTo(c.center()).lengthSquared();
        double r2 = c.radius() * c.radius();
        if (h2 > r2 * (1 + EPS)) {
            return params;
        }
        double half = Math.sqrt(Math.max(0.0, r2 - h2) / len2);
        addParam(params, tFoot - half);
        addParam(params, tFoot + half);
        return params;
    }

    private static void addParam(List<Double> params, double t) {
        if (t >= -EPS && t <= 1 + EPS) {
            params.add(Math.max(0.0, Math.min(1.0, t)));
        }
    }

    // ---------- small helpers ----------

    private static Point lerp(Point p, Point q, double t) {
        return p.translate(p.vectorTo(q).scale(t));
    }

    private static Point pointAt(Circle c, double angle) {
        return c.center().translate(Vector2D.of(Math.cos(angle), Math.sin(angle)).scale(c.radius()));
    }

    private static double angleOf(Circle c, Point p) {
        double angle = Math.atan2(p.y - c.center().y, p.x - c.center().x);
        return angle < 0 ? angle + 2 * Math.PI : angle;
    }

    /** 1/2 * integral of (x dy - y dx) along the counter-clockwise arc from angle t1 to t2. */
    private static double arcIntegral(Circle c, double t1, double t2) {
        double r = c.radius();
        double cx = c.center().x;
        double cy = c.center().y;
        return (r * r * (t2 - t1)
                + r * cx * (Math.sin(t2) - Math.sin(t1))
                - r * cy * (Math.cos(t2) - Math.cos(t1))) / 2;
    }
}
