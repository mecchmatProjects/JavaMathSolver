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
    void validateExprEscapesControlCharacterInError() {
        assertEquals("Error: Invalid token: \\n",
                capture(() -> CommandDispatcher.dispatchText("validate-expr", "x\n+1")));
    }

    @Test
    void validateExprOfBlankTextIsNotEnoughArgs() {
        // main сюди порожнє не передає, але dispatchText не має мовчати
        assertEquals(Messages.ERR_NOT_ENOUGH_ARGS,
                capture(() -> CommandDispatcher.dispatchText("validate-expr", "   ")));
    }

    @Test
    void unknownTextCommandFallsToDefault() {
        assertEquals("Unknown command: nope\n" + Messages.HINT_HELP,
                capture(() -> CommandDispatcher.dispatchText("nope", "2*x")));
    }

    // ===================================================================
    // C10 — tokenize
    // ===================================================================

    private static String tokenize(String text) {
        return capture(() -> CommandDispatcher.dispatchText("tokenize", text));
    }

    @Test
    void tokenizeSpecExample() {
        assertEquals(String.join("\n",
                        "0: NUMBER 2",
                        "1: MULTIPLY *",
                        "2: IDENTIFIER x",
                        "3: POWER ^",
                        "4: NUMBER 2",
                        "5: PLUS +",
                        "6: NUMBER 3",
                        "7: MULTIPLY *",
                        "8: IDENTIFIER x",
                        "9: MINUS -",
                        "10: NUMBER 5"),
                tokenize("2*x^2 + 3*x - 5"));
    }

    @Test
    void tokenizeSingleToken() {
        assertEquals("0: IDENTIFIER x", tokenize("x"));
        assertEquals("0: NUMBER -3.14", tokenize("-3.14"));
    }

    @Test
    void tokenizeFunctionsAndParentheses() {
        assertEquals(String.join("\n",
                        "0: IDENTIFIER sin",
                        "1: LEFT_PARENTHESIS (",
                        "2: IDENTIFIER x",
                        "3: RIGHT_PARENTHESIS )",
                        "4: DIVIDE /",
                        "5: IDENTIFIER sqrt",
                        "6: LEFT_PARENTHESIS (",
                        "7: NUMBER 2",
                        "8: RIGHT_PARENTHESIS )"),
                tokenize("sin(x) / sqrt(2)"));
    }

    @Test
    void tokenizeShowsUnknownTokensInsteadOfFailing() {
        assertEquals(String.join("\n",
                        "0: NUMBER 1",
                        "1: PLUS +",
                        "2: UNKNOWN 2..5",
                        "3: UNKNOWN @"),
                tokenize("1 + 2..5 @"));
    }

    @Test
    void tokenizeEscapesControlCharacters() {
        assertEquals("0: IDENTIFIER x\n1: UNKNOWN \\n\n2: IDENTIFIER y", tokenize("x\ny"));
    }

    @Test
    void tokenizeKeepsSurrogatePairTogether() {
        assertEquals("0: UNKNOWN \uD83D\uDE00", tokenize("\uD83D\uDE00"));
    }

    @Test
    void tokenizeIndexesAreSequentialFromZero() {
        String[] lines = tokenize("a + b - c * d / e ^ f").split("\n");
        assertEquals(11, lines.length);
        for (int i = 0; i < lines.length; i++) {
            assertTrue(lines[i].startsWith(i + ": "), lines[i]);
        }
    }

    @Test
    void tokenizeOfBlankTextIsNotEnoughArgs() {
        assertEquals(Messages.ERR_NOT_ENOUGH_ARGS, tokenize(""));
        assertEquals(Messages.ERR_NOT_ENOUGH_ARGS, tokenize(" \t "));
    }

    @Test
    void tokenizeDoesNotUseResultPrefix() {
        assertFalse(tokenize("2*x").contains("Result:"));
    }

    // ===================================================================
    // C9 — token-stats у CLI
    // ===================================================================

    @Test
    void tokenStatsPrintsFourLabelledLines() {
        assertEquals("Numbers: 2\nIdentifiers: 4\nOperators: 4\nParentheses: 2",
                capture(() -> CommandDispatcher.dispatchText("token-stats", "2*x + 3*x - sin(x)")));
    }

    @Test
    void tokenStatsSkipsUnknownTokens() {
        assertEquals("Numbers: 0\nIdentifiers: 1\nOperators: 0\nParentheses: 0",
                capture(() -> CommandDispatcher.dispatchText("token-stats", "x @ 2..5")));
    }
}
