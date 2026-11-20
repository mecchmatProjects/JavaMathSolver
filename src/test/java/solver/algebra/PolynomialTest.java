package solver.algebra;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;


class PolynomialTest {

    private static final double EPS = 1e-13;

    private static Polynomial poly(double... coefficients) {
        return new Polynomial(coefficients);
    }

    private static void assertCoefficients(double[] expected, Polynomial actual) {
        assertArrayEquals(expected, actual.getCoefficients(), EPS);
    }


    @Nested
    @DisplayName("Конструктор")
    class ConstructorTests {

        @Test
        @DisplayName("null та порожній масив → IllegalArgumentException")
        void nullAndEmpty() {
            assertThrows(IllegalArgumentException.class, () -> new Polynomial(null));
            assertThrows(IllegalArgumentException.class, () -> new Polynomial(new double[0]));
        }

        @Test
        @DisplayName("NaN та ±Infinity у коефіцієнтах → IllegalArgumentException")
        void nonFiniteCoefficients() {
            assertThrows(IllegalArgumentException.class, () -> poly(1, Double.NaN));
            assertThrows(IllegalArgumentException.class, () -> poly(Double.POSITIVE_INFINITY));
            assertThrows(IllegalArgumentException.class, () -> poly(1, 2, Double.NEGATIVE_INFINITY));
        }

        @Test
        @DisplayName("Старші нулі відкидаються: {1, 2, 0, 0} → 1 + 2x")
        void trailingZerosTrimmed() {
            Polynomial p = poly(1, 2, 0, 0);
            assertCoefficients(new double[]{1, 2}, p);
            assertEquals(1, p.degree());
        }

        @Test
        @DisplayName("Усі нулі → нульовий поліном {0} степеня 0")
        void allZeros() {
            Polynomial p = poly(0, 0, 0);
            assertCoefficients(new double[]{0}, p);
            assertEquals(0, p.degree());
            assertTrue(p.isZero());
        }

        @Test
        @DisplayName("Нулі всередині та на початку зберігаються: {0, 0, 3}")
        void leadingZerosKept() {
            Polynomial p = poly(0, 0, 3);
            assertCoefficients(new double[]{0, 0, 3}, p);
            assertEquals(2, p.degree());
        }

        @Test
        @DisplayName("Малі ненульові коефіцієнти зберігаються без обнулення")
        void smallNonZeroCoefficientsPreserved() {
            assertEquals(2, poly(-1e13, 0, 1).degree());
            assertArrayEquals(new double[]{-Math.sqrt(1e13), Math.sqrt(1e13)}, poly(-1e13, 0, 1).roots(), 1e-6);
            assertEquals(2, poly(1, 2, 1e-13).degree());
            assertEquals(1e-13, poly(1e-13, 2).getCoefficient(0));
        }

        @Test
        @DisplayName("Межа: 1e-12 ще не вважається нулем (строге порівняння)")
        void thresholdIsStrict() {
            Polynomial p = poly(0, 1e-12);
            assertEquals(1, p.degree());
            assertEquals(1e-12, p.getCoefficient(1));
        }

        @Test
        @DisplayName("-0.0 нормалізується до 0.0")
        void negativeZeroNormalized() {
            assertEquals(0.0, poly(-0.0, 1).getCoefficient(0));
        }

        @Test
        @DisplayName("Конструктор копіює масив: зміна вхідного масиву не впливає на поліном")
        void defensiveCopyOfInput() {
            double[] input = {1, 2, 3};
            Polynomial p = new Polynomial(input);
            input[0] = 99;
            assertEquals(1.0, p.getCoefficient(0), EPS);
        }

        @Test
        @DisplayName("Від'ємний старший коефіцієнт зберігається")
        void negativeLeading() {
            assertCoefficients(new double[]{1, -5}, poly(1, -5));
        }
    }


    @Nested
    @DisplayName("Доступ до коефіцієнтів")
    class AccessorTests {

