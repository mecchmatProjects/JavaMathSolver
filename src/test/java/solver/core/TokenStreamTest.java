package solver.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TokenStreamTest {

    private static final Token EOF = new Token(TokenType.EOF, "<EOF>");

    private static Token[] sample() {
        // "2 + x"
        return new Token[] {
                new Token(TokenType.NUMBER, "2"),
                new Token(TokenType.PLUS, "+"),
                new Token(TokenType.IDENTIFIER, "x")
        };
    }

    // ---- constructor ----

    @Test
    void nullArrayIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new TokenStream(null));
    }

    @Test
    void nullElementIsRejected() {
        Token[] withNull = {new Token(TokenType.NUMBER, "2"), null};
        assertThrows(IllegalArgumentException.class, () -> new TokenStream(withNull));
    }

    @Test
    void constructorCopiesTheArray() {
        Token[] source = sample();
        TokenStream stream = new TokenStream(source);
        source[0] = new Token(TokenType.NUMBER, "999");
        assertEquals(new Token(TokenType.NUMBER, "2"), stream.current());
    }

    // ---- current / position ----

    @Test
    void newStreamStartsAtFirstToken() {
        TokenStream stream = new TokenStream(sample());
        assertEquals(0, stream.position());
        assertEquals(new Token(TokenType.NUMBER, "2"), stream.current());
    }

    @Test
    void currentDoesNotMoveThePosition() {
        TokenStream stream = new TokenStream(sample());
        stream.current();
        stream.current();
        assertEquals(0, stream.position());
    }

    // ---- peek ----

    @Test
    void peekReturnsNextTokenWithoutMoving() {
        TokenStream stream = new TokenStream(sample());
        assertEquals(new Token(TokenType.PLUS, "+"), stream.peek());
        assertEquals(0, stream.position());
        assertEquals(new Token(TokenType.NUMBER, "2"), stream.current());
    }

    @Test
    void peekOnLastTokenReturnsEof() {
        TokenStream stream = new TokenStream(sample());
        stream.next();
        stream.next();
        assertEquals(new Token(TokenType.IDENTIFIER, "x"), stream.current());
        assertEquals(EOF, stream.peek());
    }

    // ---- next / hasNext ----

    @Test
    void nextReturnsOldTokenAndAdvances() {
        TokenStream stream = new TokenStream(sample());
        assertEquals(new Token(TokenType.NUMBER, "2"), stream.next());
        assertEquals(1, stream.position());
        assertEquals(new Token(TokenType.PLUS, "+"), stream.current());
    }

    @Test
    void nextWalksTheWholeStreamInOrder() {
        Token[] expected = sample();
        TokenStream stream = new TokenStream(expected);
        int i = 0;
        while (stream.hasNext()) {
            assertEquals(expected[i], stream.next());
            i++;
        }
        assertEquals(expected.length, i);
    }

    @Test
    void hasNextBecomesFalseAfterLastToken() {
        TokenStream stream = new TokenStream(sample());
        assertTrue(stream.hasNext());
        stream.next();
        stream.next();
        assertTrue(stream.hasNext());
        stream.next();
        assertFalse(stream.hasNext());
    }

    @Test
    void currentAtTheEndIsEof() {
        TokenStream stream = new TokenStream(sample());
        while (stream.hasNext()) {
            stream.next();
        }
        assertEquals(EOF, stream.current());
    }

    @Test
    void nextAtTheEndReturnsEofAndDoesNotMovePast() {
        TokenStream stream = new TokenStream(sample());
        while (stream.hasNext()) {
            stream.next();
        }
        assertEquals(EOF, stream.next());
        assertEquals(EOF, stream.next());
        assertEquals(3, stream.position());
    }

    // ---- empty stream ----

    @Test
    void emptyStreamReturnsEofEverywhere() {
        TokenStream stream = new TokenStream(new Token[0]);
        assertFalse(stream.hasNext());
        assertEquals(EOF, stream.current());
        assertEquals(EOF, stream.peek());
        assertEquals(EOF, stream.next());
        assertEquals(0, stream.position());
    }

    // ---- reset ----

    @Test
    void resetReturnsToTheStart() {
        TokenStream stream = new TokenStream(sample());
        stream.next();
        stream.next();
        stream.reset();
        assertEquals(0, stream.position());
        assertEquals(new Token(TokenType.NUMBER, "2"), stream.current());
    }

    @Test
    void resetAfterFullPassAllowsSecondPass() {
        TokenStream stream = new TokenStream(sample());
        while (stream.hasNext()) {
            stream.next();
        }
        stream.reset();
        assertTrue(stream.hasNext());
        assertEquals(new Token(TokenType.NUMBER, "2"), stream.next());
    }

    // ---- EOF sentinel ----

    @Test
    void eofTokenHasEofTypeAndMarkerLexeme() {
        Token eof = new TokenStream(new Token[0]).current();
        assertEquals(TokenType.EOF, eof.getType());
        assertEquals("<EOF>", eof.getLexeme());
    }

    // ---- C17: peek(int) lookahead ----

    @Test
    void peekZeroIsTheCurrentToken() {
        TokenStream stream = new TokenStream(sample());
        assertEquals(stream.current(), stream.peek(0));
        stream.next();
        assertEquals(stream.current(), stream.peek(0));
    }

    @Test
    void peekWalksForwardFromTheCurrentPosition() {
        // приклад із методички: "2 + x * 5", current на 2
        TokenStream stream = new TokenStream(new Token[] {
                new Token(TokenType.NUMBER, "2"),
                new Token(TokenType.PLUS, "+"),
                new Token(TokenType.IDENTIFIER, "x"),
                new Token(TokenType.MULTIPLY, "*"),
                new Token(TokenType.NUMBER, "5")
        });
        assertEquals(new Token(TokenType.NUMBER, "2"), stream.peek(0));
        assertEquals(new Token(TokenType.PLUS, "+"), stream.peek(1));
        assertEquals(new Token(TokenType.IDENTIFIER, "x"), stream.peek(2));
        assertEquals(new Token(TokenType.MULTIPLY, "*"), stream.peek(3));
        assertEquals(new Token(TokenType.NUMBER, "5"), stream.peek(4));
        assertEquals(EOF, stream.peek(5));
    }

    @Test
    void peekIsRelativeToTheCurrentPosition() {
        TokenStream stream = new TokenStream(sample());
        stream.next(); // current = "+"
        assertEquals(new Token(TokenType.PLUS, "+"), stream.peek(0));
        assertEquals(new Token(TokenType.IDENTIFIER, "x"), stream.peek(1));
        assertEquals(EOF, stream.peek(2));
    }

    @Test
    void peekDoesNotMoveThePosition() {
        TokenStream stream = new TokenStream(sample());
        stream.next(); // position = 1
        for (int offset = 0; offset < 10; offset++) {
            stream.peek(offset);
        }
        assertEquals(1, stream.position());
        assertEquals(new Token(TokenType.PLUS, "+"), stream.current());
    }

    @Test
    void peekPastTheEndReturnsEof() {
        TokenStream stream = new TokenStream(sample());
        assertEquals(EOF, stream.peek(3));
        assertEquals(EOF, stream.peek(100));
        assertEquals(EOF, stream.peek(Integer.MAX_VALUE)); // position + offset переповнюється
        assertEquals(0, stream.position());
    }

    @Test
    void peekOnEmptyStreamIsAlwaysEof() {
        TokenStream stream = new TokenStream(new Token[0]);
        assertEquals(EOF, stream.peek(0));
        assertEquals(EOF, stream.peek(1));
    }

    @Test
    void negativeOffsetIsRejected() {
        TokenStream stream = new TokenStream(sample());
        assertThrows(IllegalArgumentException.class, () -> stream.peek(-1));
        assertThrows(IllegalArgumentException.class, () -> stream.peek(Integer.MIN_VALUE));
        assertEquals(0, stream.position(), "rejected peek must not move the cursor");
    }

    @Test
    void noArgPeekIsPeekOne() {
        TokenStream stream = new TokenStream(sample());
        assertEquals(stream.peek(1), stream.peek());
        stream.next();
        assertEquals(stream.peek(1), stream.peek());
        stream.next();
        assertEquals(stream.peek(1), stream.peek()); // обидва EOF у кінці
    }
}
