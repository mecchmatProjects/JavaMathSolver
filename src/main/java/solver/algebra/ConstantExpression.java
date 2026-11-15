package solver.algebra;

import java.util.Map;
import java.util.Objects;

public class ConstantExpression extends Expression {

    private final double value;

    public ConstantExpression(double value) {
        this.value = value;
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
        if (value == (long) value) {
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
        return Double.compare(other.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
