package solver.algebra;

import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ExpressionTest {

    // Тестова реалізація для перевірки базового контракту Expression
    private static class DummyExpression extends Expression {
        private final double value;

        DummyExpression(double value) {
            this.value = value;
        }

        @Override
        public double evaluate(Map<String, Double> variables) {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            DummyExpression that = (DummyExpression) obj;
            return Double.compare(that.value, this.value) == 0;
        }

        @Override
        public int hashCode() {
            return Double.hashCode(value);
        }
    }

    @Test
    void testEvaluateWithoutArgumentsDelegatesToEmptyMap() {
        Expression expr = new DummyExpression(42.0);
        assertEquals(42.0, expr.evaluate(), 1e-9);
    }

    @Test
    void testEvaluateWithVariables() {
        Expression expr = new DummyExpression(15.0);
        Map<String, Double> vars = Collections.singletonMap("x", 10.0);
        assertEquals(15.0, expr.evaluate(vars), 1e-9);
    }

    @Test
    void testToStringAndEquality() {
        Expression expr1 = new DummyExpression(5.0);
        Expression expr2 = new DummyExpression(5.0);
        Expression expr3 = new DummyExpression(10.0);

        assertEquals("5.0", expr1.toString());
        assertEquals(expr1, expr2);
        assertNotEquals(expr1, expr3);
        assertEquals(expr1.hashCode(), expr2.hashCode());
    }
}