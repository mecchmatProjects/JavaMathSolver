package solver.algebra;

import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class BinaryExpressionTest {

    // Проста імітація константи та змінної для тестування дерева виразів
    private static class ConstExpr extends Expression {
        private final double val;
        ConstExpr(double val) { this.val = val; }
        @Override public double evaluate(Map<String, Double> vars) { return val; }
        @Override public String toString() { return (val == (long) val) ? String.format("%d", (long) val) : String.valueOf(val); }
        @Override public boolean equals(Object o) { return o instanceof ConstExpr && Double.compare(((ConstExpr) o).val, val) == 0; }
        @Override public int hashCode() { return Double.hashCode(val); }
    }

    private static class VarExpr extends Expression {
        private final String name;
        VarExpr(String name) { this.name = name; }
        @Override public double evaluate(Map<String, Double> vars) {
            if (vars == null || !vars.containsKey(name)) {
                throw new IllegalArgumentException("Variable not found: " + name);
            }
            return vars.get(name);
        }
        @Override public String toString() { return name; }
        @Override public boolean equals(Object o) { return o instanceof VarExpr && ((VarExpr) o).name.equals(name); }
        @Override public int hashCode() { return name.hashCode(); }
    }

    @Test
    void testBasicModelAndToString() {
        Expression expr = new BinaryExpression(new VarExpr("x"), "+", new ConstExpr(5));
        assertEquals("x + 5", expr.toString());
    }

    @Test
    void testEvaluationTaskA15Example() {
        // x + 5 при x = 10 -> 15
        Expression expr = new BinaryExpression(new VarExpr("x"), BinaryExpression.Operator.ADD, new ConstExpr(5));
        Map<String, Double> vars = Collections.singletonMap("x", 10.0);

        assertEquals(15.0, expr.evaluate(vars), 1e-9);
    }

    @Test
    void testAllArithmeticOperators() {
        Expression a = new ConstExpr(12);
        Expression b = new ConstExpr(3);

        assertEquals(15.0, new BinaryExpression(a, "+", b).evaluate(), 1e-9);
        assertEquals(9.0, new BinaryExpression(a, "-", b).evaluate(), 1e-9);
        assertEquals(36.0, new BinaryExpression(a, "*", b).evaluate(), 1e-9);
        assertEquals(4.0, new BinaryExpression(a, "/", b).evaluate(), 1e-9);
    }

    @Test
    void testDivisionByZeroThrowsException() {
        Expression expr = new BinaryExpression(new ConstExpr(10), "/", new ConstExpr(0));
        assertThrows(ArithmeticException.class, expr::evaluate);
    }

    @Test
    void testPrecedenceParenthesesInToString() {
        // (x + 2) * y
        Expression add = new BinaryExpression(new VarExpr("x"), "+", new ConstExpr(2));
        Expression mul = new BinaryExpression(add, "*", new VarExpr("y"));
        assertEquals("(x + 2) * y", mul.toString());

        // x + 2 * y (без дужок навколо множення)
        Expression mul2 = new BinaryExpression(new ConstExpr(2), "*", new VarExpr("y"));
        Expression add2 = new BinaryExpression(new VarExpr("x"), "+", mul2);
        assertEquals("x + 2 * y", add2.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        Expression e1 = new BinaryExpression(new VarExpr("x"), "+", new ConstExpr(5));
        Expression e2 = new BinaryExpression(new VarExpr("x"), "+", new ConstExpr(5));
        Expression e3 = new BinaryExpression(new VarExpr("x"), "-", new ConstExpr(5));

        assertEquals(e1, e2);
        assertNotEquals(e1, e3);
        assertEquals(e1.hashCode(), e2.hashCode());
    }
}