package solver.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static solver.testutil.StdOut.capture;

import org.junit.jupiter.api.Test;
import solver.core.Messages;

/**
 * T5: CLI-демо C19 {@code walk-expr}: формат рядка
 * {@code <position>: <TYPE> <lexeme>   (next: <TYPE> <lexeme>)}.
 */
class WalkExprCommandTest {

    private static String walk(String text) {
        return capture(() -> CommandDispatcher.dispatchText("walk-expr", text));
    }

    private static String run(String... args) {
        return capture(() -> MathSolver.main(args));
    }

    @Test
    void simpleExpression() {
        assertEquals(String.join("\n",
                        "0: NUMBER 2   (next: PLUS +)",
                        "1: PLUS +   (next: IDENTIFIER x)",
                        "2: IDENTIFIER x   (next: EOF <EOF>)"),
                walk("2 + x"));
    }

    @Test
    void functionCallAndPower() {
        assertEquals(String.join("\n",
                        "0: IDENTIFIER sin   (next: LEFT_PARENTHESIS ()",
                        "1: LEFT_PARENTHESIS (   (next: IDENTIFIER x)",
                        "2: IDENTIFIER x   (next: RIGHT_PARENTHESIS ))",
                        "3: RIGHT_PARENTHESIS )   (next: POWER ^)",
                        "4: POWER ^   (next: NUMBER 2)",
                        "5: NUMBER 2   (next: EOF <EOF>)"),
                walk("sin(x)^2"));
    }

    @Test
    void singleTokenEndsWithEof() {
        assertEquals("0: IDENTIFIER x   (next: EOF <EOF>)", walk("x"));
    }

    @Test
    void lastLineAlwaysPointsToEof() {
        String out = walk("2*x^2 + 3*x - 5");
        String[] lines = out.split("\n");
        assertEquals(11, lines.length);
        assertTrue(lines[10].startsWith("10: NUMBER 5"));
        assertTrue(lines[10].endsWith("(next: EOF <EOF>)"));
    }

    @Test
    void unknownTokensAreShownNotRejected() {
        assertEquals(String.join("\n",
                        "0: NUMBER 1   (next: PLUS +)",
                        "1: PLUS +   (next: UNKNOWN 2..5)",
                        "2: UNKNOWN 2..5   (next: UNKNOWN @)",
                        "3: UNKNOWN @   (next: EOF <EOF>)"),
                walk("1 + 2..5 @"));
    }

    @Test
    void emptyOrBlankExpressionIsError() {
        assertEquals(Messages.ERR_NOT_ENOUGH_ARGS, walk(""));
        assertEquals(Messages.ERR_NOT_ENOUGH_ARGS, walk("   "));
    }

    @Test
    void cliJoinsSplitArgumentsLikeOneExpression() {
        assertEquals(walk("2 + x"), run("walk-expr", "2", "+", "x"));
        assertEquals(walk("2 + x"), run("walk-expr", "2 + x"));
    }

    @Test
    void cliWithoutExpressionIsError() {
        assertEquals(Messages.ERR_NOT_ENOUGH_ARGS, run("walk-expr"));
    }

    @Test
    void walkMatchesTokenizeLineByLine() {
        // Регресія: walk-expr бачить ті самі токени, що й tokenize (Lab 4)
        String expression = "-5 * (x - 3.14)";
        String[] tokens = capture(() -> CommandDispatcher.dispatchText("tokenize", expression)).split("\n");
        String[] steps = walk(expression).split("\n");
        assertEquals(tokens.length, steps.length);
        for (int i = 0; i < tokens.length; i++) {
            assertTrue(steps[i].startsWith(tokens[i] + "   (next: "), "line " + i);
        }
    }
}
