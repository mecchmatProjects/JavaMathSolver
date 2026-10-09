package solver.algebra;

public class Basic_static {

    public static double variance(double[] array) {
        if (array == null || array.length == 0) {
            return 0.0;
        }

        double sum = 0.0;
        for (double val : array) {
            sum += val;
        }
        double mean = sum / array.length;

        double sumSquaredDiffs = 0.0;
        for (double val : array) {
            double diff = val - mean;
            sumSquaredDiffs += diff * diff;
        }

        return sumSquaredDiffs / array.length;
    }

    ///вибіркова дисперсія - незміщена оцінка
    public static double sampleVariance(double[] array) {
        if (array == null || array.length <= 1) {
            return 0.0;
        }

        double sum = 0.0;
        for (double val : array) {
            sum += val;
        }
        double mean = sum / array.length;

        double sumSquaredDiffs = 0.0;
        for (double val : array) {
            double diff = val - mean;
            sumSquaredDiffs += diff * diff;
        }

        return sumSquaredDiffs / (array.length - 1);
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