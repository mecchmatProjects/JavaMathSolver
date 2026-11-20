package solver.algebra;

import java.util.Map;
import java.util.Objects;

public class ConstantExpression extends Expression {

    private static final double EPS = 1e-9;

    private static boolean equal(double a, double b) {
        return Math.abs(a - b) <= EPS;
    }

    private final double value;

    public ConstantExpression(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException("Constant value must be a finite number");
        }
        this.value = value + 0.0;
    }

    public double getValue() {
        return value;
    }

    @Override
    public double evaluate(Map<String, Double> variables) {
        return this.value;
    }

    @Override
    public String toString() {
        if (equal(value, (long) value)) {
            return String.format("%d", (long) value);
        } else {
            return String.format("%s", value);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ConstantExpression other = (ConstantExpression) obj;
        return equal(other.value, value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
