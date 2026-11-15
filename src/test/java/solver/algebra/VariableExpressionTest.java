package solver.algebra;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class VariableExpressionTest {

    @Nested
    class Construction {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   ", "\t", "\n", " \t\n "})
        void rejectsNullEmptyAndBlankNames(String name) {
            assertThrows(IllegalArgumentException.class, () -> new VariableExpression(name));
        }

        @ParameterizedTest
        @ValueSource(strings = {"x", "y", "z", "alpha", "x1", "var_2", "X", "Ω"})
        void acceptsValidNames(String name) {
            VariableExpression v = new VariableExpression(name);
            assertEquals(name, v.getName());
        }

        @Test
        void keepsNameExactlyAsGiven() {
            assertEquals(" x ", new VariableExpression(" x ").getName());
        }
    }

    @Nested
    class Evaluate {

        @Test
        void returnsValueFromMap() {
            VariableExpression x = new VariableExpression("x");
            assertEquals(5.0, x.evaluate(Map.of("x", 5.0)));
        }

        @Test
        void picksOwnVariableAmongSeveral() {
            Map<String, Double> vars = Map.of("x", 1.0, "y", 2.0, "z", 3.0);
            assertEquals(1.0, new VariableExpression("x").evaluate(vars));
            assertEquals(2.0, new VariableExpression("y").evaluate(vars));
            assertEquals(3.0, new VariableExpression("z").evaluate(vars));
        }

        @Test
        void handlesNegativeAndZeroValues() {
            VariableExpression x = new VariableExpression("x");
            assertEquals(-7.5, x.evaluate(Map.of("x", -7.5)));
            assertEquals(0.0, x.evaluate(Map.of("x", 0.0)));
        }

        @Test
        void handlesExtremeValues() {
            VariableExpression x = new VariableExpression("x");
            assertEquals(Double.MAX_VALUE, x.evaluate(Map.of("x", Double.MAX_VALUE)));
            assertEquals(Double.MIN_VALUE, x.evaluate(Map.of("x", Double.MIN_VALUE)));
        }

        @Test
        void passesThroughNaNAndInfinityWithoutChange() {
            VariableExpression x = new VariableExpression("x");
            assertTrue(Double.isNaN(x.evaluate(Map.of("x", Double.NaN))));
            assertEquals(Double.POSITIVE_INFINITY, x.evaluate(Map.of("x", Double.POSITIVE_INFINITY)));
            assertEquals(Double.NEGATIVE_INFINITY, x.evaluate(Map.of("x", Double.NEGATIVE_INFINITY)));
        }

        @Test
        void preservesNegativeZero() {
            VariableExpression x = new VariableExpression("x");
            double result = x.evaluate(Map.of("x", -0.0));
            assertEquals(Double.doubleToLongBits(-0.0), Double.doubleToLongBits(result));
        }

        @Test
        void throwsWhenMapIsNull() {
            VariableExpression x = new VariableExpression("x");
            assertThrows(IllegalArgumentException.class, () -> x.evaluate(null));
        }

        @Test
        void throwsWhenVariableIsMissing() {
            VariableExpression x = new VariableExpression("x");
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> x.evaluate(Map.of("y", 1.0)));
            assertTrue(ex.getMessage().contains("x"),
                    "Повідомлення має містити ім'я змінної, а воно: " + ex.getMessage());
        }

        @Test
        void throwsOnEmptyMap() {
            VariableExpression x = new VariableExpression("x");
            assertThrows(IllegalArgumentException.class, () -> x.evaluate(Map.of()));
        }

        @Test
        void throwsWhenValueIsExplicitlyNull() {
            Map<String, Double> vars = new HashMap<>();
            vars.put("x", null);
            VariableExpression x = new VariableExpression("x");
            assertThrows(IllegalArgumentException.class, () -> x.evaluate(vars));
        }

        @Test
        void nameLookupIsCaseSensitive() {
            VariableExpression lower = new VariableExpression("x");
            assertThrows(IllegalArgumentException.class, () -> lower.evaluate(Map.of("X", 1.0)));
        }

        @Test
        void doesNotModifyTheMap() {
            Map<String, Double> vars = new HashMap<>();
            vars.put("x", 4.0);
            new VariableExpression("x").evaluate(vars);
            assertEquals(Map.of("x", 4.0), vars);
        }

        @Test
        void reflectsLatestMapContentsOnEachCall() {
            VariableExpression x = new VariableExpression("x");
            Map<String, Double> vars = new HashMap<>();
            vars.put("x", 1.0);
            assertEquals(1.0, x.evaluate(vars));
            vars.put("x", 2.0);
            assertEquals(2.0, x.evaluate(vars));
        }
    }

    @Nested
    class ToStringTests {

        @ParameterizedTest
        @ValueSource(strings = {"x", "y", "z", "alpha", "x1"})
        void returnsName(String name) {
            assertEquals(name, new VariableExpression(name).toString());
        }
    }

    @Nested
    class EqualsAndHashCode {

        @Test
        void equalForSameName() {
            assertEquals(new VariableExpression("x"), new VariableExpression("x"));
        }

        @Test
        void notEqualForDifferentNames() {
            assertNotEquals(new VariableExpression("x"), new VariableExpression("y"));
        }

        @Test
        void caseSensitive() {
            assertNotEquals(new VariableExpression("x"), new VariableExpression("X"));
        }

        @Test
        void reflexive() {
            VariableExpression x = new VariableExpression("x");
            assertEquals(x, x);
        }

        @Test
        void symmetric() {
            VariableExpression a = new VariableExpression("x");
            VariableExpression b = new VariableExpression("x");
            assertEquals(a, b);
            assertEquals(b, a);
        }

        @Test
        void transitive() {
            VariableExpression a = new VariableExpression("x");
            VariableExpression b = new VariableExpression("x");
            VariableExpression c = new VariableExpression("x");
            assertEquals(a, b);
            assertEquals(b, c);
            assertEquals(a, c);
        }

        @Test
        void notEqualToNull() {
            assertNotEquals(null, new VariableExpression("x"));
        }

        @Test
        void notEqualToObjectOfAnotherType() {
            assertNotEquals("x", new VariableExpression("x"));
            assertNotEquals(new VariableExpression("x"), "x");
        }

        @Test
        void equalObjectsHaveEqualHashCodes() {
            assertEquals(new VariableExpression("x").hashCode(), new VariableExpression("x").hashCode());
        }

        @Test
        void worksInHashSet() {
            Set<VariableExpression> set = new HashSet<>();
            set.add(new VariableExpression("x"));
            set.add(new VariableExpression("x"));
            set.add(new VariableExpression("y"));
            assertEquals(2, set.size());
            assertTrue(set.contains(new VariableExpression("y")));
        }

        @Test
        void worksAsMapKey() {
            Map<VariableExpression, String> map = new HashMap<>();
            map.put(new VariableExpression("x"), "first");
            map.put(new VariableExpression("x"), "second");
            assertEquals(1, map.size());
            assertEquals("second", map.get(new VariableExpression("x")));
        }
    }
}