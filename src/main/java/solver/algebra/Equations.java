package solver.algebra;

import java.util.Locale;
import static solver.core.Output.*;

/**
 * ALG Lab 2: лінійні та квадратні рівняння.
 */
public final class Equations {

    private Equations() {
    }

    // --- Algebra Lab 2 ---
    public static void solveLinear(double a, double b) {
        if (a == 0.0) {
            if (b == 0.0) {
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
        if (a == 0.0) {
            solveLinear(b, c);
            return;
        }

        double d = b * b - 4.0 * a * c;

        if (d > 0.0) {
            double x1, x2;
            if (b >= 0.0) {
                x1 = (-b - Math.sqrt(d)) / (2.0 * a);
                x2 = (2.0 * c) / (-b - Math.sqrt(d));
            } else {
                x1 = (2.0 * c) / (-b + Math.sqrt(d));
                x2 = (-b + Math.sqrt(d)) / (2.0 * a);
            }
            printResult(String.format(Locale.ROOT, "x1=%f, x2=%f", Math.min(x1, x2), Math.max(x1, x2)));
        } else if (d == 0.0) {
            double x = -b / (2.0 * a);
            printResult(x);
        } else {
            printResult("No real roots");
        }
    }
}