        @Test
        @DisplayName("degree: довжина − 1")
        void degree() {
            assertEquals(0, poly(5).degree());
            assertEquals(1, poly(1, 2).degree());
            assertEquals(3, poly(1, 0, 0, 7).degree());
            assertEquals(0, poly(0).degree());
        }

        @Test
        @DisplayName("isZero: true для нульового полінома, false для малих ненульових коефіцієнтів")
        void isZeroTrue() {
            assertTrue(poly(0).isZero());
            assertTrue(poly(0, 0, 0).isZero());
            assertFalse(poly(1e-13).isZero());
        }

        @Test
        @DisplayName("isZero: ненульові поліноми")
        void isZeroFalse() {
            assertFalse(poly(5).isZero());
            assertFalse(poly(0, 1).isZero());
            assertFalse(poly(-1).isZero());
        }

        @Test
        @DisplayName("getCoefficient: індекс дорівнює степеню")
        void getCoefficient() {
            Polynomial p = poly(1, 2, 3);
            assertEquals(1.0, p.getCoefficient(0), EPS);
            assertEquals(2.0, p.getCoefficient(1), EPS);
            assertEquals(3.0, p.getCoefficient(2), EPS);
        }

        @Test
        @DisplayName("getCoefficient: степінь вищий за степінь полінома → 0")
        void getCoefficientBeyondDegree() {
            Polynomial p = poly(1, 2, 3);
            assertEquals(0.0, p.getCoefficient(3), EPS);
            assertEquals(0.0, p.getCoefficient(1000), EPS);
        }

        @Test
        @DisplayName("getCoefficient: від'ємний степінь → IndexOutOfBoundsException")
        void getCoefficientNegative() {
            assertThrows(IndexOutOfBoundsException.class, () -> poly(1, 2).getCoefficient(-1));
        }

        @Test
        @DisplayName("getCoefficients повертає копію: зміна копії не впливає на поліном")
        void getCoefficientsReturnsCopy() {
            Polynomial p = poly(1, 2, 3);
            double[] copy = p.getCoefficients();
            copy[0] = 99;
            assertEquals(1.0, p.getCoefficient(0), EPS);
            assertNotSame(p.getCoefficients(), p.getCoefficients());
        }
    }


    @Nested
    @DisplayName("evaluate (схема Горнера)")
    class EvaluateTests {

        @Test
        @DisplayName("3x² + 2x + 1: P(2) = 17, P(0) = 1, P(-1) = 2, P(0.5) = 2.75")
        void quadratic() {
            Polynomial p = poly(1, 2, 3);
            assertEquals(17.0, p.evaluate(2), EPS);
            assertEquals(1.0, p.evaluate(0), EPS);
            assertEquals(2.0, p.evaluate(-1), EPS);
            assertEquals(2.75, p.evaluate(0.5), EPS);
        }

        @Test
        @DisplayName("x³ − 6x² + 11x − 6 = (x−1)(x−2)(x−3): корені 1, 2, 3 і P(4) = 6")
        void cubicWithKnownRoots() {
            Polynomial p = poly(-6, 11, -6, 1);
            assertEquals(0.0, p.evaluate(1), EPS);
            assertEquals(0.0, p.evaluate(2), EPS);
            assertEquals(0.0, p.evaluate(3), EPS);
            assertEquals(6.0, p.evaluate(4), EPS);
        }

        @Test
        @DisplayName("Константа та нульовий поліном")
        void constantAndZero() {
            assertEquals(5.0, poly(5).evaluate(100), EPS);
            assertEquals(0.0, poly(0).evaluate(7), EPS);
        }

        @Test
        @DisplayName("Результат 0 не буває від'ємним нулем: P(x) = -x при x = 0")
        void noNegativeZero() {
            assertEquals(0.0, poly(0, -1).evaluate(0));
        }

        @Test
        @DisplayName("NaN та ±Infinity як аргумент → IllegalArgumentException")
        void nonFiniteArgument() {
            Polynomial p = poly(1, 2);
            assertThrows(IllegalArgumentException.class, () -> p.evaluate(Double.NaN));
            assertThrows(IllegalArgumentException.class, () -> p.evaluate(Double.POSITIVE_INFINITY));
            assertThrows(IllegalArgumentException.class, () -> p.evaluate(Double.NEGATIVE_INFINITY));
        }
    }


