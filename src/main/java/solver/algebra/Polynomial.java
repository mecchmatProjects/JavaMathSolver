package solver.algebra;

import java.util.Arrays;

public class Polynomial {
    private static final double EPSILON = 1e-12;
    private final double[] coeffs;

    public Polynomial(double[] coefficients) {
        if (coefficients == null || coefficients.length == 0) {
            throw new IllegalArgumentException("Coefficient array cannot be null or empty");
        }

        for (double c : coefficients) {
            if (Double.isNaN(c) || Double.isInfinite(c)) {
                throw new IllegalArgumentException("Polynomial coefficients must be finite numbers");
            }
        }

        int len = coefficients.length;
        while (len > 1 && Math.abs(coefficients[len - 1]) < EPSILON) {
            len--;
        }

        this.coeffs = new double[len];
        for (int i = 0; i < len; i++) {
            this.coeffs[i] = (Math.abs(coefficients[i]) < EPSILON) ? 0.0 : coefficients[i];
        }
    }

    public int degree() {
        return coeffs.length - 1;
    }

    public boolean isZero() {
        return coeffs.length == 1 && coeffs[0] == 0.0;
    }

    public double getCoefficient(int power) {
        if (power < 0) {
            throw new IndexOutOfBoundsException("Power cannot be negative: " + power);
        }
        if (power >= coeffs.length) {
            return 0.0;
        }
        return coeffs[power];
    }

    public double[] getCoefficients() {
        return coeffs.clone();
    }
}
