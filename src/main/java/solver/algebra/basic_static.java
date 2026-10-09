package solver.algebra;

public class basic_static {

    public static double variance(double[] array) {
        if (array == null || array.length == 0) {
            return 0;
        }

        double sum = 0;
        for (double val : array) {
            sum += val;
        }
        double mean = sum / array.length;

        double sumSquaredDiffs = 0;
        for (double val : array) {
            double diff = val - mean;
            sumSquaredDiffs += diff * diff;
        }

        return sumSquaredDiffs / array.length;
    }

    public static double standardDeviation(double[] array) {
        double v = variance(array);
        if (v <= 0) {
            return 0;
        }

        double x = v;
        for (int i = 0; i < 20; i++) {
            x = 0.5 * (x + v / x);
        }
        return x;
    }

}