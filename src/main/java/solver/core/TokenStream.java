package solver.core;

/**
 * A forward-moving cursor over an array of {@link Token}s.
 *
 * <p>Answers the question "where are we now and what comes next?".
 * Past the end of the stream every access returns the {@link TokenType#EOF}
 * sentinel instead of {@code null} or an exception, so a parser can simply check
 * {@code current().getType() == TokenType.EOF}.
 */
public final class TokenStream {

    private static final Token EOF_TOKEN = new Token(TokenType.EOF, "<EOF>");

    private final Token[] tokens;
    private int position;

    /**
     * Creates a stream over a copy of the given tokens.
     *
     * @throws IllegalArgumentException if the array or any of its elements is null
     */
    public TokenStream(Token[] tokens) {
        if (tokens == null) {
            throw new IllegalArgumentException("Tokens array must not be null");
        }
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i] == null) {
                throw new IllegalArgumentException("Token at index " + i + " must not be null");
            }
        }
        this.tokens = tokens.clone(); // Token is immutable, so a shallow copy is enough
        this.position = 0;
    }

    /** Returns the token at the current position, or EOF past the end. */
    public Token current() {
        return tokenAt(position);
    }

    /** Returns the token after the current one without moving; EOF past the end. */
    public Token peek() {
        return peek(1);
    }

    /**
     * Looks ahead without moving the cursor: {@code peek(0)} is {@link #current()},
     * {@code peek(1)} the token after it, and so on. Past the end of the stream
     * the {@link TokenType#EOF} sentinel is returned instead of an exception.
     *
     * @param offset how far ahead to look, counted from the current position
     * @throws IllegalArgumentException if {@code offset} is negative
     */
    public Token peek(int offset) {
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must not be negative: " + offset);
        }
        return tokenAt(position + offset);
    }

    /**
     * Returns the current token and moves forward by one.
     * At the end of the stream returns EOF and does not move.
     */
    public Token next() {
        Token result = current();
        if (position < tokens.length) {
            position++;
        }
        return result;
    }

    /** True while there is at least one real token left (EOF does not count). */
    public boolean hasNext() {
        return position < tokens.length;
    }

    /** Moves the cursor back to the first token. */
    public void reset() {
        position = 0;
    }

    /** Current position (0-based); equals the token count at the end of the stream. */
    public int position() {
        return position;
    }

    private Token tokenAt(int index) {
        if (index >= 0 && index < tokens.length) {
            return tokens[index];
        }
        return EOF_TOKEN;
    }
}