    @Nested
    @DisplayName("equals / hashCode")
    class EqualityTests {

        @Test
        @DisplayName("equals(Object): однакові коефіцієнти → рівні, з однаковим hashCode")
        void equalPolynomials() {
            Polynomial p1 = poly(1, 2, 3);
            Polynomial p2 = poly(1, 2, 3);
            assertEquals(p1, p2);
            assertEquals(p2, p1);
            assertEquals(p1.hashCode(), p2.hashCode());
        }

        @Test
        @DisplayName("equals(Object): старші нулі не впливають: {1, 2, 0} = {1, 2}")
        void equalAfterTrimming() {
            assertEquals(poly(1, 2, 0), poly(1, 2));
            assertEquals(poly(1, 2, 0).hashCode(), poly(1, 2).hashCode());
        }

        @Test
        @DisplayName("equals(Object): різні коефіцієнти та різні степені → не рівні")
        void differentPolynomials() {
            assertNotEquals(poly(1, 2, 3), poly(1, 2, 4));
            assertNotEquals(poly(1, 2), poly(1, 2, 3));
        }

        @Test
        @DisplayName("equals(Object): рефлексивність, null та інший клас")
        void reflexiveNullAndOtherTypes() {
            Polynomial p = poly(1, 2);
            assertEquals(p, p);
            assertFalse(p.equals((Object) null));
            assertFalse(p.equals("1 + 2x"));
        }

        @Test
        @DisplayName("equals(Object): точне порівняння, 0.1 + 0.2 ≠ 0.3")
        void exactComparison() {
            assertNotEquals(poly(0.1 + 0.2), poly(0.3));
        }

        @Test
        @DisplayName("equals(Polynomial, eps): коефіцієнти в межах eps вважаються рівними")
        void equalsWithEpsilon() {
            Polynomial p = poly(1, 2);
            Polynomial q = poly(1.0000001, 2);
            assertTrue(p.equals(q, 1e-3));
            assertFalse(p.equals(q, 1e-9));
        }

        @Test
        @DisplayName("equals(Polynomial, eps): різні степені не рівні навіть при великому eps")
        void equalsWithEpsilonDifferentLength() {
            assertFalse(poly(1, 2).equals(poly(1, 2, 3), 100));
        }

        @Test
        @DisplayName("equals(Polynomial, eps): null → false, той самий об'єкт → true")
        void equalsWithEpsilonNullAndSelf() {
            Polynomial p = poly(1, 2);
            assertFalse(p.equals(null, 1e-6));
            assertTrue(p.equals(p, 1e-6));
        }

        @Test
        @DisplayName("HashSet: однакові поліноми зливаються в один")
        void hashSet() {
            Set<Polynomial> set = new HashSet<>();
            set.add(poly(1, 2));
            set.add(poly(1, 2, 0));
            set.add(poly(2, 1));
            assertEquals(2, set.size());
        }
    }


    @Nested
    @DisplayName("toString")
    class ToStringTests {

        @Test
        @DisplayName("Нульовий поліном → \"0\"")
        void zero() {
            assertEquals("0", poly(0).toString());
            assertEquals("0", poly(0, 0, 0).toString());
        }

        @Test
        @DisplayName("Константи: 5 → \"5\", -5 → \"-5\", 1 → \"1\", -1 → \"-1\"")
        void constants() {
            assertEquals("5", poly(5).toString());
            assertEquals("-5", poly(-5).toString());
            assertEquals("1", poly(1).toString());
            assertEquals("-1", poly(-1).toString());
        }

        @Test
        @DisplayName("Старші степені перед молодшими: 3x^2 + 2x + 1")
        void descendingOrder() {
            assertEquals("3x^2 + 2x + 1", poly(1, 2, 3).toString());
        }

