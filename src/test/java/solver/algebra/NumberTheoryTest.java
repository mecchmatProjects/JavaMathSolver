package solver.algebra;

import static org.junit.jupiter.api.Assertions.*;
import static solver.core.Messages.*;
import static solver.core.Output.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class NumberTheoryTest {


    private static String capture(Runnable action) {
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(err, true, StandardCharsets.UTF_8));
        try {
            action.run();
        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
        return "[stdout] " + out.toString(StandardCharsets.UTF_8)
                + "[stderr] " + err.toString(StandardCharsets.UTF_8);
    }

    private static void assertPrintsResult(long expected, Runnable action) {
        assertEquals(capture(() -> printResult(expected)), capture(action));
    }

    private static void assertPrintsInvalidInput(Runnable action) {
        assertEquals(capture(() -> printError(ERR_INVALID_INPUT)), capture(action));
    }

    private static long referenceFactorial(int n) {
        BigInteger result = BigInteger.ONE;
        for (int i = 2; i <= n; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        return result.longValueExact();
    }

    private static long referenceFibonacci(int n) {
        BigInteger a = BigInteger.ZERO;
        BigInteger b = BigInteger.ONE;
        for (int i = 0; i < n; i++) {
            BigInteger next = a.add(b);
            a = b;
            b = next;
        }
        return a.longValueExact();
    }


    @Test
    @DisplayName("max3: максимум не залежить від позиції (усі 6 перестановок {1, 5, 3})")
    void max3AllPermutations() {
        double[][] permutations = {
                {1, 5, 3}, {1, 3, 5}, {5, 1, 3}, {5, 3, 1}, {3, 1, 5}, {3, 5, 1}
        };
        for (double[] p : permutations) {
            assertEquals(5.0, NumberTheory.max3(p[0], p[1], p[2]));
        }
    }

    @Test
    @DisplayName("max3: від'ємні числа та суміш знаків")
    void max3Negatives() {
        assertEquals(-1.0, NumberTheory.max3(-1, -2, -3));
        assertEquals(0.0, NumberTheory.max3(-5, 0, -7));
        assertEquals(2.0, NumberTheory.max3(-10, 2, -3));
    }

    @Test
    @DisplayName("max3: однакові значення")
    void max3Equal() {
        assertEquals(5.0, NumberTheory.max3(5, 5, 5));
        assertEquals(7.0, NumberTheory.max3(7, 7, 2));
    }

    @Test
    @DisplayName("max3: дробові числа")
    void max3Fractions() {
        assertEquals(2.5, NumberTheory.max3(1.5, 2.5, 2.4));
        assertEquals(0.30000000000000004, NumberTheory.max3(0.1, 0.2 + 0.1, 0.3));
    }

    @Test
    @DisplayName("max3: Infinity")
    void max3Infinity() {
        assertEquals(Double.POSITIVE_INFINITY,
                NumberTheory.max3(Double.NEGATIVE_INFINITY, 0, Double.POSITIVE_INFINITY));
        assertEquals(-1.0,
                NumberTheory.max3(Double.NEGATIVE_INFINITY, -1, Double.NEGATIVE_INFINITY));
    }

    @Test
    @DisplayName("max3: 0.0 більший за -0.0")
    void max3NegativeZero() {
        assertEquals(0.0, NumberTheory.max3(-0.0, 0.0, -0.0));
    }

    @Test
    @DisplayName("max3: якщо будь-який аргумент NaN, результат NaN")
    void max3NaN() {
        assertTrue(Double.isNaN(NumberTheory.max3(Double.NaN, 1, 2)));
        assertTrue(Double.isNaN(NumberTheory.max3(1, Double.NaN, 3)));
        assertTrue(Double.isNaN(NumberTheory.max3(1, 2, Double.NaN)));
    }


    @ParameterizedTest(name = "[{index}] НСД({0}, {1}) = {2}")
    @CsvSource({
            "12, 18, 6",
            "18, 12, 6",
            "48, 180, 12",
            "1071, 462, 21",
            "17, 13, 1",
            "7, 7, 7",
            "3, 9, 3",
            "1000000007, 998244353, 1",
            "1099511627776, 4611686018427387904, 1099511627776",
            "9223372036854775807, 9223372036854775807, 9223372036854775807"
    })
    @DisplayName("solveGcd: додатні числа")
    void gcdPositive(long a, long b, long expected) {
        assertPrintsResult(expected, () -> NumberTheory.solveGcd(a, b));
    }

    @ParameterizedTest(name = "[{index}] НСД({0}, {1}) = {2}")
    @CsvSource({
            "0, 5, 5",
            "5, 0, 5",
            "0, -5, 5"
    })
    @DisplayName("solveGcd: один з аргументів нуль, результат дорівнює модулю другого")
    void gcdWithZero(long a, long b, long expected) {
        assertPrintsResult(expected, () -> NumberTheory.solveGcd(a, b));
    }

    @Test
    @DisplayName("solveGcd: НСД(0, 0) = 0 (особлива гілка)")
    void gcdZeroZero() {
        assertPrintsResult(0L, () -> NumberTheory.solveGcd(0, 0));
    }

    @ParameterizedTest(name = "[{index}] НСД({0}, {1}) = {2}")
    @CsvSource({
            "-12, 18, 6",
            "12, -18, 6",
            "-12, -18, 6",
            "-7, 7, 7"
    })
    @DisplayName("solveGcd: від'ємні числа, НСД завжди невід'ємний")
    void gcdNegative(long a, long b, long expected) {
        assertPrintsResult(expected, () -> NumberTheory.solveGcd(a, b));
    }

    @Test
    @DisplayName("solveGcd: симетричність НСД(a, b) = НСД(b, a)")
    void gcdSymmetric() {
        assertEquals(capture(() -> NumberTheory.solveGcd(84, 36)),
                capture(() -> NumberTheory.solveGcd(36, 84)));
    }


    @ParameterizedTest(name = "[{index}] {0}! = {1}")
    @CsvSource({
            "0, 1",
            "1, 1",
            "2, 2",
            "3, 6",
            "4, 24",
            "5, 120",
            "6, 720",
            "7, 5040",
            "8, 40320",
            "9, 362880",
            "10, 3628800",
            "11, 39916800",
            "12, 479001600",
            "13, 6227020800",
            "14, 87178291200",
            "15, 1307674368000",
            "16, 20922789888000",
            "17, 355687428096000",
            "18, 6402373705728000",
            "19, 121645100408832000",
            "20, 2432902008176640000"
    })
    @DisplayName("factorial: таблиця значень від 0! до 20!")
    void factorialTable(long n, long expected) {
        assertPrintsResult(expected, () -> NumberTheory.factorial(n));
    }

    @Test
    @DisplayName("factorial: для всіх n від 0 до 20 збігається з незалежним еталоном (BigInteger)")
    void factorialMatchesReference() {
        for (int n = 0; n <= 20; n++) {
            final int k = n;
            assertPrintsResult(referenceFactorial(k), () -> NumberTheory.factorial(k));
        }
    }

    @Test
    @DisplayName("factorial: 20 ще влізає в long, а 21! уже ні (межа в коді точна)")
    void factorialBoundaryIsExact() {
        assertDoesNotThrow(() -> referenceFactorial(20));
        assertThrows(ArithmeticException.class, () -> referenceFactorial(21));
    }

    @ParameterizedTest(name = "[{index}] n = {0}")
    @ValueSource(longs = {-1, -100, 21, 22, 100, Long.MAX_VALUE, Long.MIN_VALUE})
    @DisplayName("factorial: n < 0 або n > 20 → повідомлення про некоректний ввід")
    void factorialInvalidInput(long n) {
        assertPrintsInvalidInput(() -> NumberTheory.factorial(n));
    }


    @ParameterizedTest(name = "[{index}] F({0}) = {1}")
    @CsvSource({
            "0, 0",
            "1, 1",
            "2, 1",
            "3, 2",
            "4, 3",
            "5, 5",
            "6, 8",
            "7, 13",
            "8, 21",
            "9, 34",
            "10, 55",
            "20, 6765",
            "30, 832040",
            "50, 12586269025",
            "90, 2880067194370816120",
            "91, 4660046610375530309",
            "92, 7540113804746346429"
    })
    @DisplayName("fibonacci: відомі значення")
    void fibonacciTable(long n, long expected) {
        assertPrintsResult(expected, () -> NumberTheory.fibonacci(n));
    }

    @Test
    @DisplayName("fibonacci: для всіх n від 0 до 92 збігається з незалежним еталоном (BigInteger)")
    void fibonacciMatchesReference() {
        for (int n = 0; n <= 92; n++) {
            final int k = n;
            assertPrintsResult(referenceFibonacci(k), () -> NumberTheory.fibonacci(k));
        }
    }

    @Test
    @DisplayName("fibonacci: F(92) ще влізає в long, а F(93) уже ні (межа в коді точна)")
    void fibonacciBoundaryIsExact() {
        assertDoesNotThrow(() -> referenceFibonacci(92));
        assertThrows(ArithmeticException.class, () -> referenceFibonacci(93));
    }

    @ParameterizedTest(name = "[{index}] n = {0}")
    @ValueSource(longs = {-1, -100, 93, 94, 1000, Long.MAX_VALUE, Long.MIN_VALUE})
    @DisplayName("fibonacci: n < 0 або n > 92 → повідомлення про некоректний ввід")
    void fibonacciInvalidInput(long n) {
        assertPrintsInvalidInput(() -> NumberTheory.fibonacci(n));
    }


    @Test
    @DisplayName("Контроль тестів: вивід помилки відрізняється від виводу результату")
    void errorOutputDiffersFromResultOutput() {
        assertNotEquals(capture(() -> printResult(1L)), capture(() -> printError(ERR_INVALID_INPUT)));
    }

    @Test
    @DisplayName("Утиліт-клас: приватний конструктор")
    void privateConstructor() throws Exception {
        var ctor = NumberTheory.class.getDeclaredConstructor();
        assertTrue(java.lang.reflect.Modifier.isPrivate(ctor.getModifiers()));
    }
}