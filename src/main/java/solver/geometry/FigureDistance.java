package solver.geometry;

/**
 * GEO (Lab 5): shortest distance between any two figures.
 *
 * <p>Rules used:
 * <ul>
 *   <li>Point: the other figure's {@code distanceTo(Point)};</li>
 *   <li>Circle (a disc): distance from its center to the other figure minus the radius
 *       (this holds for any closed set);</li>
 *   <li>Line, Segment, Triangle, Polygon: 0 if one region contains a point of the other figure,
 *       otherwise the minimum distance between their edges.</li>
 * </ul>
 */
final class FigureDistance {

    private FigureDistance() {
    }

    static double between(Figure first, Figure second) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("Figures must not be null");
        }
        if (first instanceof Point) {
            return second.distanceTo((Point) first);
        }
        if (second instanceof Point) {
            return first.distanceTo((Point) second);
        }
        if (first instanceof Circle) {
            Circle c = (Circle) first;
            return Math.max(0.0, second.distanceTo(c.center()) - c.radius());
        }
        if (second instanceof Circle) {
            Circle c = (Circle) second;
            return Math.max(0.0, first.distanceTo(c.center()) - c.radius());
        }
        if (regionContainsPointOf(first, second) || regionContainsPointOf(second, first)) {
            return 0.0;
        }
        double best = Double.POSITIVE_INFINITY;
        for (Figure e : edges(first)) {
            for (Figure f : edges(second)) {
                best = Math.min(best, edgeDistance(e, f));
                if (best == 0.0) {
                    return 0.0;
                }
            }
        }
        return best;
    }

    /** Whether {@code region} is a Triangle/Polygon that contains some point of {@code other}. */
    private static boolean regionContainsPointOf(Figure region, Figure other) {
        if (!(region instanceof Triangle) && !(region instanceof Polygon)) {
            return false;
        }
        return region.contains(anyPoint(other));
    }

    private static Point anyPoint(Figure f) {
        if (f instanceof Line) {
            return ((Line) f).point();
        }
        if (f instanceof Segment) {
            return ((Segment) f).a();
        }
        if (f instanceof Triangle) {
            return ((Triangle) f).a();
        }
        if (f instanceof Polygon) {
            return ((Polygon) f).vertices()[0];
        }
        throw unsupported(f);
    }

    /** The boundary of a figure as lines/segments. */
    private static Figure[] edges(Figure f) {
        if (f instanceof Line || f instanceof Segment) {
            return new Figure[]{f};
        }
        if (f instanceof Triangle) {
            return ((Triangle) f).sides();
        }
        if (f instanceof Polygon) {
            return ((Polygon) f).edges();
        }
        throw unsupported(f);
    }

    private static double edgeDistance(Figure e, Figure f) {
        if (e instanceof Line && f instanceof Line) {
            return ((Line) e).distanceTo((Line) f);
        }
        if (e instanceof Line) {
            return lineToSegment((Line) e, (Segment) f);
        }
        if (f instanceof Line) {
            return lineToSegment((Line) f, (Segment) e);
        }
        Segment s = (Segment) e;
        Segment t = (Segment) f;
        if (s.intersects(t)) {
            return 0.0;
        }
        return Math.min(Math.min(s.distanceTo(t.a()), s.distanceTo(t.b())),
                Math.min(t.distanceTo(s.a()), t.distanceTo(s.b())));
    }

    private static double lineToSegment(Line line, Segment s) {
        if (s.intersects(line)) {
            return 0.0;
        }
        return Math.min(line.distanceTo(s.a()), line.distanceTo(s.b()));
    }

    private static UnsupportedOperationException unsupported(Figure f) {
        return new UnsupportedOperationException("Unsupported figure type: " + f.getClass().getSimpleName());
    }
}
