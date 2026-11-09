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

            default:
                printUnknownCommand(command);
                break;
        }
    }

    // C0-b: диспетчеризація текстових команд (аргумент - увесь вираз одним рядком)
    public static void dispatchText(String command, String text) {
        switch (command) {
            // C10: tokenize, token-stats, validate-expr
            default:
                printUnknownCommand(command);
                break;
        }
    }
}
