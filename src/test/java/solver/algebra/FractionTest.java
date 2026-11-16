package solver.algebra;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FractionTest {
    @Test
    void testAdd() {
        Fraction a = new Fraction(1, 2);
        Fraction b = new Fraction(1, 3);

        assertEquals(new Fraction(5, 6), a.add(b));
    }

    @Test
    void testSubtract() {
        Fraction a = new Fraction(1, 2);
        Fraction b = new Fraction(1, 3);

        assertEquals(new Fraction(1, 6), a.subtract(b));
    }

    @Test
    void testMultiply() {
        Fraction a = new Fraction(1, 2);
        Fraction b = new Fraction(1, 3);

        assertEquals(new Fraction(1, 6), a.multiply(b));
    }

    @Test
    void testDivide() {
        Fraction a = new Fraction(1, 2);
        Fraction b = new Fraction(1, 3);

        assertEquals(new Fraction(3, 2), a.divide(b));
    }

    @Test
    void testFractionReduction() {
        Fraction fraction = new Fraction(2, 4);

        assertEquals(new Fraction(1, 2), fraction);
    }

    @Test
    void testZeroDenominator() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Fraction(1, 0)
        );
    }

    @Test
    void testDivisionByZero() {
        Fraction a = new Fraction(1, 2);
        Fraction zero = new Fraction(0, 1);

        assertThrows(
                ArithmeticException.class,
                () -> a.divide(zero)
        );
    }

    @Test
    void testEquals() {
        Fraction a = new Fraction(1, 2);
        Fraction b = new Fraction(2, 4);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testCompareTo() {
        Fraction a = new Fraction(1, 3);
        Fraction b = new Fraction(1, 2);

        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(a) > 0);
        assertEquals(0, a.compareTo(new Fraction(2, 6)));
    }
}
