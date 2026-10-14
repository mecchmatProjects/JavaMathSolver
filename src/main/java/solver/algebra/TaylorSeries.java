package solver.algebra;

import static solver.core.Output.*;
import static solver.core.Messages.*;

/**
 * ALG Lab 2: ряди Тейлора.
 */
public final class TaylorSeries {

    private static final int MAX_SERIES_ITERATIONS = 10_000_000;

    private TaylorSeries() {
    }

    private static double normalizeAngle(double x) {
        x = x % (2.0 * Math.PI);
        if (x > Math.PI) x -= 2.0 * Math.PI;
        if (x < -Math.PI) x += 2.0 * Math.PI;
        return x;
    }

    private static boolean validateInfiniteSeriesInputs(double x, double eps) {
        if (!Double.isFinite(x) || !Double.isFinite(eps) || eps <= 0.0) {
            printError(ERR_INVALID_INPUT);
            return false;
        }
        return true;
    }

    private static boolean validateSeriesInputs(double x, double eps) {
        if (!Double.isFinite(x) || !Double.isFinite(eps) || Math.abs(x) >= 1.0 || eps <= 0.0) {
            printError(ERR_INVALID_INPUT);
            return false;
        }
        return true;
    }

    public static void calculateTaylorSin(double x, double eps) {
        if (!validateInfiniteSeriesInputs(x, eps)) return;

        x = normalizeAngle(x);

        double sum = 0.0;
        double a = x;
        int step = 2;
        int iter = 0;

        while (Math.abs(a) >= eps && iter++ < MAX_SERIES_ITERATIONS) {
            sum += a;
            a = -a * x * x / ((double) step * (step + 1));
            if (!Double.isFinite(a) || !Double.isFinite(sum) || a == 0.0) break;
            step += 2;
        }
        printResult(sum);
    }

    public static void calculateTaylorCos(double x, double eps) {
        if (!validateInfiniteSeriesInputs(x, eps)) return;

        x = normalizeAngle(x);

        double sum = 0.0;
        double a = 1.0;
        int k = 1;
        int iter = 0;

        while (Math.abs(a) >= eps && iter++ < MAX_SERIES_ITERATIONS) {
            sum += a;
            a = -a * x * x / ((2.0 * k - 1.0) * (2.0 * k));
            if (!Double.isFinite(a) || !Double.isFinite(sum) || a == 0.0) break;
            k++;
        }
        printResult(sum);
    }

    public static void calculateTaylorSinh(double x, double eps) {
        if (!validateInfiniteSeriesInputs(x, eps)) return;

        double sum = 0.0;
        double a = x;
        int k = 1;
        int iter = 0;

        while (Math.abs(a) >= eps && iter++ < MAX_SERIES_ITERATIONS) {
            sum += a;
            a = a * (x * x) / ((2.0 * k) * (2.0 * k + 1.0));
            if (!Double.isFinite(a) || !Double.isFinite(sum) || a == 0.0) break;
            k++;
        }
        printResult(sum);
    }

    public static void calculateTaylorCosh(double x, double eps) {
        if (!validateInfiniteSeriesInputs(x, eps)) return;

        double sum = 0.0;
        double a = 1.0;
        int k = 1;
        double xSquared = x * x;
        int iter = 0;

        while (Math.abs(a) >= eps && iter++ < MAX_SERIES_ITERATIONS) {
            sum += a;
            a = a * xSquared / ((2.0 * k - 1.0) * (2.0 * k));
            if (!Double.isFinite(a) || !Double.isFinite(sum) || a == 0.0) break;
            k++;
        }
        printResult(sum);
    }

    public static void calculateTaylorExp(double x, double eps) {
        if (!validateInfiniteSeriesInputs(x, eps)) return;

        boolean isNegative = x < 0.0;
        double absX = Math.abs(x);
        double sum = 0.0;
        double a = 1.0;
        int k = 1;
        int iter = 0;

        while (Math.abs(a) >= eps && iter++ < MAX_SERIES_ITERATIONS) {
            sum += a;
            a = a * absX / k;
            if (!Double.isFinite(a) || !Double.isFinite(sum) || a == 0.0) break;
            k++;
        }
        printResult(isNegative ? (1.0 / sum) : sum);
    }

    public static void calculateTaylorLnOnePlusX(double x, double eps) {
        if (!validateSeriesInputs(x, eps)) return;

        double sum = 0.0;
        double a = x;
        int k = 1;
        int iter = 0;

        while (Math.abs(a) >= eps && iter++ < MAX_SERIES_ITERATIONS) {
            sum += a;
            k++;
            a = -a * x * (k - 1.0) / k;
            if (!Double.isFinite(a) || !Double.isFinite(sum) || a == 0.0) break;
        }
        printResult(sum);
    }

