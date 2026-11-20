package solver.geometry;

/**
 * GEO (Lab 5): a figure that bounds a region of the plane (Circle, Triangle, Polygon).
 */
public interface AreaFigure extends Figure {

    double area();

    double perimeter();

    @Override
    AreaFigure translate(Vector2D v);

    @Override
    AreaFigure rotate(double angle, Point center);

    @Override
    AreaFigure scale(double factor, Point center);
}
