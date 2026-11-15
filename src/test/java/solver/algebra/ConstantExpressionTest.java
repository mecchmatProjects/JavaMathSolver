package solver.algebra;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ConstantExpressionTest {


    @Test
    @DisplayName("getValue повертає значення з конструктора")
    void getValue() {
        assertEquals(3.5, new ConstantExpression(3.5).getValue());
        assertEquals(-7.25, new ConstantExpression(-7.25).getValue());
        assertEquals(0.0, new ConstantExpression(0).getValue());
    }

    @Test
    @DisplayName("getValue зберігає -0.0, NaN та Infinity без змін")
    void getValueSpecialValues() {
        assertEquals(-0.0, new ConstantExpression(-0.0).getValue());
        assertEquals(Double.POSITIVE_INFINITY, new ConstantExpression(Double.POSITIVE_INFINITY).getValue());
        assertTrue(Double.isNaN(new ConstantExpression(Double.NaN).getValue()));
    }

    @Test
    @DisplayName("ConstantExpression є Expression")
    void isExpression() {
        assertInstanceOf(Expression.class, new ConstantExpression(1));
    }


    @Test
    @DisplayName("evaluate повертає значення константи")
    void evaluate() {
        assertEquals(5.0, new ConstantExpression(5).evaluate(Map.of()));
        assertEquals(-2.5, new ConstantExpression(-2.5).evaluate(Map.of()));
        assertEquals(0.0, new ConstantExpression(0).evaluate(Map.of()));
    }

    @Test
    @DisplayName("evaluate ігнорує змінні: порожня мапа, мапа зі змінними та null дають той самий результат")
    void evaluateIgnoresVariables() {
        ConstantExpression c = new ConstantExpression(42);
        assertEquals(42.0, c.evaluate(Map.of()));
        assertEquals(42.0, c.evaluate(Map.of("x", 99.0, "y", -1.0)));
        assertEquals(42.0, c.evaluate(new HashMap<>(Map.of("x", 1.0))));
        assertEquals(42.0, c.evaluate((Map<String, Double>) null));
    }

    @Test
    @DisplayName("evaluate: Infinity і NaN передаються як є")
    void evaluateSpecialValues() {
        assertEquals(Double.NEGATIVE_INFINITY,
                new ConstantExpression(Double.NEGATIVE_INFINITY).evaluate(Map.of()));
        assertTrue(Double.isNaN(new ConstantExpression(Double.NaN).evaluate(Map.of())));
    }

    @Test
    @DisplayName("evaluate: повторні виклики дають той самий результат")
    void evaluateRepeatable() {
        ConstantExpression c = new ConstantExpression(1.5);
        assertEquals(c.evaluate(Map.of()), c.evaluate(Map.of()));
    }


    @ParameterizedTest(name = "[{index}] {0} → \"{1}\"")
    @CsvSource({
            "5.0, 5",
            "-3.0, -3",
            "0.0, 0",
            "100.0, 100",
            "123456789.0, 123456789",
            "1.0E10, 10000000000",
            "1.0E18, 1000000000000000000",
            "-9.223372036854775808E18, -9223372036854775808"
    })
    @DisplayName("toString: ціле значення друкується без дробової частини")
    void toStringIntegers(double value, String expected) {
        assertEquals(expected, new ConstantExpression(value).toString());
    }

    @Test
    @DisplayName("toString: -0.0 друкується як \"0\"")
    void toStringNegativeZero() {
        assertEquals("0", new ConstantExpression(-0.0).toString());
    }


    @ParameterizedTest(name = "[{index}] {0} → \"{1}\"")
    @CsvSource({
            "2.5, 2.5",
            "-1.5, -1.5",
            "0.1, 0.1",
            "3.14159, 3.14159",
            "0.001, 0.001"
    })
    @DisplayName("toString: дробове значення друкується як Double.toString")
    void toStringFractions(double value, String expected) {
        assertEquals(expected, new ConstantExpression(value).toString());
    }

    @ParameterizedTest(name = "[{index}] {0} → \"{1}\"")
    @CsvSource({
            "1.0E-5, 1.0E-5",
            "1.23456785E7, 1.23456785E7",
            "1.0E19, 1.0E19",
            "1.7976931348623157E308, 1.7976931348623157E308",
            "4.9E-324, 4.9E-324"
    })
    @DisplayName("toString: дуже малі/великі значення (поза діапазоном long або з дробом) у науковій нотації")
    void toStringScientific(double value, String expected) {
        assertEquals(expected, new ConstantExpression(value).toString());
    }

    @Test
    @DisplayName("toString: NaN та ±Infinity")
    void toStringNonFinite() {
        assertEquals("NaN", new ConstantExpression(Double.NaN).toString());
        assertEquals("Infinity", new ConstantExpression(Double.POSITIVE_INFINITY).toString());
        assertEquals("-Infinity", new ConstantExpression(Double.NEGATIVE_INFINITY).toString());
    }

    @Test
    @DisplayName("toString: 5 і 5.0 дають однаковий рядок")
    void toStringSameForSameValue() {
        assertEquals(new ConstantExpression(5).toString(), new ConstantExpression(5.0).toString());
    }


    @Test
    @DisplayName("equals: рефлексивність")
    void equalsReflexive() {
        ConstantExpression c = new ConstantExpression(3);
        assertEquals(c, c);
    }

    @Test
    @DisplayName("equals: однакові значення з різних екземплярів рівні, симетрично")
    void equalsSameValue() {
        ConstantExpression c1 = new ConstantExpression(2.5);
        ConstantExpression c2 = new ConstantExpression(2.5);
        assertNotSame(c1, c2);
        assertEquals(c1, c2);
        assertEquals(c2, c1);
    }

    @Test
    @DisplayName("equals: різні значення не рівні")
    void equalsDifferentValue() {
        assertNotEquals(new ConstantExpression(1), new ConstantExpression(2));
        assertNotEquals(new ConstantExpression(1), new ConstantExpression(-1));
    }

    @Test
    @DisplayName("equals: порівняння точне, 0.1 + 0.2 ≠ 0.3")
    void equalsIsExact() {
        assertNotEquals(new ConstantExpression(0.1 + 0.2), new ConstantExpression(0.3));
    }

    @Test
    @DisplayName("equals: 0.0 і -0.0 не рівні (Double.compare)")
    void equalsZeroAndNegativeZero() {
        assertNotEquals(new ConstantExpression(0.0), new ConstantExpression(-0.0));
    }

    @Test
    @DisplayName("equals: NaN дорівнює NaN (Double.compare)")
    void equalsNaN() {
        assertEquals(new ConstantExpression(Double.NaN), new ConstantExpression(Double.NaN));
    }

    @Test
    @DisplayName("equals: null та об'єкти інших типів не рівні")
    void equalsOtherTypes() {
        ConstantExpression c = new ConstantExpression(5);
        assertFalse(c.equals(null));
        assertFalse(c.equals(5.0));
        assertFalse(c.equals("5"));
    }


    @Test
    @DisplayName("hashCode: рівні об'єкти мають однаковий хеш")
    void hashCodeConsistentWithEquals() {
        assertEquals(new ConstantExpression(2.5).hashCode(), new ConstantExpression(2.5).hashCode());
        assertEquals(new ConstantExpression(Double.NaN).hashCode(),
                new ConstantExpression(Double.NaN).hashCode());
    }

    @Test
    @DisplayName("hashCode: стабільний між викликами")
    void hashCodeStable() {
        ConstantExpression c = new ConstantExpression(7);
        assertEquals(c.hashCode(), c.hashCode());
    }

    @Test
    @DisplayName("HashSet: однакові константи зливаються в одну, 0.0 і -0.0 лишаються різними")
    void worksInHashSet() {
        Set<ConstantExpression> set = new HashSet<>();
        set.add(new ConstantExpression(1));
        set.add(new ConstantExpression(1));
        set.add(new ConstantExpression(2));
        assertEquals(2, set.size());
        assertTrue(set.contains(new ConstantExpression(2)));
        assertFalse(set.contains(new ConstantExpression(3)));

        Set<ConstantExpression> zeros = new HashSet<>();
        zeros.add(new ConstantExpression(0.0));
        zeros.add(new ConstantExpression(-0.0));
        assertEquals(2, zeros.size());
    }

    @Test
    @DisplayName("HashMap: константа працює як ключ")
    void worksAsMapKey() {
        Map<ConstantExpression, String> map = new HashMap<>();
        map.put(new ConstantExpression(10), "ten");
        assertEquals("ten", map.get(new ConstantExpression(10)));
        assertNull(map.get(new ConstantExpression(11)));
    }
}