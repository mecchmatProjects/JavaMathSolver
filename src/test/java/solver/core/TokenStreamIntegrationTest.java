package solver.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * T5: інтеграція String -> Tokenizer -> Token[] -> TokenStream на реальних виразах.
 * Юніт-тести кожного методу лежать у TokenStreamTest / TokenStreamConsumeTest;
 * тут перевіряються сценарії, якими користуватиметься Parser.
 */
class TokenStreamIntegrationTest {

    private static final String SPEC = "2*x^2 + 3*x - 5";

    private static TokenStream streamOf(String expression) {
        return new TokenStream(Tokenizer.tokenize(expression));
    }

    private static Token tok(TokenType type, String lexeme) {
        return new Token(type, lexeme);
    }

    // ---- нормальний випадок: увесь вираз через consume ----

    @Test
    void consumeWalksTheWholeSpecExpression() {
        TokenStream s = streamOf(SPEC);

        assertEquals(tok(TokenType.NUMBER, "2"), s.consume(TokenType.NUMBER));
        assertEquals(tok(TokenType.MULTIPLY, "*"), s.consume(TokenType.MULTIPLY));
        assertEquals(tok(TokenType.IDENTIFIER, "x"), s.consume(TokenType.IDENTIFIER));
        assertEquals(tok(TokenType.POWER, "^"), s.consume(TokenType.POWER));
        assertEquals(tok(TokenType.NUMBER, "2"), s.consume(TokenType.NUMBER));
        assertEquals(tok(TokenType.PLUS, "+"), s.consume(TokenType.PLUS));
        assertEquals(tok(TokenType.NUMBER, "3"), s.consume(TokenType.NUMBER));
        assertEquals(tok(TokenType.MULTIPLY, "*"), s.consume(TokenType.MULTIPLY));
        assertEquals(tok(TokenType.IDENTIFIER, "x"), s.consume(TokenType.IDENTIFIER));
        assertEquals(tok(TokenType.MINUS, "-"), s.consume(TokenType.MINUS));
        assertEquals(tok(TokenType.NUMBER, "5"), s.consume(TokenType.NUMBER));

        assertFalse(s.hasNext());
        assertEquals(11, s.position());
        assertEquals(TokenType.EOF, s.consume(TokenType.EOF).getType());
        assertEquals(11, s.position(), "consume(EOF) must not move past the end");
    }

    @Test
    void consumeWithFunctionCallAndParentheses() {
        TokenStream s = streamOf("sin(x) + sqrt(y)");
        TokenType[] shape = {
                TokenType.IDENTIFIER, TokenType.LEFT_PARENTHESIS, TokenType.IDENTIFIER, TokenType.RIGHT_PARENTHESIS,
                TokenType.PLUS,
                TokenType.IDENTIFIER, TokenType.LEFT_PARENTHESIS, TokenType.IDENTIFIER, TokenType.RIGHT_PARENTHESIS,
                TokenType.EOF
        };
        for (TokenType expected : shape) {
            assertEquals(expected, s.consume(expected).getType());
        }
        assertEquals(9, s.position());
    }

    @Test
    void signedNumberIsOneTokenForConsume() {
        // "3 - -5" -> NUMBER(3) MINUS(-) NUMBER(-5): унарний мінус лишається в лексемі числа
        TokenStream s = streamOf("3 - -5");
        s.consume(TokenType.NUMBER);
        s.consume(TokenType.MINUS);
        assertEquals(tok(TokenType.NUMBER, "-5"), s.consume(TokenType.NUMBER));
        assertEquals(TokenType.EOF, s.current().getType());
    }

    // ---- lookahead із методички: "2 + x * 5", current на "2" ----

    @Test
    void lookaheadTableFromSpec() {
        TokenStream s = streamOf("2 + x * 5");

        assertEquals(tok(TokenType.NUMBER, "2"), s.current());
        assertEquals(tok(TokenType.PLUS, "+"), s.peek(1));
        assertEquals(tok(TokenType.IDENTIFIER, "x"), s.peek(2));
        assertEquals(tok(TokenType.MULTIPLY, "*"), s.peek(3));
        assertEquals(tok(TokenType.NUMBER, "5"), s.peek(4));
        assertEquals(TokenType.EOF, s.peek(5).getType());
        assertEquals(0, s.position());
    }