        @Test
        @DisplayName("Коефіцієнт 1 при x не друкується: x, x^2, x^2 + x")
        void unitCoefficientOmitted() {
            assertEquals("x", poly(0, 1).toString());
            assertEquals("x^2", poly(0, 0, 1).toString());
            assertEquals("x^2 + x", poly(0, 1, 1).toString());
            assertEquals("x^3", poly(0, 0, 0, 1).toString());
        }

        @Test
        @DisplayName("Коефіцієнт -1 при x друкується як мінус: -x, -x^2, -x + 1")
        void minusOneCoefficient() {
            assertEquals("-x", poly(0, -1).toString());
            assertEquals("-x^2", poly(0, 0, -1).toString());
            assertEquals("-x + 1", poly(1, -1).toString());
        }

        @Test
        @DisplayName("Від'ємні доданки з \" - \": x^3 - 6x^2 + 11x - 6")
        void negativeTerms() {
            assertEquals("x^3 - 6x^2 + 11x - 6", poly(-6, 11, -6, 1).toString());
        }

        @Test
        @DisplayName("Нульові коефіцієнти пропускаються: 2x^3 + 1")
        void zeroTermsSkipped() {
            assertEquals("2x^3 + 1", poly(1, 0, 0, 2).toString());
        }

        @Test
        @DisplayName("Від'ємний старший коефіцієнт: -3x^2")
        void negativeLeading() {
            assertEquals("-3x^2", poly(0, 0, -3).toString());
            assertEquals("2x^2 - 1.5", poly(-1.5, 0, 2).toString());
        }

        @Test
        @DisplayName("Дробові коефіцієнти: 1.5x + 2.5, 0.5x")
        void fractionalCoefficients() {
            assertEquals("1.5x + 2.5", poly(2.5, 1.5).toString());
            assertEquals("0.5x", poly(0, 0.5).toString());
        }
    }

    @Nested
    @DisplayName("derivative")
    class DerivativeTests {

        @Test
        @DisplayName("(3x² + 2x + 1)' = 6x + 2")
        void quadratic() {
            assertCoefficients(new double[]{2, 6}, poly(1, 2, 3).derivative());
        }

        @Test
        @DisplayName("(x³ − 6x² + 11x − 6)' = 3x² − 12x + 11")
        void cubic() {
            assertCoefficients(new double[]{11, -12, 3}, poly(-6, 11, -6, 1).derivative());
        }

        @Test
        @DisplayName("Похідна лінійного полінома: (4x + 3)' = 4")
        void linear() {
            assertCoefficients(new double[]{4}, poly(3, 4).derivative());
        }

        @Test
        @DisplayName("Похідна константи та нуля дорівнює нулю")
        void constantAndZero() {
            assertTrue(poly(5).derivative().isZero());
            assertTrue(poly(0).derivative().isZero());
        }

        @Test
        @DisplayName("Степінь похідної на 1 менший: (x^5)' = 5x^4")
        void degreeDrops() {
            Polynomial d = poly(0, 0, 0, 0, 0, 1).derivative();
            assertEquals(4, d.degree());
            assertEquals(5.0, d.getCoefficient(4), EPS);
        }

        @Test
        @DisplayName("Правило суми: (p + q)' = p' + q'")
        void sumRule() {
            Polynomial p = poly(1, 2, 3);
            Polynomial q = poly(-4, 0, 5, 1);
            assertTrue(p.add(q).derivative().equals(p.derivative().add(q.derivative()), EPS));
        }

        @Test
        @DisplayName("Правило добутку: (p·q)' = p'·q + p·q'")
        void productRule() {
            Polynomial p = poly(1, 2);
            Polynomial q = poly(3, 0, 1);
            assertCoefficients(new double[]{6, 2, 6}, p.multiply(q).derivative());
            Polynomial rule = p.derivative().multiply(q).add(p.multiply(q.derivative()));
            assertTrue(p.multiply(q).derivative().equals(rule, EPS));
        }
    }

