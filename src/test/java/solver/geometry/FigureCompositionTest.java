package solver.geometry;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static solver.geometry.TestPoints.pts;

/** Lab 5, Task 2: areas of a composition of figures with and without intersections. */
class FigureCompositionTest {

    private static final double EPS = 1e-9;

    private static Polygon box(double x0, double y0, double x1, double y1) {
        return Polygon.of(pts(x0, y0, x1, y0, x1, y1, x0, y1));
    }

    private static void assertAreas(double sum, double union, double intersection, FigureComposition c) {
        assertEquals(sum, c.sumOfAreas(), EPS, "sum");
        assertEquals(union, c.unionArea(), EPS, "union");
        assertEquals(intersection, c.intersectionArea(), EPS, "intersection");
    }

    @Test
    void invalidFiguresThrow() {
        assertThrows(IllegalArgumentException.class, () -> FigureComposition.of((AreaFigure[]) null));
        assertThrows(IllegalArgumentException.class, () -> FigureComposition.of(box(0, 0, 1, 1), null));
        AreaFigure custom = new AreaFigure() {
            public double area() { return 1; }
            public double perimeter() { return 4; }
            public boolean contains(Point p) { return false; }
            public double distanceTo(Point p) { return 0; }
            public AreaFigure translate(Vector2D v) { return this; }
            public AreaFigure rotate(double angle, Point center) { return this; }
            public AreaFigure scale(double factor, Point center) { return this; }
        };
        assertThrows(IllegalArgumentException.class, () -> FigureComposition.of(custom));
    }

    @Test
    void emptyComposition() {
        assertAreas(0, 0, 0, FigureComposition.of());
    }

    @Test
    void singleFigureAreas() {
        assertAreas(Math.PI * 4, Math.PI * 4, Math.PI * 4, FigureComposition.of(Circle.of(1, 1, 2)));
        Triangle t = Triangle.of(Point.of(0, 0), Point.of(4, 0), Point.of(0, 3));
        assertAreas(6, 6, 6, FigureComposition.of(t));
        Polygon l = Polygon.of(pts(0, 0, 2, 0, 2, 1, 1, 1, 1, 2, 0, 2));
        assertAreas(3, 3, 3, FigureComposition.of(l));
    }

    @Test
    void overlappingSquaresWithAndWithoutIntersections() {
        FigureComposition c = FigureComposition.of(box(0, 0, 1, 1), box(0.5, 0, 1.5, 1));
        assertAreas(2, 1.5, 0.5, c);
        assertTrue(c.hasOverlaps());
    }

    @Test
    void disjointFiguresHaveNoOverlap() {
        FigureComposition c = FigureComposition.of(box(0, 0, 1, 1), Circle.of(5, 5, 1),
                Triangle.of(Point.of(10, 0), Point.of(12, 0), Point.of(10, 2)));
        assertAreas(1 + Math.PI + 2, 1 + Math.PI + 2, 0, c);
        assertFalse(c.hasOverlaps());
    }

    @Test
    void figuresSharingAnEdgeDoNotOverlap() {
        FigureComposition c = FigureComposition.of(box(0, 0, 1, 1), box(1, 0, 2, 1));
        assertAreas(2, 2, 0, c);
        assertFalse(c.hasOverlaps());
        FigureComposition half = FigureComposition.of(box(0, 0, 2, 2),
                Triangle.of(Point.of(2, 0), Point.of(4, 0), Point.of(2, 2)));
        assertAreas(6, 6, 0, half);
    }

    @Test
    void identicalFiguresCountOnceInUnion() {
        assertAreas(2, 1, 1, FigureComposition.of(box(0, 0, 1, 1), box(0, 0, 1, 1)));
        assertAreas(2 * Math.PI, Math.PI, Math.PI, FigureComposition.of(Circle.of(0, 0, 1), Circle.of(0, 0, 1)));
        Triangle t = Triangle.of(Point.of(0, 0), Point.of(2, 0), Point.of(0, 2));
        Triangle reversed = Triangle.of(Point.of(0, 2), Point.of(2, 0), Point.of(0, 0));
        assertAreas(6, 2, 2, FigureComposition.of(t, reversed, t));
    }

    @Test
    void nestedFigures() {
        assertAreas(17, 16, 1, FigureComposition.of(box(0, 0, 4, 4), box(1, 1, 2, 2)));
        assertAreas(16 + Math.PI, 16, Math.PI, FigureComposition.of(Circle.of(2, 2, 1), box(0, 0, 4, 4)));
    }

    @Test
    void circleInscribedInSquareTouchesItsSides() {
        FigureComposition c = FigureComposition.of(box(0, 0, 2, 2), Circle.of(1, 1, 1));
        assertAreas(4 + Math.PI, 4, Math.PI, c);
    }

    @Test
    void quarterOfCircleInsideSquareAndTriangle() {
        Circle unit = Circle.of(0, 0, 1);
        assertEquals(Math.PI / 4, FigureComposition.intersectionArea(unit, box(0, 0, 1, 1)), EPS);
        assertEquals(Math.PI + 1 - Math.PI / 4, FigureComposition.of(unit, box(0, 0, 1, 1)).unionArea(), EPS);
        Triangle t = Triangle.of(Point.of(0, 0), Point.of(2, 0), Point.of(0, 2));
        assertEquals(Math.PI / 4, FigureComposition.intersectionArea(unit, t), EPS);
    }

