package solver.geometry;

import java.util.Locale;
import static solver.core.Output.*;
import static solver.core.Messages.*;

/**
 * GEO: трикутники.
 */
public final class Triangles {

    private Triangles() {
    }

    // --- Geometry Lab 2 ---
    public static double heronArea(double sideA, double sideB, double sideC) {
        double m = Math.max(sideA, Math.max(sideB, sideC));
        if (m <= 0) {
            return 0;
        }
        double a = sideA / m;
        double b = sideB / m;
        double c = sideC / m;
        double s = (a + b + c) / 2.0;
        double diffA = s - a;
        double diffB = s - b;
        double diffC = s - c;
        if (diffA < 0 || diffB < 0 || diffC < 0) {
            return 0;
        }
        double scaledArea = Math.sqrt(s * diffA * diffB * diffC);
        return (scaledArea * m) * m;
    }

    public static void calculateTriangleArea(double sideA, double sideB, double sideC) {
        if (!triangleValid(sideA, sideB, sideC)) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        double area = heronArea(sideA, sideB, sideC);
        if (Double.isInfinite(area) || Double.isNaN(area)) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        printResult(area);
    }

    public static boolean triangleValid(double sideA, double sideB, double sideC) {
        return sideA > 0 && sideB > 0 && sideC > 0
                && (sideA + sideB > sideC)
                && (sideA + sideC > sideB)
                && (sideB + sideC > sideA);
    }

    public static void calculateTriangleValid(double sideA, double sideB, double sideC) {
        printResult(triangleValid(sideA, sideB, sideC));
    }

    // Task 8a: Triangle medians
    public static void calculateMedians(double sideA, double sideB, double sideC) {
        if (!triangleValid(sideA, sideB, sideC)) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        double medA = 0.5 * Math.sqrt(Math.max(0, 2 * sideB * sideB + 2 * sideC * sideC - sideA * sideA));
        double medB = 0.5 * Math.sqrt(Math.max(0, 2 * sideA * sideA + 2 * sideC * sideC - sideB * sideB));
        double medC = 0.5 * Math.sqrt(Math.max(0, 2 * sideA * sideA + 2 * sideB * sideB - sideC * sideC));
        if (Double.isInfinite(medA) || Double.isInfinite(medB) || Double.isInfinite(medC)) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        printResult(String.format(Locale.ROOT, "m_a=%f, m_b=%f, m_c=%f", medA, medB, medC));
    }

    // Task 8b: Triangle bisectors
    public static void calculateBisectors(double sideA, double sideB, double sideC) {
        if (!triangleValid(sideA, sideB, sideC)) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        double bisA = Math.sqrt(Math.max(0, sideB * sideC * ((sideB + sideC) * (sideB + sideC) - sideA * sideA))) / (sideB + sideC);
        double bisB = Math.sqrt(Math.max(0, sideA * sideC * ((sideA + sideC) * (sideA + sideC) - sideB * sideB))) / (sideA + sideC);
        double bisC = Math.sqrt(Math.max(0, sideA * sideB * ((sideA + sideB) * (sideA + sideB) - sideC * sideC))) / (sideA + sideB);
        if (Double.isInfinite(bisA) || Double.isInfinite(bisB) || Double.isInfinite(bisC)) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        printResult(String.format(Locale.ROOT, "l_a=%f, l_b=%f, l_c=%f", bisA, bisB, bisC));
    }

    // Task 8c: Triangle heights
    public static void calculateHeights(double sideA, double sideB, double sideC) {
        if (!triangleValid(sideA, sideB, sideC)) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        double area = heronArea(sideA, sideB, sideC);
        if (Double.isInfinite(area) || Double.isNaN(area)) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        double hA = 2 * area / sideA;
        double hB = 2 * area / sideB;
        double hC = 2 * area / sideC;
        if (Double.isInfinite(hA) || Double.isInfinite(hB) || Double.isInfinite(hC)) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        printResult(String.format(Locale.ROOT, "h_a=%f, h_b=%f, h_c=%f", hA, hB, hC));
    }

    // Task 9: Area by angles (in radians) and inradius
    public static void calculateAreaByAnglesAndInradius(double angleA, double angleB, double angleC, double inradius) {
        if (inradius <= 0 || angleA <= 0 || angleB <= 0 || angleC <= 0 || angleA >= Math.PI || angleB >= Math.PI || angleC >= Math.PI) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        if (Math.abs((angleA + angleB + angleC) - Math.PI) > 1e-4) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        double sumCot = 1 / Math.tan(angleA / 2) + 1 / Math.tan(angleB / 2) + 1 / Math.tan(angleC / 2);
        double result = inradius * inradius * sumCot;
        if (Double.isInfinite(result) || Double.isNaN(result)) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        printResult(result);
    }

    // Task 10: Triangle angles (in radians and degrees)
    public static void calculateTriangleAngles(double sideA, double sideB, double sideC) {
        if (!triangleValid(sideA, sideB, sideC)) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        double cosA = Math.min(1.0, Math.max(-1.0, (sideB * sideB + sideC * sideC - sideA * sideA) / (2 * sideB * sideC)));
        double cosB = Math.min(1.0, Math.max(-1.0, (sideA * sideA + sideC * sideC - sideB * sideB) / (2 * sideA * sideC)));
        double cosC = Math.min(1.0, Math.max(-1.0, (sideA * sideA + sideB * sideB - sideC * sideC) / (2 * sideA * sideB)));

        double radA = Math.acos(cosA);
        double radB = Math.acos(cosB);
        double radC = Math.acos(cosC);

        printResult(String.format(Locale.ROOT, "A=%f rad (%f deg), B=%f rad (%f deg), C=%f rad (%f deg)",
                radA, Math.toDegrees(radA), radB, Math.toDegrees(radB), radC, Math.toDegrees(radC)));
    }
}
