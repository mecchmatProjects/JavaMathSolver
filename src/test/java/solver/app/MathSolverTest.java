package solver.app;

import org.junit.jupiter.api.Test;

import solver.core.Messages;

import static org.junit.jupiter.api.Assertions.*;
import static solver.testutil.StdOut.capture;

class MathSolverTest {

    @Test
    void joinTextKeepsQuotedExpressionAsIs() {
        assertEquals("2*x + 3", MathSolver.joinText(new String[] {"tokenize", "2*x + 3"}));
    }

    @Test
    void joinTextJoinsUnquotedWordsWithSpaces() {
        assertEquals("2*x + 3", MathSolver.joinText(new String[] {"tokenize", "2*x", "+", "3"}));
    }

    @Test
    void joinTextTrimsOuterWhitespace() {
        assertEquals("sin(x)", MathSolver.joinText(new String[] {"tokenize", "  sin(x)  "}));
    }

    @Test
    void joinTextOfBlankExpressionIsEmpty() {
        assertEquals("", MathSolver.joinText(new String[] {"tokenize", ""}));
        assertEquals("", MathSolver.joinText(new String[] {"tokenize", "   "}));
    }

    // ===================================================================
    // CLI: validate-expr (C8)
    // ===================================================================

    @Test
    void validateExprAcceptsQuotedAndSplitExpression() {
        assertEquals("Result: true", run("validate-expr", "2*x + 3"));
        assertEquals("Result: true", run("validate-expr", "2*x", "+", "3"));
        assertEquals("Result: true", run("VALIDATE-EXPR", "sin(x) + sqrt(y)"));
    }

    @Test
    void validateExprReportsFirstInvalidToken() {
        assertEquals("Error: Invalid token: 2..5", run("validate-expr", "2..5"));
        assertEquals("Error: Invalid token: @", run("validate-expr", "x@ + 3.4.5"));
        assertEquals("Error: Invalid token: .5", run("validate-expr", "x + .5"));
    }

    @Test
    void validateExprWithoutExpressionIsNotEnoughArgs() {
        assertEquals("Error: Not enough arguments", run("validate-expr"));
        assertEquals("Error: Not enough arguments", run("validate-expr", "   "));
    }

    @Test
    void unimplementedTextCommandsAreStillUnknown() {
        // C9 / C10 ще не реалізовані: вони мають доходити до dispatchText, а не падати
        assertTrue(run("tokenize", "2*x").startsWith("Unknown command: tokenize"));
    }

    // ===================================================================
    // main: загальний потік
    // ===================================================================

    @Test
    void noArgumentsPrintsBanner() {
        assertEquals("JavaMathSolver v" + MathSolver.VERSION + "\n" + Messages.HINT_HELP, run());
    }

    @Test
    void commandIsCaseInsensitiveAndTrimmed() {
        assertEquals("Result: 3.000000", run("ADD", "1", "2"));
        assertEquals("Result: 3.000000", run("  add ", "1", "2"));
    }

    @Test
    void unknownCommandKeepsOriginalSpelling() {
        assertEquals("Unknown command: HeLLo\n" + Messages.HINT_HELP, run("HeLLo", "1", "2"));
    }

    @Test
    void arityIsCheckedBeforeNumbers() {
        // "x" не число, але спершу має спрацювати перевірка кількості
        assertEquals(Messages.ERR_NOT_ENOUGH_ARGS, run("add", "x"));
        assertEquals(Messages.ERR_TOO_MANY_ARGS, run("sqrt", "x", "y"));
        assertEquals(Messages.ERR_TOO_MANY_ARGS, run("help", "1"));
    }

    @Test
    void firstInvalidNumberIsReported() {
        assertEquals("Invalid number: abc", run("add", "abc", "def"));
        assertEquals("Invalid number: 1,5", run("add", "1", "1,5"));
    }

    @Test
    void numbersInAllSupportedFormats() {
        assertEquals("Result: 1000.000010", run("add", "1e3", "1e-5"));
        assertEquals("Result: 0.000000", run("add", "+5", "-5"));
        assertEquals("Result: 1.500000", run("add", "1.", ".5"));
    }

    @Test
    void polygonParityErrorComesFromValidator() {
        assertEquals(Messages.ERR_ODD_COORDINATES, run("polygon", "0", "0", "1", "0", "1", "1", "5"));
    }

    private static String run(String... args) {
        return capture(() -> MathSolver.main(args));
    }
}
