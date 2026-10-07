package solver.app;

import org.junit.jupiter.api.Test;

import solver.core.Messages;

import static org.junit.jupiter.api.Assertions.*;
import static solver.testutil.StdOut.capture;

/**
 * CORE-11: маршрутизація та guard-и диспетчера. Правильність самих формул —
 * зона тестів Algebra / Geometry; тут перевіряється, що команда доходить до
 * свого методу і що перевірки вхідних даних у dispatch спрацьовують.
 */
class CommandDispatcherTest {

    private static String dispatch(String command, Number... args) {
        return capture(() -> CommandDispatcher.dispatch(command, args));
    }

    // ===================================================================
    // Ділення та корінь
    // ===================================================================

    @Test
    void divisionByZeroIsRejected() {
        assertEquals(Messages.ERR_DIVISION_BY_ZERO, dispatch("div", 1L, 0L));
        assertEquals(Messages.ERR_DIVISION_BY_ZERO, dispatch("div", 0L, 0L));
        assertEquals(Messages.ERR_DIVISION_BY_ZERO, dispatch("div", 1L, -0.0));
        assertEquals("Result: 2.000000", dispatch("div", 6L, 3L));
    }

    @Test
    void squareRootOfNegativeIsRejected() {
        assertEquals(Messages.ERR_INVALID_INPUT, dispatch("sqrt", -1L));
        assertEquals("Result: 0.000000", dispatch("sqrt", -0.0));
        assertEquals("Result: 4.000000", dispatch("sqrt", 16L));
    }

    // ===================================================================
    // Цілочисельні команди: toLongExact у dispatch
    // ===================================================================

    @Test
    void gcdNeedsIntegers() {
        assertEquals(Messages.ERR_INVALID_INPUT, dispatch("gcd", 1.5, 2L));
        assertEquals(Messages.ERR_INVALID_INPUT, dispatch("gcd", 2L, 1.5));
        assertEquals("Result: 10", dispatch("gcd", 1e3, 10L)); // 1e3 — ціле, записане як double
    }

    @Test
    void factorialAndFibonacciNeedIntegers() {
        assertEquals("Result: 120", dispatch("factorial", 5.0));
        assertEquals(Messages.ERR_INVALID_INPUT, dispatch("factorial", 5.5));
        assertEquals("Result: 55", dispatch("fibonacci", 10L));
        assertEquals(Messages.ERR_INVALID_INPUT, dispatch("fibonacci", 10.5));
    }

    @Test
    void monteCarloNeedsPositiveIntInRange() {
        assertEquals(Messages.ERR_INVALID_INPUT, dispatch("monte-carlo-triangle", 0L));
        assertEquals(Messages.ERR_INVALID_INPUT, dispatch("monte-carlo-triangle", -5L));
        assertEquals(Messages.ERR_INVALID_INPUT, dispatch("monte-carlo-triangle", 1.5));
        assertEquals(Messages.ERR_INVALID_INPUT, dispatch("monte-carlo-triangle", 3_000_000_000L));
        assertTrue(dispatch("monte-carlo-triangle", 1000L).startsWith("Result: 0."));
    }

    @Test
    void monteCarloIsDeterministic() {
        assertEquals(dispatch("monte-carlo-triangle", 1000L), dispatch("monte-carlo-triangle", 1000L));
    }

    // ===================================================================
    // Аліаси та невідомі команди
    // ===================================================================

    @Test
    void taylorAliasGivesSameResultAsCanonicalName() {
        assertEquals(dispatch("taylor-sin", 1L, 1e-9), dispatch("sin-taylor", 1L, 1e-9));
        assertEquals(dispatch("taylor-geom-series", 0.5, 1e-9), dispatch("series-ye", 0.5, 1e-9));
        assertEquals(dispatch("taylor-inv-one-plus-x2", 0.5, 1e-9), dispatch("series-y", 0.5, 1e-9));
    }

    @Test
    void unknownCommandFallsToDefault() {
        assertEquals("Unknown command: nope\n" + Messages.HINT_HELP, dispatch("nope"));
    }

    @Test
    void helpIsDispatched() {
        assertTrue(dispatch("help").startsWith("JavaMathSolver v" + MathSolver.VERSION));
    }

    // ===================================================================
    // Текстові команди
    // ===================================================================

    @Test
    void validateExprIsDispatched() {
        assertEquals("Result: true", capture(() -> CommandDispatcher.dispatchText("validate-expr", "2*x + 3")));
        assertEquals("Error: Invalid token: 2..5",
                capture(() -> CommandDispatcher.dispatchText("validate-expr", "1 + 2..5")));
    }

    @Test
    void unimplementedTextCommandFallsToDefault() {
        assertEquals("Unknown command: tokenize\n" + Messages.HINT_HELP,
                capture(() -> CommandDispatcher.dispatchText("tokenize", "2*x")));
    }
}
