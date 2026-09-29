package solver.app;

import static solver.core.ArgumentValidator.*;

/**
 * CORE-12: каталог команд і їх арність.
 */
public final class CommandCatalog {

    private CommandCatalog() {
    }

    // CORE-12: очікувана кількість аргументів
    public static int getExpectedArgsCount(String command) {
        switch (command) {
            case "help":
                return 0;

            case "abs":
            case "sqrt":
            case "factorial":
            case "fibonacci":
            case "circle-area":
            case "circle-circumference":
            case "monte-carlo-triangle":
                return 1;

            case "add":
            case "sub":
            case "mul":
            case "div":
            case "pow":
            case "solve-linear":
            case "gcd":
            case "taylor-sin":
            case "sin-taylor":
            case "taylor-cos":
            case "series-b":
            case "taylor-sinh":
            case "series-c":
            case "taylor-cosh":
            case "series-h":
            case "taylor-exp":
            case "series-e":
            case "taylor-ln-one-plus-x":
            case "series-ln":
            case "taylor-geom-series":
            case "series-ye":
            case "taylor-artanh":
            case "series-j":
            case "taylor-sqrt-one-plus-x":
            case "series-k":
            case "taylor-inv-sqrt-one-plus-x":
            case "series-l":
            case "taylor-asin":
            case "series-m":
            case "taylor-inv-sq":
            case "series-z":
            case "taylor-inv-cube":
            case "series-i":
            case "taylor-inv-one-plus-x2":
            case "series-y":
            case "origin-distance":
            case "rectangle-area":
            case "rectangle-perimeter":
            case "quadrant":
            case "ellipse-area":
            case "cylinder-volume":
            case "cone-volume":
            case "torus-volume":
                return 2;

            case "solve-quadratic":
            case "max3":
            case "triangle-area":
            case "triangle-valid":
            case "triangle-medians":
            case "triangle-bisectors":
            case "triangle-heights":
            case "triangle-angles":
                return 3;

            case "distance":
            case "manhattan-distance":
            case "midpoint":
            case "triangle-area-inradius":
            case "circle-segment":
                return 4;

            case "collinear":
            case "circle-line":
            case "circles-intersect":
            case "squares-intersect":
                return 6;

            case "rect-bounding-box":
                return 8;

            case "polygon":
                return VARIADIC_POLYGON;

            default:
                return UNKNOWN_COMMAND;
        }
    }
}