    public static void calculateTaylorGeomSeries(double x, double eps) {
        if (!validateSeriesInputs(x, eps)) return;

        double sum = 0.0;
        double a = 1.0;
        int iter = 0;

        while (Math.abs(a) >= eps && iter++ < MAX_SERIES_ITERATIONS) {
            sum += a;
            a = -a * x;
            if (!Double.isFinite(a) || !Double.isFinite(sum) || a == 0.0) break;
        }
        printResult(sum);
    }

    public static void calculateTaylorArtanh(double x, double eps) {
        if (!validateSeriesInputs(x, eps)) return;

        double sum = 0.0;
        double a = x;
        int k = 1;
        double xSquared = x * x;
        int iter = 0;

        while (Math.abs(a) >= eps && iter++ < MAX_SERIES_ITERATIONS) {
            sum += a;
            k++;
            a = a * xSquared * (2.0 * k - 3.0) / (2.0 * k - 1.0);
            if (!Double.isFinite(a) || !Double.isFinite(sum) || a == 0.0) break;
        }
        printResult(sum);
    }

    public static void calculateTaylorSqrtOnePlusX(double x, double eps) {
        if (!validateSeriesInputs(x, eps)) return;

        double sum = 0.0;
        double a = 1.0;
        int k = 1;
        int iter = 0;

        while (Math.abs(a) >= eps && iter++ < MAX_SERIES_ITERATIONS) {
            sum += a;
            a = -a * x * (2.0 * k - 3.0) / (2.0 * k);
            if (!Double.isFinite(a) || !Double.isFinite(sum) || a == 0.0) break;
            k++;
        }
        printResult(sum);
    }

    public static void calculateTaylorInvSqrtOnePlusX(double x, double eps) {
        if (!validateSeriesInputs(x, eps)) return;

        double sum = 0.0;
        double a = 1.0;
        int k = 1;
        int iter = 0;

        while (Math.abs(a) >= eps && iter++ < MAX_SERIES_ITERATIONS) {
            sum += a;
            a = -a * x * (2.0 * k - 1.0) / (2.0 * k);
            if (!Double.isFinite(a) || !Double.isFinite(sum) || a == 0.0) break;
            k++;
        }
        printResult(sum);
    }

    public static void calculateTaylorAsin(double x, double eps) {
        if (!validateSeriesInputs(x, eps)) return;

        double sum = 0.0;
        double a = x;
        int k = 1;
        double xSquared = x * x;
        int iter = 0;

        while (Math.abs(a) >= eps && iter++ < MAX_SERIES_ITERATIONS) {
            sum += a;
            a = a * xSquared * ((2.0 * k - 1.0) * (2.0 * k - 1.0)) / (2.0 * k * (2.0 * k + 1.0));
            if (!Double.isFinite(a) || !Double.isFinite(sum) || a == 0.0) break;
            k++;
        }
        printResult(sum);
    }

    public static void calculateTaylorInvSq(double x, double eps) {
        if (!validateSeriesInputs(x, eps)) return;

        double sum = 0.0;
        double a = 1.0;
        int k = 0;
        int iter = 0;

        while (Math.abs(a) >= eps && iter++ < MAX_SERIES_ITERATIONS) {
            sum += a;
            k++;
            a = -a * x * ((k + 1.0) / k);
            if (!Double.isFinite(a) || !Double.isFinite(sum) || a == 0.0) break;
        }
        printResult(sum);
    }

    public static void calculateTaylorInvCube(double x, double eps) {
        if (!validateSeriesInputs(x, eps)) return;

        double sum = 0.0;
        double a = 1.0;
        int k = 1;
        int iter = 0;

        while (Math.abs(a) >= eps && iter++ < MAX_SERIES_ITERATIONS) {
            sum += a;
            k++;
            a = -a * x * (k + 1.0) / (k - 1.0);
            if (!Double.isFinite(a) || !Double.isFinite(sum) || a == 0.0) break;
        }
        printResult(sum);
    }

    public static void calculateTaylorInvOnePlusX2(double x, double eps) {
        if (!validateSeriesInputs(x, eps)) return;

        double sum = 0.0;
        double a = 1.0;
        double xSquared = x * x;
        int iter = 0;

        while (Math.abs(a) >= eps && iter++ < MAX_SERIES_ITERATIONS) {
            sum += a;
            a = -a * xSquared;
            if (!Double.isFinite(a) || !Double.isFinite(sum) || a == 0.0) break;
        }
        printResult(sum);
    }
}