    @Nested
    @DisplayName("add / subtract / multiply")
    class ArithmeticTests {

        @Test
        @DisplayName("add: (1 + 2x) + (3 + 4x + 5x²) = 4 + 6x + 5x²")
        void add() {
            assertCoefficients(new double[]{4, 6, 5}, poly(1, 2).add(poly(3, 4, 5)));
        }

        @Test
        @DisplayName("add: порядок операндів не важливий, різні довжини")
        void addCommutative() {
            Polynomial p = poly(1, 2);
            Polynomial q = poly(3, 4, 5);
            assertEquals(p.add(q), q.add(p));
        }

        @Test
        @DisplayName("add: старші коефіцієнти скорочуються, степінь падає: (x² + 1) + (−x² + 2) = 3")
        void addCancelsLeading() {
            Polynomial sum = poly(1, 0, 1).add(poly(2, 0, -1));
            assertCoefficients(new double[]{3}, sum);
            assertEquals(0, sum.degree());
        }

        @Test
        @DisplayName("add: нуль нейтральний")
        void addZero() {
            Polynomial p = poly(1, 2, 3);
            assertEquals(p, p.add(poly(0)));
        }

        @Test
        @DisplayName("subtract: (5 + 6x) − (2 + 3x + 4x²) = 3 + 3x − 4x²")
        void subtract() {
            assertCoefficients(new double[]{3, 3, -4}, poly(5, 6).subtract(poly(2, 3, 4)));
        }

        @Test
        @DisplayName("subtract: p − p = 0")
        void subtractSelf() {
            Polynomial p = poly(1, 2, 3);
            assertTrue(p.subtract(p).isZero());
            assertTrue(p.subtract(poly(1, 2, 3)).isZero());
        }

        @Test
        @DisplayName("multiply: (x + 1)(x − 1) = x² − 1")
        void multiplyDifferenceOfSquares() {
            assertCoefficients(new double[]{-1, 0, 1}, poly(1, 1).multiply(poly(-1, 1)));
        }

        @Test
        @DisplayName("multiply: (x + 1)² = x² + 2x + 1")
        void multiplySquare() {
            assertCoefficients(new double[]{1, 2, 1}, poly(1, 1).multiply(poly(1, 1)));
        }

        @Test
        @DisplayName("multiply: (x−1)(x−2)(x−3) = x³ − 6x² + 11x − 6")
        void multiplyThreeFactors() {
            Polynomial p = poly(-1, 1).multiply(poly(-2, 1)).multiply(poly(-3, 1));
            assertCoefficients(new double[]{-6, 11, -6, 1}, p);
        }

        @Test
        @DisplayName("multiply: на константу та на одиницю")
        void multiplyByConstantAndOne() {
            assertCoefficients(new double[]{2, 4, 6}, poly(1, 2, 3).multiply(poly(2)));
            assertEquals(poly(1, 2, 3), poly(1, 2, 3).multiply(poly(1)));
        }

        @Test
        @DisplayName("multiply: на нуль дає нуль")
        void multiplyByZero() {
            assertTrue(poly(1, 2, 3).multiply(poly(0)).isZero());
            assertTrue(poly(0).multiply(poly(1, 2, 3)).isZero());
        }

        @Test
        @DisplayName("multiply: степінь добутку дорівнює сумі степенів")
        void multiplyDegree() {
            assertEquals(5, poly(1, 0, 2).multiply(poly(3, 1, 0, 1)).degree());
        }

        @Test
        @DisplayName("multiply: комутативність")
        void multiplyCommutative() {
            Polynomial p = poly(1, 2);
            Polynomial q = poly(3, 0, 1);
            assertEquals(p.multiply(q), q.multiply(p));
        }

        @Test
        @DisplayName("null-операнд → IllegalArgumentException")
        void nullOperand() {
            Polynomial p = poly(1, 2);
            assertThrows(IllegalArgumentException.class, () -> p.add(null));
            assertThrows(IllegalArgumentException.class, () -> p.subtract(null));
            assertThrows(IllegalArgumentException.class, () -> p.multiply(null));
        }

