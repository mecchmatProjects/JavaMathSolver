package solver.app;

import solver.core.ArgumentValidator;

import static solver.core.ArgumentValidator.UNKNOWN_COMMAND;

/**
 * CORE-12: каталог команд і їх арність.
 */
public final class CommandCatalog {

    // C0-b: службові значення арності для команд зі змінною кількістю аргументів
    public static final int VARIADIC_NUMBERS = ArgumentValidator.VARIADIC_NUMBERS;  // -2: >= 1 число (statistics)
    public static final int VARIADIC_POINTS = ArgumentValidator.VARIADIC_POINTS;    // -3: >= 2, парна кількість (centroid)
    public static final int VARIADIC_POLYGON = ArgumentValidator.VARIADIC_POLYGON;  // -4: >= 6, парна (polygon)
    public static final int VARIADIC_TEXT = ArgumentValidator.VARIADIC_TEXT;        // -5: >= 1 слово (tokenize)

    // C0-b: тип аргументів команди
    public enum ArgKind { NUMERIC, TEXT }

    private CommandCatalog() {
    }

    // C0-b: текстові команди отримують вираз рядком, без парсингу чисел
    public static ArgKind getArgKind(String command) {
        switch (command) {
            case "tokenize":
            case "token-stats":
            case "validate-expr":
                return ArgKind.TEXT;
            default:
                return ArgKind.NUMERIC;
        }
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

            // C0-b: текстові команди (див. getArgKind). Без цього main відсікав їх
            // як Unknown command ще до перевірки ArgKind.TEXT.
            case "tokenize":
            case "token-stats":
            case "validate-expr":
                return VARIADIC_TEXT;

            default:
                return UNKNOWN_COMMAND;
        }
    }
}
