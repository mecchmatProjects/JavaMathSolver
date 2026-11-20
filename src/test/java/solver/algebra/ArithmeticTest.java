package solver.algebra;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ArithmeticTest {

    private static final double EPS = 1e-9;

    //add

    @Test
    @DisplayName("add: звичайні числа")
    void addBasic() {
        assertEquals(5.0, Arithmetic.add(2, 3), EPS);
        assertEquals(0.3, Arithmetic.add(0.1, 0.2), EPS); // похибка double
    }

    @Test
    @DisplayName("add: від'ємні числа та нуль")
    void addNegativeAndZero() {
        assertEquals(-1.0, Arithmetic.add(2, -3), EPS);
        assertEquals(-5.0, Arithmetic.add(-2, -3), EPS);
        assertEquals(7.0, Arithmetic.add(7, 0), EPS);
    }

    @Test
    @DisplayName("add: комутативність")
    void addCommutative() {
        assertEquals(Arithmetic.add(1.5, 4.2), Arithmetic.add(4.2, 1.5), EPS);
    }

    @Test
    @DisplayName("add: переповнення та спецзначення")
    void addSpecial() {
        assertEquals(Double.POSITIVE_INFINITY, Arithmetic.add(Double.MAX_VALUE, Double.MAX_VALUE));
        assertTrue(Double.isNaN(Arithmetic.add(Double.NaN, 1)));
        assertTrue(Double.isNaN(Arithmetic.add(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY)));
    }

    //sub

    @Test
    @DisplayName("sub: звичайні числа")
    void subBasic() {
        assertEquals(-1.0, Arithmetic.sub(2, 3), EPS);
        assertEquals(5.0, Arithmetic.sub(2, -3), EPS);
        assertEquals(0.0, Arithmetic.sub(4.5, 4.5), EPS);
    }

    @Test
    @DisplayName("sub: не комутативна")
    void subNotCommutative() {
        assertNotEquals(Arithmetic.sub(5, 2), Arithmetic.sub(2, 5));
    }

    @Test
    @DisplayName("sub: спецзначення")
    void subSpecial() {
        assertTrue(Double.isNaN(Arithmetic.sub(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)));
        assertTrue(Double.isNaN(Arithmetic.sub(Double.NaN, 1)));
    }

    //mul
    @Test
    @DisplayName("mul: звичайні числа")
    void mulBasic() {
        assertEquals(6.0, Arithmetic.mul(2, 3), EPS);
        assertEquals(-6.0, Arithmetic.mul(2, -3), EPS);
        assertEquals(6.0, Arithmetic.mul(-2, -3), EPS);
        assertEquals(0.25, Arithmetic.mul(0.5, 0.5), EPS);
    }

    @Test
    @DisplayName("mul: нуль та одиниця")
    void mulZeroAndOne() {
        assertEquals(0.0, Arithmetic.mul(123.456, 0), EPS);
        assertEquals(42.0, Arithmetic.mul(42, 1), EPS);
    }

    @Test
    @DisplayName("mul: спецзначення")
    void mulSpecial() {
        assertEquals(Double.POSITIVE_INFINITY, Arithmetic.mul(Double.MAX_VALUE, 2));
        assertTrue(Double.isNaN(Arithmetic.mul(Double.POSITIVE_INFINITY, 0)));
    }

    //div

    @Test
    @DisplayName("div: звичайні числа")
    void divBasic() {
        assertEquals(2.0, Arithmetic.div(6, 3), EPS);
        assertEquals(0.5, Arithmetic.div(1, 2), EPS);
        assertEquals(-2.0, Arithmetic.div(6, -3), EPS);
        assertEquals(2.0, Arithmetic.div(-6, -3), EPS);
    }

    @Test
    @DisplayName("div: ділення на нуль дає ±Infinity (double, не виняток)")
    void divByZero() {
        assertEquals(Double.POSITIVE_INFINITY, Arithmetic.div(1, 0));
        assertEquals(Double.NEGATIVE_INFINITY, Arithmetic.div(-1, 0));
        assertEquals(Double.NEGATIVE_INFINITY, Arithmetic.div(1, -0.0));
    }

    @Test
    @DisplayName("div: 0/0 дає NaN")
    void divZeroByZero() {
        assertTrue(Double.isNaN(Arithmetic.div(0, 0)));
    }

    @Test
    @DisplayName("div: нуль у чисельнику")
    void divZeroNumerator() {
        assertEquals(0.0, Arithmetic.div(0, 5), EPS);
    }

    //pow

    @Test
    @DisplayName("pow: цілі степені")
    void powInteger() {
        assertEquals(8.0, Arithmetic.pow(2, 3), EPS);
        assertEquals(1.0, Arithmetic.pow(5, 0), EPS);
        assertEquals(5.0, Arithmetic.pow(5, 1), EPS);
        assertEquals(-8.0, Arithmetic.pow(-2, 3), EPS);
        assertEquals(4.0, Arithmetic.pow(-2, 2), EPS);
    }

    @Test
    @DisplayName("pow: від'ємний та дробовий показник")
    void powNegativeAndFractional() {
        assertEquals(0.125, Arithmetic.pow(2, -3), EPS);
        assertEquals(3.0, Arithmetic.pow(9, 0.5), EPS);
        assertEquals(2.0, Arithmetic.pow(8, 1.0 / 3.0), 1e-9);
    }

    @Test
    @DisplayName("pow: особливі випадки")
    void powSpecial() {
        assertEquals(1.0, Arithmetic.pow(0, 0), EPS);
        assertEquals(Double.POSITIVE_INFINITY, Arithmetic.pow(0, -1));
        assertTrue(Double.isNaN(Arithmetic.pow(-8, 1.0 / 3.0))); // від'ємна основа, дробовий степінь
    }

    //abs

    @Test
    @DisplayName("abs: додатні, від'ємні, нуль")
    void absBasic() {
        assertEquals(5.0, Arithmetic.abs(5), EPS);
        assertEquals(5.0, Arithmetic.abs(-5), EPS);
        assertEquals(0.0, Arithmetic.abs(0), EPS);
        assertEquals(3.14, Arithmetic.abs(-3.14), EPS);
    }

    @Test
    @DisplayName("abs: -0.0 та спецзначення")
    void absSpecial() {
        assertEquals(0.0, Arithmetic.abs(-0.0));
        assertEquals(Double.POSITIVE_INFINITY, Arithmetic.abs(Double.NEGATIVE_INFINITY));
        assertTrue(Double.isNaN(Arithmetic.abs(Double.NaN)));
    }

    //sqrt

    @Test
    @DisplayName("sqrt: повні квадрати та звичайні числа")
    void sqrtBasic() {
        assertEquals(0.0, Arithmetic.sqrt(0), EPS);
        assertEquals(1.0, Arithmetic.sqrt(1), EPS);
        assertEquals(4.0, Arithmetic.sqrt(16), EPS);
        assertEquals(Math.sqrt(2), Arithmetic.sqrt(2), EPS);
        assertEquals(0.5, Arithmetic.sqrt(0.25), EPS);
    }

    @Test
    @DisplayName("sqrt: від'ємне число дає NaN")
    void sqrtNegative() {
        assertTrue(Double.isNaN(Arithmetic.sqrt(-1)));
    }

    @Test
    @DisplayName("sqrt: Infinity")
    void sqrtInfinity() {
        assertEquals(Double.POSITIVE_INFINITY, Arithmetic.sqrt(Double.POSITIVE_INFINITY));
    }

    //комбіновані властивості

    @Test
    @DisplayName("sqrt(x)^2 ≈ x, pow(x, 0.5) == sqrt(x)")
    void sqrtPowConsistency() {
        double x = 17.0;
        assertEquals(x, Arithmetic.pow(Arithmetic.sqrt(x), 2), 1e-9);
        assertEquals(Arithmetic.sqrt(x), Arithmetic.pow(x, 0.5), 1e-9);
    }

    @Test
    @DisplayName("(a + b) - b ≈ a, (a * b) / b ≈ a")
    void inverseOperations() {
        double a = 12.34, b = 5.67;
        assertEquals(a, Arithmetic.sub(Arithmetic.add(a, b), b), 1e-9);
        assertEquals(a, Arithmetic.div(Arithmetic.mul(a, b), b), 1e-9);
    }

    @Test
    @DisplayName("Утиліт-клас: приватний конструктор")
    void privateConstructor() throws Exception {
        var ctor = Arithmetic.class.getDeclaredConstructor();
        assertTrue(java.lang.reflect.Modifier.isPrivate(ctor.getModifiers()));
    }
}