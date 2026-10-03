package solver.geometry;

import static solver.core.Output.*;
import static solver.core.Messages.*;

/**
 * GEO: обʼєми тіл.
 */
public final class Solids {

    private Solids() {
    }

    // Task 11: Cylinder volume
    public static void calculateCylinderVolume(double radius, double height) {
        if (radius < 0 || height < 0) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        double vol = Math.PI * radius * radius * height;
        if (Double.isInfinite(vol) || Double.isNaN(vol)) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        printResult(vol);
    }

    // Task 12: Cone volume
    public static void calculateConeVolume(double radius, double height) {
        if (radius < 0 || height < 0) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        double vol = Math.PI * radius * radius * height / 3;
        if (Double.isInfinite(vol) || Double.isNaN(vol)) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        printResult(vol);
    }

    // Task 13: Torus volume
    public static void calculateTorusVolume(double innerRadius, double outerRadius) {
        if (innerRadius < 0 || outerRadius < 0 || innerRadius > outerRadius) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        double tubeRadius = (outerRadius - innerRadius) / 2;
        double centerRadius = (outerRadius + innerRadius) / 2;
        double vol = 2 * Math.PI * Math.PI * centerRadius * tubeRadius * tubeRadius;
        if (Double.isInfinite(vol) || Double.isNaN(vol)) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        printResult(vol);
    }
}
