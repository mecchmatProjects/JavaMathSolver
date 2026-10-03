package solver.geometry;

import static solver.core.Output.*;
import static solver.core.Messages.*;

/**
 * GEO: метод Монте-Карло для трикутника.
 */
public final class MonteCarlo {

    private MonteCarlo() {
    }

    // Monte Carlo simulation for triangle probability
    public static void calculateMonteCarloTriangle(int totalTrials) {
        calculateMonteCarloTriangle(totalTrials, 42L);
    }

    public static void calculateMonteCarloTriangle(int totalTrials, long seed) {
        if (totalTrials <= 0) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        java.util.Random rnd = new java.util.Random(seed);
        int validCount = 0;
        for (int i = 0; i < totalTrials; i++) {
            double sideA = rnd.nextDouble(), sideB = rnd.nextDouble(), sideC = rnd.nextDouble();
            if (sideA + sideB > sideC && sideA + sideC > sideB && sideB + sideC > sideA) {
                validCount++;
            }
        }
        printResult((double) validCount / totalTrials);
    }
}
