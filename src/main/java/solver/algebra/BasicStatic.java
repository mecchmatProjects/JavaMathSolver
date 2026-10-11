package solver.algebra;

public class BasicStatic {

    private static double calculateSquaredDiffs(double[] array) {
        double mean = 0.0;
        double sumSquaredDiffs = 0.0;
        for (int i = 0; i < array.length; i++) {
            double delta = array[i] - mean;
            mean += delta / (i + 1);
            sumSquaredDiffs += delta * (array[i] - mean);
        }
        return sumSquaredDiffs;
    }
    
    public static double variance(double[] array) {
        if (array == null || array.length == 0) {
            return 0.0;
        }

        return calculateSquaredDiffs(array) / array.length;
    }

    ///вибіркова дисперсія - незміщена оцінка
    public static double sampleVariance(double[] array) {
        if (array == null || array.length <= 1) {
            return 0.0;
        }

        return calculateSquaredDiffs(array) / (array.length - 1);
    }

    ///стандартне відхилення - генеральне
    public static double standardDeviation(double[] array) {
        double v = variance(array);
        if (v <= 0.0) {
            return 0.0;
        }

        return Math.sqrt(v);
    }

    ///стандартне відхилення - вибіркове
    public static double sampleStandardDeviation(double[] array) {
        double v = sampleVariance(array);
        if (v <= 0.0) {
            return 0.0;
        }
        return Math.sqrt(v);
    }

}
