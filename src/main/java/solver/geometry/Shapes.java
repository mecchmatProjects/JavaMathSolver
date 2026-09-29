package solver.geometry;

import static solver.core.Output.*;
import static solver.core.Messages.*;

/**
 * GEO: коло, прямокутник, еліпс.
 */
public final class Shapes {

    private Shapes() {
    }

    public static void calculateCircleArea(double r) {
        if (r < 0.0) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        printResult(Math.PI * r * r);
    }

    public static void calculateCircleCircumference(double r) {
        if (r < 0.0) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        printResult(2.0 * Math.PI * r);
    }

    public static void calculateRectangleArea(double a, double b) {
        if (a < 0.0 || b < 0.0) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        printResult(a * b);
    }

    public static void calculateRectanglePerimeter(double a, double b) {
        if (a < 0.0 || b < 0.0) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        printResult(2.0 * (a + b));
    }

    // Task 7: Ellipse area
    public static void calculateEllipseArea(double radiusA, double radiusB) {
        if (radiusA < 0 || radiusB < 0) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        printResult(Math.PI * radiusA * radiusB);
    }
}