    @Test
    void twoCirclesMatchCircleIntersectionArea() {
        Circle a = Circle.of(0, 0, 2);
        for (Circle b : new Circle[]{Circle.of(1, 0, 1.5), Circle.of(3, 1, 2), Circle.of(4, 0, 2), Circle.of(0.5, 0, 0.3)}) {
            double inter = a.intersectionArea(b);
            assertEquals(inter, FigureComposition.intersectionArea(a, b), EPS, b.toString());
            assertEquals(a.area() + b.area() - inter, FigureComposition.of(a, b).unionArea(), EPS, b.toString());
        }
    }

    @Test
    void twoPolygonsMatchPolygon2DIntersection() {
        Polygon l = Polygon.of(pts(0, 0, 2, 0, 2, 1, 1, 1, 1, 2, 0, 2));
        Polygon[] others = {box(0.5, 0.5, 3, 3), box(-1, -1, 0.5, 3), Polygon.of(pts(1.5, -1, 3, 2, 0, 2))};
        for (Polygon other : others) {
            double expected = 0;
            for (Point[] piece : Polygon2D.intersection(l.vertices(), other.vertices())) {
                expected += Polygon2D.area(piece);
            }
            assertEquals(expected, FigureComposition.intersectionArea(l, other), EPS, other.toString());
        }
    }

    @Test
    void degenerateTriangleAddsNoAreaAndEmptiesTheIntersection() {
        Triangle flat = Triangle.of(Point.of(0, 0), Point.of(1, 1), Point.of(2, 2));
        FigureComposition c = FigureComposition.of(box(0, 0, 4, 4), flat);
        assertAreas(16, 16, 0, c);
    }

    @Test
    void resultDoesNotDependOnOrder() {
        AreaFigure a = Circle.of(0, 0, 1.5);
        AreaFigure b = box(0, 0, 2, 2);
        AreaFigure c = Triangle.of(Point.of(-1, -1), Point.of(2, -1), Point.of(0, 1.5));
        FigureComposition abc = FigureComposition.of(a, b, c);
        FigureComposition cab = FigureComposition.of(c, a, b);
        assertEquals(abc.unionArea(), cab.unionArea(), EPS);
        assertEquals(abc.intersectionArea(), cab.intersectionArea(), EPS);
    }

    /** Union and intersection of random triples agree with the inclusion-exclusion formula. */
    @Test
    void inclusionExclusionHoldsForRandomTriples() {
        Random rnd = new Random(5);
        for (int trial = 0; trial < 40; trial++) {
            AreaFigure a = randomFigure(rnd);
            AreaFigure b = randomFigure(rnd);
            AreaFigure c = randomFigure(rnd);
            double expected = a.area() + b.area() + c.area()
                    - FigureComposition.intersectionArea(a, b)
                    - FigureComposition.intersectionArea(a, c)
                    - FigureComposition.intersectionArea(b, c)
                    + FigureComposition.of(a, b, c).intersectionArea();
            assertEquals(expected, FigureComposition.of(a, b, c).unionArea(), 1e-7, a + " / " + b + " / " + c);
        }
    }

    /** A three-circle Venn diagram checked against a fine grid. */
    @Test
    void vennDiagramMatchesGridEstimate() {
        Circle[] circles = {Circle.of(0, 0, 1.5), Circle.of(1.5, 0, 1.5), Circle.of(0.75, 1.3, 1.5)};
        FigureComposition c = FigureComposition.of(circles);
        int n = 800;
        double lo = -2, hi = 3.5, h = (hi - lo) / n;
        double union = 0;
        double inter = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                Point p = Point.of(lo + (i + 0.5) * h, lo + (j + 0.5) * h);
                boolean any = false;
                boolean all = true;
                for (Circle k : circles) {
                    boolean in = k.contains(p);
                    any |= in;
                    all &= in;
                }
                union += any ? h * h : 0;
                inter += all ? h * h : 0;
            }
        }
        assertEquals(union, c.unionArea(), 0.01);
        assertEquals(inter, c.intersectionArea(), 0.01);
    }

    private static AreaFigure randomFigure(Random rnd) {
        double x = rnd.nextDouble() * 3;
        double y = rnd.nextDouble() * 3;
        switch (rnd.nextInt(3)) {
            case 0:
                return Circle.of(x, y, 0.5 + rnd.nextDouble() * 1.5);
            case 1:
                return Triangle.of(Point.of(x, y), Point.of(x + 1 + rnd.nextDouble() * 2, y + rnd.nextDouble()),
                        Point.of(x + rnd.nextDouble(), y + 1 + rnd.nextDouble() * 2));
            default:
                return box(x, y, x + 0.5 + rnd.nextDouble() * 2, y + 0.5 + rnd.nextDouble() * 2);
        }
    }
}
