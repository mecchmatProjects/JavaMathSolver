package solver.geometry;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static solver.geometry.TestPoints.pts;

/** Lab 5: distanceTo() from every figure type to every figure type. */
class FigureDistanceTest {

    private static final double EPS = 1e-9;

    // Fixed figures: one of each type, placed apart from each other.
    private static final Point P = Point.of(10, 0);
    private static final Line L = Line.through(Point.of(0, -5), Point.of(1, -5));             // y = -5
    private static final Segment S = Segment.of(0, 10, 4, 10);
    private static final Circle C = Circle.of(20, 0, 2);
    private static final Triangle T = Triangle.of(Point.of(0, 0), Point.of(4, 0), Point.of(0, 3));
    private static final Polygon Q = Polygon.of(pts(30, 0, 34, 0, 34, 4, 30, 4));

    /** Checks the distance in both directions, through the static type Figure. */
    private static void assertDistance(double expected, Figure a, Figure b) {
        assertEquals(expected, a.distanceTo(b), EPS, a + " -> " + b);
        assertEquals(expected, b.distanceTo(a), EPS, b + " -> " + a);
    }

    @Test
    void pointToEveryFigure() {
        assertDistance(5.0, Point.of(0, 0), Point.of(3, 4));
        assertDistance(5.0, P, L);
        assertDistance(2.0, Point.of(2, 12), S);
        assertDistance(8.0, P, C);
        assertDistance(6.0, P, T);
        assertDistance(20.0, P, Q);
    }

    @Test
    void lineToEveryFigure() {
        assertDistance(12.0, L, Line.through(Point.of(0, 7), Point.of(5, 7)));
        assertDistance(0.0, L, Line.through(Point.of(0, 0), Point.of(1, 1)));
        assertDistance(15.0, L, S);
        assertDistance(0.0, L, Segment.of(0, -10, 0, 0));
        assertDistance(3.0, L, C);
        assertDistance(5.0, L, T);
        assertDistance(5.0, L, Q);
    }

    @Test
    void segmentToEveryFigure() {
        assertDistance(2.0, S, Segment.of(6, 10, 9, 10));
        assertDistance(5.0, Segment.of(0, 0, 2, 0), Segment.of(5, 4, 5, 8));
        assertDistance(0.0, S, Segment.of(2, 8, 2, 12));
        assertDistance(4.0, S, Circle.of(2, 5, 1));
        assertDistance(7.0, S, T);
        assertDistance(Math.hypot(26, 6), S, Q);
    }

    @Test
    void circleToEveryFigure() {
        assertDistance(3.0, C, Circle.of(26, 0, 1));
        assertDistance(0.0, C, Circle.of(21, 0, 2));
        assertDistance(14.0, C, T);
        assertDistance(8.0, C, Q);
    }

    @Test
    void triangleAndPolygonToEachOther() {
        assertDistance(6.0, T, T.translate(Vector2D.of(10, 0)));
        assertDistance(26.0, T, Q);
        assertDistance(6.0, Q, Q.translate(Vector2D.of(0, 10)));
    }

    @Test
    void figureInsideRegionIsAtDistanceZero() {
        Polygon big = Polygon.of(pts(-10, -10, 10, -10, 10, 10, -10, 10));
        assertDistance(0.0, big, Segment.of(0, 0, 1, 1));
        assertDistance(0.0, big, T);
        assertDistance(0.0, big, Circle.of(0, 0, 1));
        assertDistance(0.0, big, Point.of(3, 3));
        assertDistance(0.0, T, Segment.of(0.5, 0.5, 1, 1));
        assertDistance(0.0, T, Triangle.of(Point.of(0.5, 0.5), Point.of(1, 0.5), Point.of(0.5, 1)));
        assertDistance(0.0, Circle.of(0, 0, 10), C.translate(Vector2D.of(-20, 0)));
    }

    @Test
    void lineThroughRegionIsAtDistanceZero() {
        Line diagonal = Line.through(Point.of(0, 0), Point.of(1, 1));
        assertDistance(0.0, diagonal, T);
        assertDistance(0.0, diagonal, Polygon.of(pts(-1, -1, 1, -1, 1, 1, -1, 1)));
    }

    @Test
    void distanceToItselfIsZero() {
        for (Figure f : new Figure[]{P, L, S, C, T, Q}) {
            assertEquals(0.0, f.distanceTo(f), EPS, f.toString());
        }
    }

    @Test
    void nullFigureThrows() {
        assertThrows(IllegalArgumentException.class, () -> T.distanceTo((Figure) null));
    }

    /**
     * Every ordered pair of figures (including overlapping ones): the distance is symmetric and matches
     * a brute-force minimum over densely sampled boundary points.
     */
    @Test
    void everyPairMatchesBruteForce() {
        Figure[] figures = {
            P, Point.of(1, 1),
            L, Line.through(Point.of(0, 0), Point.of(3, 1)),
            S, Segment.of(-2, -2, 3, 2),
            C, Circle.of(2, 1, 1.5),
            T, Triangle.of(Point.of(15, 5), Point.of(25, 5), Point.of(20, 12)),
            Q, Polygon.of(pts(1, 5, 6, 5, 6, 9, 4, 7, 1, 9)),
        };
        for (Figure a : figures) {
            for (Figure b : figures) {
                double exact = a.distanceTo(b);
                assertTrue(exact >= 0, a + " -> " + b);
                assertEquals(exact, b.distanceTo(a), EPS, "symmetry " + a + " / " + b);
                double brute = bruteForce(a, b);
                assertTrue(exact <= brute + EPS, "exact " + exact + " > brute " + brute + " for " + a + " / " + b);
                assertEquals(brute, exact, 0.02, a + " / " + b);
            }
        }
    }

    private static double bruteForce(Figure a, Figure b) {
        List<Point> sa = samples(a);
        List<Point> sb = samples(b);
        for (Point p : sa) {
            if (b.contains(p)) {
                return 0.0;
            }
        }
        for (Point p : sb) {
            if (a.contains(p)) {
                return 0.0;
            }
        }
        double best = Double.POSITIVE_INFINITY;
        for (Point p : sa) {
            for (Point q : sb) {
                best = Math.min(best, p.distanceTo(q));
            }
        }
        return best;
    }

    /** Points on the figure (boundary points for regions, a long window for lines). */
    private static List<Point> samples(Figure f) {
        List<Point> s = new ArrayList<>();
        if (f instanceof Point) {
            s.add((Point) f);
        } else if (f instanceof Line) {
            Line l = (Line) f;
            Vector2D d = l.direction().normalize();
            for (int i = -2000; i <= 2000; i++) {
                s.add(l.point().translate(d.scale(i * 0.02)));
            }
        } else if (f instanceof Circle) {
            Circle c = (Circle) f;
            for (int i = 0; i < 1000; i++) {
                double t = 2 * Math.PI * i / 1000;
                s.add(c.center().translate(Vector2D.of(Math.cos(t), Math.sin(t)).scale(c.radius())));
            }
            s.add(c.center());
        } else {
            Segment[] parts = f instanceof Segment ? new Segment[]{(Segment) f}
                    : f instanceof Triangle ? ((Triangle) f).sides() : ((Polygon) f).edges();
            for (Segment part : parts) {
                for (int i = 0; i <= 300; i++) {
                    s.add(part.a().translate(part.toVector().scale(i / 300.0)));
                }
            }
        }
        return s;
    }
}
