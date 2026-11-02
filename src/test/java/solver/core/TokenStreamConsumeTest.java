package solver.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class TokenStreamConsumeTest {

    private static TokenStream sample() {
        // "2 + x"
        return new TokenStream(new Token[] {
                new Token(TokenType.NUMBER, "2"),
                new Token(TokenType.PLUS, "+"),
                new Token(TokenType.IDENTIFIER, "x")
        });
    }

    @Test
    void matchingTypeReturnsTokenAndAdvances() {
        TokenStream s = sample();
        assertEquals(new Token(TokenType.NUMBER, "2"), s.consume(TokenType.NUMBER));
        assertEquals(1, s.position());
    }

    @Test
    void consumeWholeStream() {
        TokenStream s = sample();
        s.consume(TokenType.NUMBER);
        s.consume(TokenType.PLUS);
        assertEquals(new Token(TokenType.IDENTIFIER, "x"), s.consume(TokenType.IDENTIFIER));
        assertEquals(3, s.position());
    }

    @Test
    void consumeEofAtTheEnd() {
        TokenStream s = new TokenStream(new Token[0]);
        assertEquals(TokenType.EOF, s.consume(TokenType.EOF).getType());
        assertEquals(0, s.position());
    }

    @Test
    void mismatchThrowsWithPositionAndDoesNotMove() {
        TokenStream s = sample();
        s.consume(TokenType.NUMBER);
        ParseException e = assertThrows(ParseException.class, () -> s.consume(TokenType.NUMBER));
        assertEquals(1, e.getPosition());
        assertEquals(1, s.position());
        assertEquals("Error: Expected NUMBER but found PLUS('+') at position 1", e.getMessage());
    }

    @Test
    void repeatedFailureKeepsPosition() {
        TokenStream s = sample();
        assertThrows(ParseException.class, () -> s.consume(TokenType.PLUS));
        assertThrows(ParseException.class, () -> s.consume(TokenType.PLUS));
        assertEquals(0, s.position());
    }

    @Test
    void emptyStreamMismatchReportsEof() {
        TokenStream s = new TokenStream(new Token[0]);
        ParseException e = assertThrows(ParseException.class, () -> s.consume(TokenType.NUMBER));
        assertEquals(0, e.getPosition());
        assertEquals("Error: Expected NUMBER but found EOF('<EOF>') at position 0", e.getMessage());
    }

    @Test
    void nullExpectedTypeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> sample().consume(null));
    }
}
