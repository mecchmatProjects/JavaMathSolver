package solver.geometry;

/**
 * GEO (Lab 5): common contract of all geometric figures
 * (Point, Line, Segment, Circle, Triangle, Polygon).
 */
public interface Figure {

    /** Task 1: whether point p belongs to the figure (the boundary counts as part of the figure). */
    boolean contains(Point p);

    /** Shortest distance from the figure to point p (0 if p belongs to the figure). */
    double distanceTo(Point p);

    /** Affine transform: translation by vector v. */
    Figure translate(Vector2D v);

    /** Affine transform: counter-clockwise rotation by {@code angle} radians about {@code center}. */
    Figure rotate(double angle, Point center);

    /** Affine transform: scaling (homothety) by {@code factor} about {@code center}. */
    Figure scale(double factor, Point center);
}
