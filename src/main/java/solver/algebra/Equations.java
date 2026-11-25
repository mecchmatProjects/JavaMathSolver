package solver.algebra;

import java.util.Locale;
import static solver.core.Output.*;

/**
 * ALG Lab 2: лінійні та квадратні рівняння.
 */
public final class Equations {

    private static final double EPS = 1e-9;

    private static boolean isZero(double value) {
        return Math.abs(value) <= EPS;
    }

    private Equations() {
    }

    // --- Algebra Lab 2 ---
    public static void solveLinear(double a, double b) {
        if (isZero(a)) {
            if (isZero(b)) {
                printResult("Infinite solutions");
            } else {
                printResult("No solution");
            }
        } else {
            double x = -b / a;
            printResult(x);
        }
    }

    public static void solveQuadratic(double a, double b, double c) {
        if (isZero(a)) {
            solveLinear(b, c);
            return;
        }

        double d = b * b - 4.0 * a * c;

        if (d > 0.0 && !isZero(d)) {
            double x1, x2;
            if (b >= 0.0 || isZero(b)) {
                x1 = (-b - Math.sqrt(d)) / (2.0 * a);
                x2 = (2.0 * c) / (-b - Math.sqrt(d));
            } else {
                x1 = (2.0 * c) / (-b + Math.sqrt(d));
                x2 = (-b + Math.sqrt(d)) / (2.0 * a);
            }
            printResult(String.format(Locale.ROOT, "x1=%f, x2=%f", Math.min(x1, x2), Math.max(x1, x2)));
        } else if (isZero(d)) {
            double x = -b / (2.0 * a);
            printResult(x);
        } else {
            printResult("No real roots");
        }
    }
}
