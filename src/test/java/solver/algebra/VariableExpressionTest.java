package solver.algebra;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class VariableExpressionTest {

    @Test
    void toStringReturnsName() {
        assertEquals("x", new VariableExpression("x").toString());
    }

    @Test
    void getNameReturnsName() {
        assertEquals("y", new VariableExpression("y").getName());
    }

    @Test
    void evaluateReturnsValueFromMap() {
        Expression x = new VariableExpression("x");
        assertEquals(10.0, x.evaluate(Map.of("x", 10.0)));
    }

    @Test
    void evaluateUsesOnlyItsOwnVariable() {
        Expression y = new VariableExpression("y");
        assertEquals(2.5, y.evaluate(Map.of("x", 10.0, "y", 2.5, "z", -1.0)));
    }

    @Test
    void evaluateThrowsWhenVariableIsMissing() {
        Expression z = new VariableExpression("z");
        assertThrows(IllegalArgumentException.class, () -> z.evaluate(Map.of("x", 1.0)));
    }

    @Test
    void evaluateWithoutArgumentsThrows() {
        assertThrows(IllegalArgumentException.class, () -> new VariableExpression("x").evaluate());
    }

    @Test
    void constructorRejectsNullAndBlankNames() {
        assertThrows(IllegalArgumentException.class, () -> new VariableExpression(null));
        assertThrows(IllegalArgumentException.class, () -> new VariableExpression(""));
        assertThrows(IllegalArgumentException.class, () -> new VariableExpression("   "));
    }

    @Test
    void equalVariablesAreEqualAndHaveSameHashCode() {
        VariableExpression a = new VariableExpression("x");
        VariableExpression b = new VariableExpression("x");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void differentVariablesAreNotEqual() {
        assertNotEquals(new VariableExpression("x"), new VariableExpression("y"));
    }

    @Test
    void variableIsNotEqualToNullOrOtherTypes() {
        VariableExpression x = new VariableExpression("x");
        assertNotEquals(null, x);
        assertNotEquals("x", x);
    }

    @Test
    void evaluateThrowsWhenVariablesMapIsNull() {
        Expression x = new VariableExpression("x");
        assertThrows(IllegalArgumentException.class, () -> x.evaluate(null));
    }
}