        @Test
        @DisplayName("Значення в точці: (p + q)(x) = p(x) + q(x), (p·q)(x) = p(x)·q(x)")
        void evaluationHomomorphism() {
            Polynomial p = poly(1, 2, 3);
            Polynomial q = poly(-4, 0, 5, 1);
            for (double x : new double[]{-3, 0, 1.5, 2}) {
                assertEquals(p.evaluate(x) + q.evaluate(x), p.add(q).evaluate(x), EPS);
                assertEquals(p.evaluate(x) - q.evaluate(x), p.subtract(q).evaluate(x), EPS);
                assertEquals(p.evaluate(x) * q.evaluate(x), p.multiply(q).evaluate(x), EPS);
            }
        }

        @Test
        @DisplayName("Операції не змінюють операнди (незмінність)")
        void immutability() {
            Polynomial p = poly(1, 2, 3);
            Polynomial q = poly(4, 5);

            p.add(q);
            p.subtract(q);
            p.multiply(q);
            p.derivative();
            p.divideAndRemainder(q);

            assertCoefficients(new double[]{1, 2, 3}, p);
            assertCoefficients(new double[]{4, 5}, q);
        }
    }

    @Nested
    @DisplayName("divideAndRemainder / divide / remainder")
    class DivisionTests {

        @Test
        @DisplayName("Точне ділення: (x² − 1) / (x − 1) = x + 1, остача 0")
        void exactDivision() {
            Polynomial[] qr = poly(-1, 0, 1).divideAndRemainder(poly(-1, 1));
            assertCoefficients(new double[]{1, 1}, qr[0]);
            assertTrue(qr[1].isZero());
        }

        @Test
        @DisplayName("З остачею: (x³ + 2x² + 3x + 4) / (x + 1) = x² + x + 2, остача 2")
        void divisionWithRemainder() {
            Polynomial[] qr = poly(4, 3, 2, 1).divideAndRemainder(poly(1, 1));
            assertCoefficients(new double[]{2, 1, 1}, qr[0]);
            assertCoefficients(new double[]{2}, qr[1]);
        }

        @Test
        @DisplayName("Старший коефіцієнт дільника ≠ 1: (6x² + 5x + 1) / (2x + 1) = 3x + 1")
        void nonMonicDivisor() {
            Polynomial[] qr = poly(1, 5, 6).divideAndRemainder(poly(1, 2));
            assertCoefficients(new double[]{1, 3}, qr[0]);
            assertTrue(qr[1].isZero());
        }

        @Test
        @DisplayName("Дробовий результат: (x² + 1) / (2x) = 0.5x, остача 1")
        void fractionalQuotient() {
            Polynomial[] qr = poly(1, 0, 1).divideAndRemainder(poly(0, 2));
            assertCoefficients(new double[]{0, 0.5}, qr[0]);
            assertCoefficients(new double[]{1}, qr[1]);
        }

        @Test
        @DisplayName("Однакові степені: (2x + 3) / (x + 1) = 2, остача 1")
        void equalDegrees() {
            Polynomial[] qr = poly(3, 2).divideAndRemainder(poly(1, 1));
            assertCoefficients(new double[]{2}, qr[0]);
            assertCoefficients(new double[]{1}, qr[1]);
        }

        @Test
        @DisplayName("Ділення на константу: (2 + 4x + 6x²) / 2 = 1 + 2x + 3x²")
        void divideByConstant() {
            Polynomial[] qr = poly(2, 4, 6).divideAndRemainder(poly(2));
            assertCoefficients(new double[]{1, 2, 3}, qr[0]);
            assertTrue(qr[1].isZero());
        }

        @Test
        @DisplayName("Степінь діленого менший за степінь дільника: частка 0, остача = ділене")
        void dividendDegreeLower() {
            Polynomial dividend = poly(1, 1);
            Polynomial[] qr = dividend.divideAndRemainder(poly(1, 0, 1));
            assertTrue(qr[0].isZero());
            assertSame(dividend, qr[1]);
        }

