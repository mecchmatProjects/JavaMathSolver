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
    public int hashCode() {
        return Arrays.hashCode(coeffs);
    }

    private static String formatCoeff(double val) {
        if (Math.abs(val - Math.round(val)) < EPSILON) {
            return String.valueOf(Math.round(val));
        }
        return String.valueOf(val);
    }

    @Override
    public String toString() {
        if (isZero()) {
            return "0";
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
                sb.append(formatCoeff(absC));
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

    public Polynomial[] divideAndRemainder(Polynomial divisor) {
        if (divisor == null) {
            throw new IllegalArgumentException("Divisor polynomial cannot be null");
        }
        if (divisor.isZero()) {
            throw new ArithmeticException("Polynomial division by zero");
        }

        if (this.degree() < divisor.degree() || this.isZero()) {
            return new Polynomial[]{
                    new Polynomial(new double[]{0.0}),
                    this
            };
        }

        double[] rem = this.coeffs.clone();
        int divisorDeg = divisor.degree();
        double leadDivisorCoeff = divisor.coeffs[divisorDeg];
        double[] quot = new double[this.degree() - divisorDeg + 1];

        for (int i = this.degree() - divisorDeg; i >= 0; i--) {
            if (Math.abs(rem[i + divisorDeg]) < EPSILON) {
                rem[i + divisorDeg] = 0.0;
                continue;
            }
            double factor = rem[i + divisorDeg] / leadDivisorCoeff;
            quot[i] = factor;
            for (int j = 0; j <= divisorDeg; j++) {
                rem[i + j] -= factor * divisor.coeffs[j];
            }
            rem[i + divisorDeg] = 0.0;
        }

        return new Polynomial[]{
                new Polynomial(quot),
                new Polynomial(rem)
        };
    }

    public Polynomial divide(Polynomial other) {
        return divideAndRemainder(other)[0];
    }

    public Polynomial remainder(Polynomial other) {
        return divideAndRemainder(other)[1];
    }

    /**
     * Знаходить дійсні корені для поліномів степеня 1 (лінійні) та степеня 2 (квадратні) (Task A8).
     */
    public double[] roots() {
        int deg = degree();
        if (deg == 0) {
            return new double[0];
        }
        if (deg == 1) {
            // a1 * x + a0 = 0  =>  x = -a0 / a1
            return new double[]{-coeffs[0] / coeffs[1]};
        }
        if (deg == 2) {
            // a2 * x^2 + a1 * x + a0 = 0
            double a = coeffs[2];
            double b = coeffs[1];
            double c = coeffs[0];
            double d = b * b - 4 * a * c;

            if (d < -EPSILON) {
                return new double[0];
            }
            if (Math.abs(d) <= EPSILON) {
                return new double[]{-b / (2 * a)};
            }
            double sqrtD = Math.sqrt(d);
            double r1 = (-b - sqrtD) / (2 * a);
            double r2 = (-b + sqrtD) / (2 * a);
            return new double[]{Math.min(r1, r2), Math.max(r1, r2)};
        }
        throw new UnsupportedOperationException("Roots calculation is currently supported only for degree 1 and 2");
    }
}
