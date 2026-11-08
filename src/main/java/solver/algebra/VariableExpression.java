package solver.algebra;

import java.util.Map;

/**
 * (Task A13): x, y, z.
 */
public class VariableExpression extends Expression {

    private final String name;

    public VariableExpression(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Variable name must not be empty");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

@Override
public double evaluate(Map<String, Double> variables) {
    if (variables == null) {
        throw new IllegalArgumentException("Variables map must not be null");
    }
    Double value = variables.get(name);
    if (value == null) {
        throw new IllegalArgumentException("No value provided for variable: " + name);
    }
    return value;
}

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof VariableExpression)) return false;
        return name.equals(((VariableExpression) obj).name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