        @Test
        @DisplayName("Нульове ділене: частка 0, остача 0")
        void zeroDividend() {
            Polynomial[] qr = poly(0).divideAndRemainder(poly(1, 1));
            assertTrue(qr[0].isZero());
            assertTrue(qr[1].isZero());
        }

        @Test
        @DisplayName("Ділення на нульовий поліном → ArithmeticException")
        void divisionByZero() {
            assertThrows(ArithmeticException.class, () -> poly(1, 2).divideAndRemainder(poly(0)));
            assertThrows(ArithmeticException.class, () -> poly(1, 2).divideAndRemainder(poly(0, 0, 0)));
            assertThrows(ArithmeticException.class, () -> poly(0).divideAndRemainder(poly(0)));
        }

        @Test
        @DisplayName("Ділення на null → IllegalArgumentException")
        void divisionByNull() {
            assertThrows(IllegalArgumentException.class, () -> poly(1, 2).divideAndRemainder(null));
            assertThrows(IllegalArgumentException.class, () -> poly(1, 2).divide(null));
            assertThrows(IllegalArgumentException.class, () -> poly(1, 2).remainder(null));
        }

        @Test
        @DisplayName("divide повертає частку, remainder повертає остачу")
        void divideAndRemainderShortcuts() {
            Polynomial p = poly(4, 3, 2, 1);
            Polynomial d = poly(1, 1);
            assertEquals(p.divideAndRemainder(d)[0], p.divide(d));
            assertEquals(p.divideAndRemainder(d)[1], p.remainder(d));
        }

        @Test
        @DisplayName("Тотожність ділення: p = d·q + r, deg r < deg d")
        void divisionIdentity() {
            Polynomial[][] cases = {
                    {poly(4, 3, 2, 1), poly(1, 1)},
                    {poly(1, 2, 3, 4, 5), poly(1, 0, 2)},
                    {poly(-6, 11, -6, 1), poly(-2, 1)},
                    {poly(7, 0, 0, 0, 3), poly(2, 5, 1)},
                    {poly(1, 1), poly(3, 0, 4)}
            };
            for (Polynomial[] c : cases) {
                Polynomial p = c[0];
                Polynomial d = c[1];
                Polynomial q = p.divide(d);
                Polynomial r = p.remainder(d);

                Polynomial reconstructed = d.multiply(q).add(r);
                assertTrue(p.equals(reconstructed, EPS), "p = d·q + r для " + p + " ÷ " + d);
                assertTrue(r.isZero() || r.degree() < d.degree(), "deg r < deg d для " + p + " ÷ " + d);
            }
        }

        @Test
        @DisplayName("Ділення добутку на множник: ((x−1)(x−2)(x−3)) / (x−2) = (x−1)(x−3), остача 0")
        void divideProductByFactor() {
            Polynomial product = poly(-1, 1).multiply(poly(-2, 1)).multiply(poly(-3, 1));
            Polynomial[] qr = product.divideAndRemainder(poly(-2, 1));
            assertTrue(qr[0].equals(poly(-1, 1).multiply(poly(-3, 1)), EPS));
            assertTrue(qr[1].isZero());
        }
    }


    @Nested
    @DisplayName("roots")
    class RootsTests {

        @Test
        @DisplayName("Константа: коренів немає (порожній масив); нуль: ArithmeticException")
        void constants() {
            assertEquals(0, poly(5).roots().length);
            assertThrows(ArithmeticException.class, () -> poly(0).roots());
        }

        @Test
        @DisplayName("Лінійний: 3x − 6 = 0 → 2; 2x + 4 = 0 → −2")
        void linear() {
            assertArrayEquals(new double[]{2}, poly(-6, 3).roots(), EPS);
            assertArrayEquals(new double[]{-2}, poly(4, 2).roots(), EPS);
        }

        @Test
        @DisplayName("Квадратний, два корені: x² − 3x + 2 = 0 → 1 і 2")
        void quadraticTwoRoots() {
            assertArrayEquals(new double[]{1, 2}, poly(2, -3, 1).roots(), EPS);
        }

