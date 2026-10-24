package solver.core;

import java.util.Objects;

public final class Token {

    private final TokenType type;
    private final String lexeme;

    public Token(TokenType type, String lexeme) {
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.lexeme = Objects.requireNonNull(lexeme, "lexeme must not be null");
    }

    public TokenType getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }

    /**
     * Full format for logs and debugging.
     * Example: Token{type=NUMBER, lexeme='3.14'}
     */
    @Override
    public String toString() {
        return "Token{type=" + type + ", lexeme='" + lexeme + "'}";
    }

    /**
     * Compact format for CLI output.
     * Example: NUMBER(3.14)
     */
    public String toCompactString() {
        return type + "(" + lexeme + ")";
    }

    /**
     * Logical equality: same type and same lexeme.
     * Lexemes are compared as text, not as numbers ("2" != "2.0").
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Token)) { // instanceof is false for null as well
            return false;
        }
        Token other = (Token) o;
        return type == other.type && Objects.equals(lexeme, other.lexeme);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, lexeme);
    }
}