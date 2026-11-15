package solver.algebra;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ComplexNumberTest {

    private static final double EPS = 1e-9;

    private static final ComplexNumber ZERO = new ComplexNumber(0, 0);
    private static final ComplexNumber ONE = new ComplexNumber(1, 0);
    private static final ComplexNumber I = new ComplexNumber(0, 1);

    private static void assertComplex(double re, double im, ComplexNumber z) {
        assertAll(
                () -> assertEquals(re, z.getReal(), EPS, "дійсна частина"),
                () -> assertEquals(im, z.getImaginary(), EPS, "уявна частина")
        );
    }


    @Test
    @DisplayName("Геттери повертають значення з конструктора")
    void getters() {
        ComplexNumber z = new ComplexNumber(3, -4);
        assertEquals(3.0, z.getReal(), EPS);
        assertEquals(-4.0, z.getImaginary(), EPS);
    }


    @Test
    @DisplayName("add: (1 + 2i) + (3 + 4i) = 4 + 6i")
    void add() {
        assertComplex(4, 6, new ComplexNumber(1, 2).add(new ComplexNumber(3, 4)));
    }

    @Test
    @DisplayName("add: від'ємні частини, (-1 + 2i) + (1 - 5i) = 0 - 3i")
    void addNegative() {
        assertComplex(0, -3, new ComplexNumber(-1, 2).add(new ComplexNumber(1, -5)));
    }

    @Test
    @DisplayName("add: нуль є нейтральним елементом")
    void addZero() {
        assertComplex(2, -7, new ComplexNumber(2, -7).add(ZERO));
    }


    @Test
    @DisplayName("subtract: (5 + 3i) - (2 + 7i) = 3 - 4i")
    void subtract() {
        assertComplex(3, -4, new ComplexNumber(5, 3).subtract(new ComplexNumber(2, 7)));
    }

    @Test
    @DisplayName("subtract: z - z = 0")
    void subtractSelf() {
        ComplexNumber z = new ComplexNumber(3.5, -1.25);
        assertComplex(0, 0, z.subtract(z));
    }


    @Test
    @DisplayName("multiply: (1 + 2i)(3 + 4i) = 3 + 4i + 6i + 8i² = -5 + 10i")
    void multiply() {
        assertComplex(-5, 10, new ComplexNumber(1, 2).multiply(new ComplexNumber(3, 4)));
    }

    @Test
    @DisplayName("multiply: i · i = -1")
    void iSquared() {
        assertComplex(-1, 0, I.multiply(I));
    }

    @Test
    @DisplayName("multiply: (a + bi)(a - bi) = a² + b², тобто (3 + 4i)(3 - 4i) = 25")
    void multiplyByConjugate() {
        assertComplex(25, 0, new ComplexNumber(3, 4).multiply(new ComplexNumber(3, -4)));
    }

    @Test
    @DisplayName("multiply: на дійсне число, (2 + 3i) · 4 = 8 + 12i")
    void multiplyByReal() {
        assertComplex(8, 12, new ComplexNumber(2, 3).multiply(new ComplexNumber(4, 0)));
    }

    @Test
    @DisplayName("multiply: одиниця нейтральна, нуль занулює")
    void multiplyIdentityAndZero() {
        ComplexNumber z = new ComplexNumber(2, -5);
        assertComplex(2, -5, z.multiply(ONE));
        assertComplex(0, 0, z.multiply(ZERO));
    }


    @Test
    @DisplayName("abs: |3 + 4i| = 5, |5 + 12i| = 13")
    void absPythagoreanTriples() {
        assertEquals(5.0, new ComplexNumber(3, 4).abs(), EPS);
        assertEquals(13.0, new ComplexNumber(5, 12).abs(), EPS);
    }

    @Test
    @DisplayName("abs: знаки частин не впливають, |-3 - 4i| = 5")
    void absNegative() {
        assertEquals(5.0, new ComplexNumber(-3, -4).abs(), EPS);
    }

    @Test
    @DisplayName("abs: |0| = 0, |i| = 1, |-7| = 7")
    void absSpecialValues() {
        assertEquals(0.0, ZERO.abs(), EPS);
        assertEquals(1.0, I.abs(), EPS);
        assertEquals(7.0, new ComplexNumber(-7, 0).abs(), EPS);
    }


    @Test
    @DisplayName("divide: (1 + 2i)/(3 + 4i) = (11 + 2i)/25 = 0.44 + 0.08i")
    void divide() {
        assertComplex(0.44, 0.08, new ComplexNumber(1, 2).divide(new ComplexNumber(3, 4)));
    }

    @Test
    @DisplayName("divide: (-5 + 10i)/(3 + 4i) = 1 + 2i (обернена до множення)")
    void divideInverseOfMultiply() {
        assertComplex(1, 2, new ComplexNumber(-5, 10).divide(new ComplexNumber(3, 4)));
    }

    @Test
    @DisplayName("divide: на дійсне число, (6 + 8i)/2 = 3 + 4i")
    void divideByReal() {
        assertComplex(3, 4, new ComplexNumber(6, 8).divide(new ComplexNumber(2, 0)));
    }

    @Test
    @DisplayName("divide: 1/i = -i")
    void divideOneByI() {
        assertComplex(0, -1, ONE.divide(I));
    }

    @Test
    @DisplayName("divide: z/z = 1")
    void divideBySelf() {
        ComplexNumber z = new ComplexNumber(3, 4);
        assertComplex(1, 0, z.divide(z));
    }

    @Test
    @DisplayName("divide: (-1 - i)/(1 - i) = -i")
    void divideNegativeParts() {
        assertComplex(0, -1, new ComplexNumber(-1, -1).divide(new ComplexNumber(1, -1)));
    }

    @Test
    @DisplayName("divide: нуль у чисельнику, 0/(3 + 4i) = 0")
    void divideZeroNumerator() {
        assertComplex(0, 0, ZERO.divide(new ComplexNumber(3, 4)));
    }

    @Test
    @DisplayName("divide: на нуль → ArithmeticException")
    void divideByZero() {
        assertThrows(ArithmeticException.class, () -> new ComplexNumber(1, 2).divide(ZERO));
        assertThrows(ArithmeticException.class, () -> ZERO.divide(ZERO));
        assertThrows(ArithmeticException.class,
                () -> new ComplexNumber(1, 2).divide(new ComplexNumber(-0.0, -0.0)));
    }


    @Test
    @DisplayName("Комутативність додавання та множення")
    void commutativity() {
        ComplexNumber z = new ComplexNumber(1.5, -2);
        ComplexNumber w = new ComplexNumber(-3, 4.5);

        ComplexNumber sum1 = z.add(w);
        ComplexNumber sum2 = w.add(z);
        assertComplex(sum1.getReal(), sum1.getImaginary(), sum2);

        ComplexNumber prod1 = z.multiply(w);
        ComplexNumber prod2 = w.multiply(z);
        assertComplex(prod1.getReal(), prod1.getImaginary(), prod2);
    }

    @Test
    @DisplayName("Дистрибутивність: z(w + u) = zw + zu")
    void distributivity() {
        ComplexNumber z = new ComplexNumber(1, 2);
        ComplexNumber w = new ComplexNumber(3, 4);
        ComplexNumber u = new ComplexNumber(5, -6);

        ComplexNumber left = z.multiply(w.add(u));      // (1+2i)(8-2i) = 12 + 14i
        ComplexNumber right = z.multiply(w).add(z.multiply(u));

        assertComplex(12, 14, left);
        assertComplex(left.getReal(), left.getImaginary(), right);
    }

    @Test
    @DisplayName("(z + w) - w = z")
    void addThenSubtract() {
        ComplexNumber z = new ComplexNumber(2.5, -1.5);
        ComplexNumber w = new ComplexNumber(-7, 3);
        assertComplex(2.5, -1.5, z.add(w).subtract(w));
    }

    @Test
    @DisplayName("(z · w) / w = z")
    void multiplyThenDivide() {
        ComplexNumber z = new ComplexNumber(2.5, -1.5);
        ComplexNumber w = new ComplexNumber(-7, 3);
        assertComplex(2.5, -1.5, z.multiply(w).divide(w));
    }

    @Test
    @DisplayName("Модуль добутку: |z · w| = |z| · |w|")
    void absOfProduct() {
        ComplexNumber z = new ComplexNumber(1, 2);
        ComplexNumber w = new ComplexNumber(3, 4);
        assertEquals(z.abs() * w.abs(), z.multiply(w).abs(), EPS);
    }

    @Test
    @DisplayName("Модуль частки: |z / w| = |z| / |w|")
    void absOfQuotient() {
        ComplexNumber z = new ComplexNumber(1, 2);
        ComplexNumber w = new ComplexNumber(3, 4);
        assertEquals(z.abs() / w.abs(), z.divide(w).abs(), EPS);
    }


    @Test
    @DisplayName("Операції повертають нові об'єкти й не змінюють операнди")
    void immutability() {
        ComplexNumber z = new ComplexNumber(1, 2);
        ComplexNumber w = new ComplexNumber(3, 4);

        ComplexNumber[] results = {z.add(w), z.subtract(w), z.multiply(w), z.divide(w)};
        for (ComplexNumber r : results) {
            assertNotSame(z, r);
            assertNotSame(w, r);
        }

        assertComplex(1, 2, z);
        assertComplex(3, 4, w);
    }

    @Test
    @DisplayName("null-аргумент → NullPointerException (фіксує поточну поведінку)")
    void nullArgument() {
        ComplexNumber z = new ComplexNumber(1, 2);
        assertThrows(NullPointerException.class, () -> z.add(null));
        assertThrows(NullPointerException.class, () -> z.subtract(null));
        assertThrows(NullPointerException.class, () -> z.multiply(null));
        assertThrows(NullPointerException.class, () -> z.divide(null));
    }


    @Test
    @DisplayName("abs: дуже великі та дуже малі значення без переповнення/втрати порядку")
    void absExtremeMagnitudes() {
        // Тут abs() ймовірно впаде. Причина: real*real + imaginary*imaginary
        // переповнюється (1e200² = ∞) або занулюється (1e-200² = 0).
        // Виправлення: return Math.hypot(real, imaginary);
        double big = Math.sqrt(2) * 1e200;
        double small = Math.sqrt(2) * 1e-200;
        assertAll(
                () -> assertEquals(big, new ComplexNumber(1e200, 1e200).abs(), big * 1e-12),
                () -> assertEquals(small, new ComplexNumber(1e-200, 1e-200).abs(), small * 1e-12)
        );
    }

    @Test
    @DisplayName("NaN поширюється через операції")
    void nanPropagates() {
        ComplexNumber nan = new ComplexNumber(Double.NaN, 0);
        ComplexNumber z = new ComplexNumber(1, 2);
        assertTrue(Double.isNaN(z.add(nan).getReal()));
        assertTrue(Double.isNaN(z.multiply(nan).getReal()));
        assertTrue(Double.isNaN(nan.abs()));
    }
}