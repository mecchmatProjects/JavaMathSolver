package solver.algebra;

import java.util.Map;
import java.util.Objects;

/**
 * Модель бінарного математичного виразу: left operator right (Task A14, A15).
 * Наприклад: x + 5, a * b, (x + 2) / y.
 */
public class BinaryExpression extends Expression {

    public enum Operator {
        ADD("+", 1),
        SUBTRACT("-", 1),
        MULTIPLY("*", 2),
        DIVIDE("/", 2);

        private final String symbol;
        private final int precedence;

        Operator(String symbol, int precedence) {
            this.symbol = symbol;
            this.precedence = precedence;
        }

        public String getSymbol() {
            return symbol;
        }

        public int getPrecedence() {
            return precedence;
        }

        public static Operator fromSymbol(String symbol) {
            if (symbol == null) {
                throw new IllegalArgumentException("Operator symbol cannot be null");
            }
            switch (symbol.trim()) {
                case "+": return ADD;
                case "-": return SUBTRACT;
                case "*": return MULTIPLY;
                case "/": return DIVIDE;
                default:
                    throw new IllegalArgumentException("Unsupported operator: " + symbol);
            }
        }
    }

    private final Expression left;
    private final Operator operator;
    private final Expression right;

    public BinaryExpression(Expression left, Operator operator, Expression right) {
        if (left == null || operator == null || right == null) {
            throw new IllegalArgumentException("Operands and operator must not be null");
        }
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public BinaryExpression(Expression left, String operatorSymbol, Expression right) {
        this(left, Operator.fromSymbol(operatorSymbol), right);
    }

    public Expression getLeft() {
        return left;
    }

    public Operator getOperator() {
        return operator;
    }

    public Expression getRight() {
        return right;
    }

    /**
     * Обчислює значення бінарного виразу рекурсивно (Task A15).
     */
    @Override
    public double evaluate(Map<String, Double> variables) {
        double leftVal = left.evaluate(variables);
        double rightVal = right.evaluate(variables);

        switch (operator) {
            case ADD:
                return leftVal + rightVal;
            case SUBTRACT:
                return leftVal - rightVal;
            case MULTIPLY:
                return leftVal * rightVal;
            case DIVIDE:
                if (Math.abs(rightVal) < 1e-12) {
                    throw new ArithmeticException("Division by zero in expression evaluation");
                }
                return leftVal / rightVal;
            default:
                throw new IllegalStateException("Unknown operator: " + operator);
        }
    }

    @Override
    public String toString() {
        String leftStr = formatChild(left, false);
        String rightStr = formatChild(right, true);
        return leftStr + " " + operator.getSymbol() + " " + rightStr;
    }

    private String formatChild(Expression child, boolean isRightChild) {
        if (child instanceof BinaryExpression) {
            BinaryExpression binChild = (BinaryExpression) child;
            int childPrec = binChild.operator.getPrecedence();
            int currentPrec = this.operator.getPrecedence();

            // Ставимо дужки, якщо пріоритет дочірнього виразу нижчий,
            // або якщо це правий операнд віднімання/ділення однакового пріоритету: a - (b - c)
            if (childPrec < currentPrec || (isRightChild && childPrec == currentPrec && (operator == Operator.SUBTRACT || operator == Operator.DIVIDE))) {
                return "(" + binChild.toString() + ")";
            }
        }
        return child.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BinaryExpression that = (BinaryExpression) o;
        return left.equals(that.left) &&
                operator == that.operator &&
                right.equals(that.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, operator, right);
    }
}