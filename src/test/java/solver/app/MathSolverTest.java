package solver.app;

import org.junit.jupiter.api.Test;

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
}
