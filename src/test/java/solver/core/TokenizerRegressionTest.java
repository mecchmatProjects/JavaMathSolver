package solver.core;

import org.junit.jupiter.api.Test;
import solver.app.MathSolver;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static solver.core.TokenType.*;
import static solver.testutil.StdOut.capture;

/**
 * T4: регресія Tokenizer після переходу на {@code Token[]} (Lab 4).
 *
 * <p>Корпус — вирази з Lab 3 (коректні й некоректні). Для кожного виразу
 * результат {@link Tokenizer#tokenize} порівнюється з очікуваним {@code Token[]}
 * через {@link Token#equals}, а решта API (C8 {@code isValid}, C9
 * {@code tokenStatistics}, C10 CLI {@code tokenize}) перевіряється на
 * узгодженість із тим самим {@code Token[]}.
 */
class TokenizerRegressionTest {

    private static Token t(TokenType type, String lexeme) {
        return new Token(type, lexeme);
    }

    private static final class Case {
        final String expression;
        final Token[] expected;

        Case(String expression, Token... expected) {
            this.expression = expression;
            this.expected = expected;
        }
    }

    /** Корпус Lab 3: вираз → очікуваний Token[]. */
    private static final Case[] CORPUS = {
            new Case("2*x^2 + 3*x - 5",
                    t(NUMBER, "2"), t(MULTIPLY, "*"), t(IDENTIFIER, "x"), t(POWER, "^"), t(NUMBER, "2"),
                    t(PLUS, "+"), t(NUMBER, "3"), t(MULTIPLY, "*"), t(IDENTIFIER, "x"),
                    t(MINUS, "-"), t(NUMBER, "5")),
            new Case("2*x + 3",
                    t(NUMBER, "2"), t(MULTIPLY, "*"), t(IDENTIFIER, "x"), t(PLUS, "+"), t(NUMBER, "3")),
            new Case("sin(x) + sqrt(y)",
                    t(IDENTIFIER, "sin"), t(LEFT_PARENTHESIS, "("), t(IDENTIFIER, "x"), t(RIGHT_PARENTHESIS, ")"),
                    t(PLUS, "+"),
                    t(IDENTIFIER, "sqrt"), t(LEFT_PARENTHESIS, "("), t(IDENTIFIER, "y"), t(RIGHT_PARENTHESIS, ")")),
            new Case("12.5*x",
                    t(NUMBER, "12.5"), t(MULTIPLY, "*"), t(IDENTIFIER, "x")),
            new Case("-5 * (x - 3.14)",
                    t(NUMBER, "-5"), t(MULTIPLY, "*"), t(LEFT_PARENTHESIS, "("), t(IDENTIFIER, "x"),
                    t(MINUS, "-"), t(NUMBER, "3.14"), t(RIGHT_PARENTHESIS, ")")),
            new Case("3 - -5",
                    t(NUMBER, "3"), t(MINUS, "-"), t(NUMBER, "-5")),
            new Case("2*-3",
                    t(NUMBER, "2"), t(MULTIPLY, "*"), t(NUMBER, "-3")),
            new Case("x-1",
                    t(IDENTIFIER, "x"), t(MINUS, "-"), t(NUMBER, "1")),
            new Case("10 / (2 ^ 3)",
                    t(NUMBER, "10"), t(DIVIDE, "/"), t(LEFT_PARENTHESIS, "("), t(NUMBER, "2"),
                    t(POWER, "^"), t(NUMBER, "3"), t(RIGHT_PARENTHESIS, ")")),
            new Case("(a+b)/c",
                    t(LEFT_PARENTHESIS, "("), t(IDENTIFIER, "a"), t(PLUS, "+"), t(IDENTIFIER, "b"),
                    t(RIGHT_PARENTHESIS, ")"), t(DIVIDE, "/"), t(IDENTIFIER, "c")),
            new Case("x1 + velocity",
                    t(IDENTIFIER, "x1"), t(PLUS, "+"), t(IDENTIFIER, "velocity")),
            new Case("a_b*_c",
                    t(IDENTIFIER, "a_b"), t(MULTIPLY, "*"), t(IDENTIFIER, "_c")),
            new Case("2x",
                    t(NUMBER, "2"), t(IDENTIFIER, "x")),
            new Case("  1\t+   2 ",
                    t(NUMBER, "1"), t(PLUS, "+"), t(NUMBER, "2")),
            // некоректні лексеми Lab 3: текст зберігається, тип UNKNOWN
            new Case("2..5",
                    t(UNKNOWN, "2..5")),
            new Case("x@",
                    t(IDENTIFIER, "x"), t(UNKNOWN, "@")),
            new Case("3.4.5",
                    t(UNKNOWN, "3.4.5")),
            new Case("2..5 + x@",
                    t(UNKNOWN, "2..5"), t(PLUS, "+"), t(IDENTIFIER, "x"), t(UNKNOWN, "@")),
            // порожній вираз і самі пробіли не дають токенів
            new Case(""),
            new Case(" \t "),
    };

