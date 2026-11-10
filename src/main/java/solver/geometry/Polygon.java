package solver.geometry;

import java.util.Arrays;
import java.util.Locale;

import static solver.core.Messages.*;
import static solver.core.Output.*;

/**
 * GEO (Lab 5): simple polygon as an object built by composition of its vertices.
 * The algorithms live in {@link Polygon2D} (Lab 3); this class validates the vertices once
 * in the constructor, so its methods never print errors.
 * It also keeps the CLI command polygon ({@link #processPolygon}).
 */
public final class Polygon implements AreaFigure {

    private final Point[] vertices;

    private Polygon(Point[] vertices) {
        if (vertices == null) {
            throw new IllegalArgumentException("Polygon vertices must not be null");
        }
        Point[] open = Polygon2D.open(vertices.clone());
        if (open.length < 3) {
            throw new IllegalArgumentException("Polygon needs at least 3 vertices");
        }
        for (Point p : open) {
            if (p == null) {
                throw new IllegalArgumentException("Polygon vertices must not be null");
            }
            if (!Double.isFinite(p.x) || !Double.isFinite(p.y)) {
                throw new IllegalArgumentException("Polygon vertices must be finite: " + p);
            }
        }
        if (!Polygon2D.isSimple(open)) {
            throw new IllegalArgumentException("Polygon must be simple (no self-intersections)");
        }
        this.vertices = open;
    }

    // CLI command "polygon" (Lab 2): perimeter and convexity
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

    /** Vertices in traversal order; a repeated closing vertex is dropped. */
    public static Polygon of(Point... vertices) {
        return new Polygon(vertices);
    }

    public Point[] vertices() {
        return vertices.clone();
    }

    public int size() {
        return vertices.length;
    }

    /** Edges in traversal order, the last one closes the contour. */
    public Segment[] edges() {
        Segment[] edges = new Segment[vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            edges[i] = Segment.of(vertices[i], vertices[(i + 1) % vertices.length]);
        }
        return edges;
    }

    @Override
    public double area() {
        return Math.abs(Polygon2D.signedArea(vertices));
    }

    @Override
    public double perimeter() {
        return Polygon2D.perimeter(vertices);
    }

    public boolean isConvex() {
        return Polygon2D.isConvex(vertices);
    }

    /** Task 1: whether p lies inside the polygon or on its boundary. */
    @Override
    public boolean contains(Point p) {
        return Polygon2D.containsPoint(vertices, p);
    }

    @Override
    public double distanceTo(Point p) {
        if (contains(p)) {
            return 0.0;
        }
        double best = Double.POSITIVE_INFINITY;
        for (Segment edge : edges()) {
            best = Math.min(best, edge.distanceTo(p));
        }
        return best;
    }

    public Triangle[] triangulate() {
        Point[][] pieces = Polygon2D.triangulate(vertices);
        Triangle[] triangles = new Triangle[pieces.length];
        for (int i = 0; i < pieces.length; i++) {
            triangles[i] = Triangle.of(pieces[i][0], pieces[i][1], pieces[i][2]);
        }
        return triangles;
    }

    @Override
    public Polygon translate(Vector2D v) {
        Point[] moved = new Point[vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            moved[i] = vertices[i].translate(v);
        }
        return new Polygon(moved);
    }

    @Override
    public Polygon rotate(double angle, Point center) {
        Point[] moved = new Point[vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            moved[i] = vertices[i].rotate(angle, center);
        }
        return new Polygon(moved);
    }

    /** Throws for factor 0, because all vertices would collapse into one point. */
    @Override
    public Polygon scale(double factor, Point center) {
        Point[] moved = new Point[vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            moved[i] = vertices[i].scale(factor, center);
        }
        return new Polygon(moved);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Polygon)) {
            return false;
        }
        return Arrays.equals(vertices, ((Polygon) o).vertices);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(vertices);
    }

    @Override
    public String toString() {
        return "Polygon" + Arrays.toString(vertices);
    }
}