        @Test
        @DisplayName("Квадратний: x² + x − 6 = 0 → −3 і 2")
        void quadraticNegativeAndPositiveRoot() {
            assertArrayEquals(new double[]{-3, 2}, poly(-6, 1, 1).roots(), EPS);
        }

        @Test
        @DisplayName("Корені відсортовані за зростанням і при a < 0: −x² + 4 = 0 → −2 і 2")
        void rootsSortedForNegativeLeading() {
            assertArrayEquals(new double[]{-2, 2}, poly(4, 0, -1).roots(), EPS);
        }

        @Test
        @DisplayName("Один подвійний корінь: x² − 2x + 1 = 0 → 1")
        void doubleRoot() {
            assertArrayEquals(new double[]{1}, poly(1, -2, 1).roots(), EPS);
        }

        @Test
        @DisplayName("Дискримінант < 0: x² + 1 = 0 → коренів немає")
        void noRealRoots() {
            assertEquals(0, poly(1, 0, 1).roots().length);
            assertEquals(0, poly(1, 1, 1).roots().length);
        }

        @Test
        @DisplayName("Відносний допуск на дискримінант: |d| ≤ 1e-12 · scale вважається нулем")
        void discriminantTolerance() {
            assertArrayEquals(new double[]{-1}, poly(1 + 1e-13, 2, 1).roots(), 1e-6);
            assertArrayEquals(new double[]{-1}, poly(1 - 1e-13, 2, 1).roots(), 1e-6);
        }

        @Test
        @DisplayName("Старші нулі не заважають: {2, -3, 1, 0, 0} це квадратний поліном")
        void trailingZerosDoNotBreakRoots() {
            assertArrayEquals(new double[]{1, 2}, poly(2, -3, 1, 0, 0).roots(), EPS);
        }

        @Test
        @DisplayName("Підстановка знайдених коренів дає нуль")
        void rootsSatisfyPolynomial() {
            Polynomial[] polynomials = {poly(2, -3, 1), poly(-6, 1, 1), poly(4, 0, -1), poly(-6, 3), poly(-7, 0.5, 2)};
            for (Polynomial p : polynomials) {
                for (double root : p.roots()) {
                    assertEquals(0.0, p.evaluate(root), 1e-9, "P(" + root + ") для " + p);
                }
            }
        }

        @Test
        @DisplayName("Степінь більший за 2 → UnsupportedOperationException")
        void unsupportedDegree() {
            assertThrows(UnsupportedOperationException.class, () -> poly(-6, 11, -6, 1).roots());
        }

        @Test
        @DisplayName("Корінь 0 не має бути від'ємним нулем: 3x = 0 і x² = 0")
        void zeroRootIsNotNegativeZero() {
            assertAll(
                    () -> assertEquals(0.0, poly(0, 3).roots()[0]),
                    () -> assertEquals(0.0, poly(0, 0, 1).roots()[0])
            );
        }

        @Test
        @DisplayName("Малі коефіцієнти: {0, 0, 1e-13} зберігає степінь 2, {-1e-13, 0, 1e-13} має корені -1 і 1")
        void smallCoefficientsRelativeThreshold() {
            assertEquals(2, poly(0, 0, 1e-13).degree());
            assertArrayEquals(new double[]{-1, 1}, poly(-1e-13, 0, 1e-13).roots(), 1e-9);
            assertArrayEquals(new double[]{0.9, 1.1}, poly(0.99e-7, -2e-7, 1e-7).roots(), 1e-9);
        }

        @Test
        @DisplayName("Чисельна стабільність: 1e-8x² + x + 1e-8 → -1e8 і -1e-8")
        void numericallyStableQuadraticRoots() {
            double[] roots = poly(1e-8, 1, 1e-8).roots();
            assertEquals(2, roots.length);
            assertEquals(-1e8, roots[0], 1.0);
            assertEquals(-1e-8, roots[1], 1e-16);
        }
    }
}