    @Test
    void lookaheadDecisionLikeParser() {
        // Parser-стиль: IDENTIFIER + "(" -> виклик функції, IDENTIFIER без "(" -> змінна
        TokenStream s = streamOf("f(x) + y");
        int calls = 0;
        int variables = 0;
        while (s.hasNext()) {
            Token t = s.next();
            if (t.getType() == TokenType.IDENTIFIER) {
                if (s.current().getType() == TokenType.LEFT_PARENTHESIS) {
                    calls++;
                } else {
                    variables++;
                }
            }
        }
        assertEquals(1, calls);
        assertEquals(2, variables); // x, y
    }

    // ---- граничні випадки ----

    @Test
    void emptyAndBlankExpressionGiveOnlyEof() {
        for (String expression : new String[] {"", "   ", "\t"}) {
            TokenStream s = streamOf(expression);
            assertFalse(s.hasNext());
            assertEquals(TokenType.EOF, s.consume(TokenType.EOF).getType());
            assertEquals(0, s.position());
        }
    }

    @Test
    void singleTokenExpression() {
        TokenStream s = streamOf("x");
        assertEquals(TokenType.EOF, s.peek().getType());
        s.consume(TokenType.IDENTIFIER);
        assertEquals(TokenType.EOF, s.current().getType());
        assertEquals(1, s.position());
    }

    @Test
    void resetAfterFullConsumeAllowsSecondIdenticalPass() {
        TokenStream s = streamOf(SPEC);
        Token[] first = new Token[11];
        for (int i = 0; i < first.length; i++) {
            first[i] = s.next();
        }
        assertFalse(s.hasNext());

        s.reset();
        assertEquals(0, s.position());
        for (Token expected : first) {
            assertEquals(expected, s.consume(expected.getType()));
        }
        assertEquals(TokenType.EOF, s.current().getType());
    }

    // ---- некоректні випадки ----

    @Test
    void wrongTokenMidExpressionHasExactMessageAndPosition() {
        TokenStream s = streamOf(SPEC);
        s.consume(TokenType.NUMBER);
        s.consume(TokenType.MULTIPLY);

        ParseException e = assertThrows(ParseException.class, () -> s.consume(TokenType.NUMBER));

        assertEquals(2, e.getPosition());
        assertEquals(String.format(Messages.ERR_UNEXPECTED_TOKEN, TokenType.NUMBER, TokenType.IDENTIFIER, "x", 2),
                e.getMessage());
        assertEquals("Error: Expected NUMBER but found IDENTIFIER('x') at position 2", e.getMessage());
        assertEquals(2, s.position(), "failed consume must not move the cursor");
    }

    @Test
    void expectingMoreAfterTheEndReportsEof() {
        TokenStream s = streamOf("x +");
        s.consume(TokenType.IDENTIFIER);
        s.consume(TokenType.PLUS);

        ParseException e = assertThrows(ParseException.class, () -> s.consume(TokenType.IDENTIFIER));
        assertEquals(2, e.getPosition());
        assertTrue(e.getMessage().contains("EOF"));
    }

    @Test
    void unknownTokenStaysInStreamAndBreaksConsume() {
        // NUMBER(2) PLUS(+) UNKNOWN(@)
        TokenStream s = streamOf("2 + @");
        s.consume(TokenType.NUMBER);
        s.consume(TokenType.PLUS);
        assertEquals(tok(TokenType.UNKNOWN, "@"), s.current());

        ParseException e = assertThrows(ParseException.class, () -> s.consume(TokenType.NUMBER));
        assertEquals(2, e.getPosition());
        assertTrue(e.getMessage().contains("UNKNOWN('@')"));
    }

    @Test
    void streamIsIndependentFromTokenizerArray() {
        Token[] tokens = Tokenizer.tokenize("1 + 2");
        TokenStream s = new TokenStream(tokens);
        tokens[0] = tok(TokenType.IDENTIFIER, "hacked");
        assertEquals(tok(TokenType.NUMBER, "1"), s.current());
    }
}
