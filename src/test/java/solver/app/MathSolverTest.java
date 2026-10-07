package solver.app;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

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

    private static String run(String... args) {
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
        try {
            MathSolver.main(args);
        } finally {
            System.setOut(original);
        }
        return buffer.toString(StandardCharsets.UTF_8).trim();
    }
}
