package solver.app;

import static solver.core.Output.*;
import static solver.core.Messages.*;
import static solver.core.NumberParser.*;
import static solver.app.HelpPrinter.printHelp;
import static solver.algebra.Arithmetic.*;
import static solver.algebra.Equations.*;
import static solver.algebra.NumberTheory.*;
import static solver.algebra.TaylorSeries.*;
import static solver.geometry.Distances.*;
import static solver.geometry.Shapes.*;
import static solver.geometry.Triangles.*;
import static solver.geometry.Solids.*;
import static solver.geometry.Intersections.*;
import static solver.geometry.Polygon.*;
import static solver.geometry.MonteCarlo.*;

import java.util.Locale;

import solver.algebra.NumericArray;
import solver.core.Token;
import solver.core.TokenType;
import solver.core.Tokenizer;
import solver.geometry.Point;
import solver.geometry.Points;
import solver.geometry.Polygon2D;

/**
 * CORE-11: диспетчеризація команд.
 */
public final class CommandDispatcher {

    private CommandDispatcher() {
    }

    // CORE-11: диспетчеризація команд
    public static void dispatch(String command, Number[] arguments) {
        double[] a = toDoubleArray(arguments);

        switch (command) {
            case "help":
                printHelp();
                break;

            // --- ALGEBRA: Lab 1 ---
            case "add":
                printResult(add(a[0], a[1]));
                break;
            case "sub":
                printResult(sub(a[0], a[1]));
                break;
            case "mul":
                printResult(mul(a[0], a[1]));
                break;
            case "div":
                if (a[1] == 0.0) {
                    printError(ERR_DIVISION_BY_ZERO);
                } else {
                    printResult(div(a[0], a[1]));
                }
                break;
            case "pow":
                printResult(pow(a[0], a[1]));
                break;
            case "abs":
                printResult(abs(a[0]));
                break;
            case "sqrt":
                if (a[0] < 0.0) {
                    printError(ERR_INVALID_INPUT);
                } else {
                    printResult(sqrt(a[0]));
                }
                break;

            // --- ALGEBRA: Lab 2 ---
            case "solve-linear":
                solveLinear(a[0], a[1]);
                break;
            case "solve-quadratic":
                solveQuadratic(a[0], a[1], a[2]);
                break;
            case "max3":
                printResult(max3(a[0], a[1], a[2]));
                break;
            case "gcd": {
                Long x = toLongExact(arguments[0]);
                Long y = toLongExact(arguments[1]);
                if (x == null || y == null) {
                    printError(ERR_INVALID_INPUT);
                } else {
                    solveGcd(x, y);
                }
                break;
            }
            case "factorial": {
                Long n = toLongExact(arguments[0]);
                if (n == null) {
                    printError(ERR_INVALID_INPUT);
                } else {
                    factorial(n);
                }
                break;
            }
            case "fibonacci": {
                Long n = toLongExact(arguments[0]);
                if (n == null) {
                    printError(ERR_INVALID_INPUT);
                } else {
                    fibonacci(n);
                }
                break;
            }

            // --- ALGEBRA: Taylor series (канонічна назва + аліас) ---
            case "taylor-sin":
            case "sin-taylor":
                calculateTaylorSin(a[0], a[1]);
                break;
            case "taylor-cos":
            case "series-b":
                calculateTaylorCos(a[0], a[1]);
                break;
            case "taylor-sinh":
            case "series-c":
                calculateTaylorSinh(a[0], a[1]);
                break;
            case "taylor-cosh":
            case "series-h":
                calculateTaylorCosh(a[0], a[1]);
                break;
            case "taylor-exp":
            case "series-e":
                calculateTaylorExp(a[0], a[1]);
                break;
            case "taylor-ln-one-plus-x":
            case "series-ln":
                calculateTaylorLnOnePlusX(a[0], a[1]);
                break;
            case "taylor-geom-series":
            case "series-ye":
                calculateTaylorGeomSeries(a[0], a[1]);
                break;
            case "taylor-artanh":
            case "series-j":
                calculateTaylorArtanh(a[0], a[1]);
                break;
            case "taylor-sqrt-one-plus-x":
            case "series-k":
                calculateTaylorSqrtOnePlusX(a[0], a[1]);
                break;
            case "taylor-inv-sqrt-one-plus-x":
            case "series-l":
                calculateTaylorInvSqrtOnePlusX(a[0], a[1]);
                break;
            case "taylor-asin":
            case "series-m":
                calculateTaylorAsin(a[0], a[1]);
                break;
            case "taylor-inv-sq":
            case "series-z":
                calculateTaylorInvSq(a[0], a[1]);
                break;
            case "taylor-inv-cube":
            case "series-i":
                calculateTaylorInvCube(a[0], a[1]);
                break;
            case "taylor-inv-one-plus-x2":
            case "series-y":
                calculateTaylorInvOnePlusX2(a[0], a[1]);
                break;

            // --- GEOMETRY: Lab 1 ---
            case "distance":
                calculateDistance(a[0], a[1], a[2], a[3]);
                break;
            case "origin-distance":
                calculateOriginDistance(a[0], a[1]);
                break;
            case "circle-area":
                calculateCircleArea(a[0]);
                break;
            case "circle-circumference":
                calculateCircleCircumference(a[0]);
                break;
            case "rectangle-area":
                calculateRectangleArea(a[0], a[1]);
                break;
            case "rectangle-perimeter":
                calculateRectanglePerimeter(a[0], a[1]);
                break;

            // --- GEOMETRY: Lab 2 ---
            case "triangle-area":
                calculateTriangleArea(a[0], a[1], a[2]);
                break;
            case "triangle-valid":
                calculateTriangleValid(a[0], a[1], a[2]);
                break;
            case "quadrant":
                calculateQuadrant(a[0], a[1]);
                break;
            case "manhattan-distance":
                calculateManhattanDistance(a[0], a[1], a[2], a[3]);
                break;
            case "midpoint":
                calculateMidpoint(a[0], a[1], a[2], a[3]);
                break;
            case "collinear":
                calculateCollinear(a[0], a[1], a[2], a[3], a[4], a[5]);
                break;
            case "ellipse-area":
                calculateEllipseArea(a[0], a[1]);
                break;
            case "triangle-medians":
                calculateMedians(a[0], a[1], a[2]);
                break;
            case "triangle-bisectors":
                calculateBisectors(a[0], a[1], a[2]);
                break;
            case "triangle-heights":
                calculateHeights(a[0], a[1], a[2]);
                break;
            case "triangle-area-inradius":
                calculateAreaByAnglesAndInradius(a[0], a[1], a[2], a[3]);
                break;
            case "triangle-angles":
                calculateTriangleAngles(a[0], a[1], a[2]);
                break;
            case "cylinder-volume":
                calculateCylinderVolume(a[0], a[1]);
                break;
            case "cone-volume":
                calculateConeVolume(a[0], a[1]);
                break;
            case "torus-volume":
                calculateTorusVolume(a[0], a[1]);
                break;
            case "circle-segment":
                calculateCircleSegmentIntersections(a[0], a[1], a[2], a[3]);
                break;
            case "circle-line":
                calculateCircleLine(a[0], a[1], a[2], a[3], a[4], a[5]);
                break;
            case "circles-intersect":
                calculateCirclesIntersect(a[0], a[1], a[2], a[3], a[4], a[5]);
                break;
            case "squares-intersect":
                calculateSquaresIntersect(a[0], a[1], a[2], a[3], a[4], a[5]);
                break;
            case "rect-bounding-box":
                calculateRectBoundingBox(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7]);
                break;
            case "polygon":
                processPolygon(a);
                break;
            case "monte-carlo-triangle": {
                Long n = toLongExact(arguments[0]);
                if (n == null || n <= 0 || n > Integer.MAX_VALUE) {
                    printError(ERR_INVALID_INPUT);
                } else {
                    calculateMonteCarloTriangle(n.intValue());
                }
                break;
            }

            // --- ALGEBRA: Lab 3 (NumericArray) ---
            case "statistics":
                printStatistics(a);
                break;
            case "normalize":
                printNormalized(a);
                break;

            // --- GEOMETRY: Lab 3 (Points, Polygon2D) ---
            case "centroid":
                printCentroid(a);
                break;
            case "bounding-box":
                printBoundingBox(a);
                break;
            case "nearest":
                printNearest(a);
                break;
            case "polygon-area":
                printPolygonArea(a);
                break;
            case "convex-hull":
                printConvexHull(a);
                break;
            case "point-in-polygon":
                printPointInPolygon(a);
                break;

            default:
                printUnknownCommand(command);
                break;
        }
    }

    // ===================================================================
    // Lab 3: набори чисел
    // ===================================================================

    // A1-A2: "statistics 1 4 2 8 5" -> Count / Sum / Min / Max / Mean / Variance / StdDev
    private static void printStatistics(double[] values) {
        NumericArray data = new NumericArray(values);
        double sum = data.sum();
        double min = data.min();
        double max = data.max();
        double mean = data.mean();
        double variance = data.variance();
        double std = data.standardDeviation();
        if (!allFinite(sum, mean, variance, std)) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        printLine("Count: " + data.size());
        printLine("Sum: " + fmt(sum));
        printLine("Min: " + fmt(min));
        printLine("Max: " + fmt(max));
        printLine("Mean: " + fmt(mean));
        printLine("Variance: " + fmt(variance));
        printLine("StdDev: " + fmt(std));
    }

    // A4: min-max нормалізація, "normalize 10 20 30" -> [0.000000, 0.500000, 1.000000]
    private static void printNormalized(double[] values) {
        double[] normalized = new NumericArray(values).normalize().toArray();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < normalized.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(fmt(normalized[i]));
        }
        printResult(sb.append(']').toString());
    }

    // ===================================================================
    // Lab 3: набори точок і багатокутники.
    // Points / Polygon2D самі друкують помилку і повертають null — тоді просто виходимо.
    // ===================================================================

    // G4: "centroid 0 0 4 0 4 4 0 4" -> (2.000000, 2.000000)
    private static void printCentroid(double[] coords) {
        Point c = Points.centroid(toPoints(coords, 0));
        if (c != null) {
            printPoint(c);
        }
    }

    // G3: "bounding-box 1 5 -2 3 4 0" -> minX=..., maxX=..., minY=..., maxY=...
    private static void printBoundingBox(double[] coords) {
        double[] box = Points.boundingBox(toPoints(coords, 0));
        if (box != null) {
            printResult(String.format(Locale.ROOT, "minX=%s, maxX=%s, minY=%s, maxY=%s",
                    fmt(box[0]), fmt(box[1]), fmt(box[2]), fmt(box[3])));
        }
    }

    // G5: "nearest px py x1 y1 x2 y2 ..." -> найближча до P точка набору
    private static void printNearest(double[] coords) {
        if (coords.length < 4) { // P + хоча б одна точка набору
            printError(ERR_NOT_ENOUGH_ARGS);
            return;
        }
        Point p = Point.of(coords[0], coords[1]);
        Point best = Points.nearest(p, toPoints(coords, 2));
        if (best != null) {
            printPoint(best);
        }
    }

    // G6: площа багатокутника (формула шнурування)
    private static void printPolygonArea(double[] coords) {
        Point[] polygon = toPoints(coords, 0);
        if (!Polygon2D.isSimple(polygon)) { // для "метелика" формула шнурування дає хибне 0
            printError(ERR_POLYGON_NOT_SIMPLE);
            return;
        }
        double area = Polygon2D.area(polygon);
        if (!Double.isNaN(area)) {
            printResult(area);
        }
    }

    // G6: опукла оболонка, вершини проти годинникової стрілки: "(x1, y1) (x2, y2) ..."
    private static void printConvexHull(double[] coords) {
        Point[] hull = Polygon2D.convexHull(toPoints(coords, 0));
        if (hull == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hull.length; i++) {
            if (i > 0) {
                sb.append(' ');
            }
            sb.append(formatPoint(hull[i]));
        }
        printResult(sb.toString());
    }

    // G6: "point-in-polygon px py x1 y1 x2 y2 x3 y3 ..." -> true / false (межа вважається всередині)
    private static void printPointInPolygon(double[] coords) {
        if (coords.length < 8) { // P + 3 вершини
            printError(ERR_NOT_ENOUGH_ARGS);
            return;
        }
        Point p = Point.of(coords[0], coords[1]);
        Point[] polygon = toPoints(coords, 2);
        if (!Polygon2D.isSimple(polygon)) {
            printError(ERR_POLYGON_NOT_SIMPLE);
            return;
        }
        printResult(Polygon2D.contains(polygon, p));
    }

    // x1 y1 x2 y2 ... -> Point[], починаючи з індексу from (парність перевірена ArgumentValidator)
    private static Point[] toPoints(double[] coords, int from) {
        Point[] points = new Point[(coords.length - from) / 2];
        for (int i = 0; i < points.length; i++) {
            points[i] = Point.of(coords[from + 2 * i], coords[from + 2 * i + 1]);
        }
        return points;
    }

    private static void printPoint(Point p) {
        if (!allFinite(p.x, p.y)) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        printResult(formatPoint(p));
    }

    private static String formatPoint(Point p) {
        return "(" + fmt(p.x) + ", " + fmt(p.y) + ")";
    }

    // Той самий формат, що й printResult(double): %f, без "-0.000000"
    private static String fmt(double v) {
        return String.format(Locale.ROOT, "%f", v + 0.0).replace("-0.000000", "0.000000");
    }

    private static boolean allFinite(double... values) {
        for (double v : values) {
            if (!Double.isFinite(v)) {
                return false;
            }
        }
        return true;
    }

    // C0-b: диспетчеризація текстових команд (аргумент - увесь вираз одним рядком)
    public static void dispatchText(String command, String text) {
        switch (command) {
            case "validate-expr":
                validateExpression(text);
                break;
            case "token-stats":
                printTokenStatistics(text);
                break;
            case "tokenize":
                printTokens(text);
                break;
            default:
                printUnknownCommand(command);
                break;
        }
    }

    // C8: "Result: true" або перша нерозпізнана лексема, напр. "Error: Invalid token: 2..5"
    private static void validateExpression(String text) {
        if (Tokenizer.isValid(text)) {
            printResult(true);
            return;
        }
        for (Token token : Tokenizer.tokenize(text)) {
            if (token.getType() == TokenType.UNKNOWN) {
                printError(ERR_INVALID_TOKEN + displayLexeme(token.getLexeme()));
                return;
            }
        }
        // Сюди доходить лише вираз без лексем (порожній / самі пробіли)
        printError(ERR_NOT_ENOUGH_ARGS);
    }

    // C9: "Numbers: 2", "Identifiers: 4", "Operators: 4", "Parentheses: 2" (UNKNOWN не рахуються)
    private static void printTokenStatistics(String text) {
        int[] stats = Tokenizer.tokenStatistics(text);
        printLine("Numbers: " + stats[0]);
        printLine("Identifiers: " + stats[1]);
        printLine("Operators: " + stats[2]);
        printLine("Parentheses: " + stats[3]);
    }

    /**
     * C10: демо tokenizer — кожна лексема окремим рядком {@code <index>: <TYPE> <lexeme>}.
     * <pre>
     * tokenize "2*x^2 + 3*x - 5"
     * 0: NUMBER 2
     * 1: MULTIPLY *
     * 2: IDENTIFIER x
     * ...
     * </pre>
     * Нерозпізнані лексеми теж друкуються (як UNKNOWN): це демонстрація того,
     * що бачить tokenizer; відхилити вираз — задача {@code validate-expr}.
     */
    private static void printTokens(String text) {
        Token[] tokens = Tokenizer.tokenize(text);
        if (tokens.length == 0) {
            printError(ERR_NOT_ENOUGH_ARGS);
            return;
        }
        for (int i = 0; i < tokens.length; i++) {
            printLine(i + ": " + tokens[i].getType() + " " + displayLexeme(tokens[i].getLexeme()));
        }
    }
}
