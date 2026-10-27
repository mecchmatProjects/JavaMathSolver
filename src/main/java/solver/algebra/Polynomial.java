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

    public double evaluate(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) {
            throw new IllegalArgumentException("Argument x must be a finite number");
        }
        double result = 0.0;
        for (int i = coeffs.length - 1; i >= 0; i--) {
            result = result * x + coeffs[i];
        }
        return result + 0.0;
    }

    public boolean equals(Polynomial other, double eps) {
        if (other == null) return false;
        if (this == other) return true;
        if (this.coeffs.length != other.coeffs.length) return false;
        for (int i = 0; i < coeffs.length; i++) {
            if (Math.abs(this.coeffs[i] - other.coeffs[i]) > eps) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Polynomial that = (Polynomial) o;
        return Arrays.equals(this.coeffs, that.coeffs);
    }

    @Override
    public String toString() {
        if (isZero()) {
            return "0.0";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = coeffs.length - 1; i >= 0; i--) {
            double c = coeffs[i];
            if (Math.abs(c) < EPSILON) continue;

            if (sb.length() > 0) {
                sb.append(c > 0 ? " + " : " - ");
            } else if (c < 0) {
                sb.append("-");
            }

            double absC = Math.abs(c);
            if (i == 0 || Math.abs(absC - 1.0) >= EPSILON) {
                sb.append(absC);
            }
            if (i >= 1) {
                sb.append("x");
                if (i > 1) {
                    sb.append("^").append(i);
                }
            }
        }
        return sb.toString();
    }

    public Polynomial derivative() {
        if (coeffs.length <= 1) {
            return new Polynomial(new double[]{0.0});
        }
        double[] deriv = new double[coeffs.length - 1];
        for (int i = 1; i < coeffs.length; i++) {
            deriv[i - 1] = coeffs[i] * i;
        }
        return new Polynomial(deriv);
    }

    public Polynomial add(Polynomial other) {
        if (other == null) {
            throw new IllegalArgumentException("Operand polynomial cannot be null");
        }
        int maxLen = Math.max(this.coeffs.length, other.coeffs.length);
        double[] res = new double[maxLen];
        for (int i = 0; i < maxLen; i++) {
            res[i] = this.getCoefficient(i) + other.getCoefficient(i);
        }
        return new Polynomial(res);
    }

    public Polynomial subtract(Polynomial other) {
        if (other == null) {
            throw new IllegalArgumentException("Operand polynomial cannot be null");
        }
        int maxLen = Math.max(this.coeffs.length, other.coeffs.length);
        double[] res = new double[maxLen];
        for (int i = 0; i < maxLen; i++) {
            res[i] = this.getCoefficient(i) - other.getCoefficient(i);
        }
        return new Polynomial(res);
    }

    public Polynomial multiply(Polynomial other) {
        if (other == null) {
            throw new IllegalArgumentException("Operand polynomial cannot be null");
        }
        if (this.isZero() || other.isZero()) {
            return new Polynomial(new double[]{0.0});
        }
        double[] res = new double[this.coeffs.length + other.coeffs.length - 1];
        for (int i = 0; i < this.coeffs.length; i++) {
            for (int j = 0; j < other.coeffs.length; j++) {
                res[i + j] += this.coeffs[i] * other.coeffs[j];
            }
        }
        return new Polynomial(res);
    }

}