    @Test
    void corpusMatchesExpectedTokens() {
        for (Case c : CORPUS) {
            assertArrayEquals(c.expected, Tokenizer.tokenize(c.expression),
                    "tokenize(\"" + c.expression + "\")");
        }
    }

    @Test
    void lexemesConcatenateBackToExpressionWithoutWhitespace() {
        // жоден символ не губиться і не дублюється при розбитті
        for (Case c : CORPUS) {
            StringBuilder joined = new StringBuilder();
            for (Token token : Tokenizer.tokenize(c.expression)) {
                joined.append(token.getLexeme());
            }
            assertEquals(c.expression.replaceAll("[ \\t]", ""), joined.toString(),
                    "lexemes of \"" + c.expression + "\"");
        }
    }

    @Test
    void isValidAgreesWithTokens() {
        // C8: вираз коректний ⇔ він непорожній і в ньому немає UNKNOWN
        for (Case c : CORPUS) {
            boolean hasUnknown = false;
            for (Token token : c.expected) {
                hasUnknown |= token.getType() == UNKNOWN;
            }
            boolean expected = c.expected.length > 0 && !hasUnknown;
            assertEquals(expected, Tokenizer.isValid(c.expression),
                    "isValid(\"" + c.expression + "\")");
        }
    }

    @Test
    void tokenStatisticsAgreesWithTokens() {
        // C9: статистика рахується з того самого Token[], UNKNOWN ігнорується
        for (Case c : CORPUS) {
            int[] expected = new int[4];
            for (Token token : c.expected) {
                switch (token.getType()) {
                    case NUMBER -> expected[0]++;
                    case IDENTIFIER -> expected[1]++;
                    case PLUS, MINUS, MULTIPLY, DIVIDE, POWER -> expected[2]++;
                    case LEFT_PARENTHESIS, RIGHT_PARENTHESIS -> expected[3]++;
                    default -> { }
                }
            }
            assertArrayEquals(expected, Tokenizer.tokenStatistics(c.expression),
                    "tokenStatistics(\"" + c.expression + "\")");
        }
    }

    @Test
    void tokenStatisticsSpecExampleCountsFourOperators() {
        // у методичці вказано 3, але "*", "+", "*", "-" — це 4 оператори
        assertArrayEquals(new int[] {2, 4, 4, 2}, Tokenizer.tokenStatistics("2*x + 3*x - sin(x)"));
    }

    @Test
    void tokenizeIsDeterministicAndReturnsFreshArray() {
        Token[] first = Tokenizer.tokenize("2*x + 3");
        Token[] second = Tokenizer.tokenize("2*x + 3");
        assertArrayEquals(first, second);
        assertNotSame(first, second);

        // зміна отриманого масиву не впливає на наступні виклики
        first[0] = t(UNKNOWN, "?");
        assertEquals(t(NUMBER, "2"), Tokenizer.tokenize("2*x + 3")[0]);
    }

    @Test
    void equalTokensFromDifferentPositionsAreLogicallyEqual() {
        // C14: однакові токени в різних місцях виразу рівні, але це різні об'єкти
        Token[] tokens = Tokenizer.tokenize("x + x + x");
        assertNotSame(tokens[0], tokens[2]);
        assertEquals(tokens[0], tokens[2]);
        assertEquals(tokens[0].hashCode(), tokens[2].hashCode());

        Set<Token> distinct = new HashSet<>();
        for (Token token : tokens) {
            distinct.add(token);
        }
        assertEquals(Set.of(t(IDENTIFIER, "x"), t(PLUS, "+")), distinct);
    }

    @Test
    void numberLexemesAreComparedAsTextNotValue() {
        assertNotEquals(Tokenizer.tokenize("2")[0], Tokenizer.tokenize("2.0")[0]);
        assertNotEquals(Tokenizer.tokenize("007")[0], Tokenizer.tokenize("7")[0]);
    }

    @Test
    void cliTokenizeOutputUnchangedSinceLab3() {
        // C10: формат "<index>: <TYPE> <lexeme>" не змінився після переходу на Token[]
        String expected = String.join("\n",
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
                "10: NUMBER 5");
        assertEquals(expected, capture(() -> MathSolver.main(new String[] {"tokenize", "2*x^2 + 3*x - 5"})));
    }

    @Test
    void cliTokenizePrintsOneLinePerToken() {
        for (Case c : CORPUS) {
            if (c.expected.length == 0) {
                continue; // порожній вираз дає помилку, а не токени
            }
            String output = capture(() -> MathSolver.main(new String[] {"tokenize", c.expression}));
            String[] lines = output.split("\n");
            assertEquals(c.expected.length, lines.length, "CLI lines for \"" + c.expression + "\"");
            for (int i = 0; i < lines.length; i++) {
                assertTrue(lines[i].startsWith(i + ": " + c.expected[i].getType() + " "),
                        "line " + i + " for \"" + c.expression + "\": " + lines[i]);
            }
        }
    }
}
