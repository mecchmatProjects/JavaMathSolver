package solver.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TokenToStringTest {

    @Test
    void toStringUsesFullFormatForNumber() {
        Token token = new Token(TokenType.NUMBER, "3.14");
        assertEquals("Token{type=NUMBER, lexeme='3.14'}", token.toString());
    }

    @Test
    void toStringUsesFullFormatForIdentifier() {
        Token token = new Token(TokenType.IDENTIFIER, "x");
        assertEquals("Token{type=IDENTIFIER, lexeme='x'}", token.toString());
    }

    @Test
    void toStringWorksForOperator() {
        Token token = new Token(TokenType.PLUS, "+");
        assertEquals("Token{type=PLUS, lexeme='+'}", token.toString());
    }

    @Test
    void compactStringForNumber() {
        assertEquals("NUMBER(3.14)", new Token(TokenType.NUMBER, "3.14").toCompactString());
    }

    @Test
    void compactStringForOperators() {
        assertEquals("MULTIPLY(*)", new Token(TokenType.MULTIPLY, "*").toCompactString());
        assertEquals("LEFT_PARENTHESIS(()", new Token(TokenType.LEFT_PARENTHESIS, "(").toCompactString());
    }

    @Test
    void compactStringForUnknownToken() {
        assertEquals("UNKNOWN(@)", new Token(TokenType.UNKNOWN, "@").toCompactString());
    }

    @Test
    void formatsAreDifferent() {
        Token token = new Token(TokenType.IDENTIFIER, "velocity");
        assertEquals("IDENTIFIER(velocity)", token.toCompactString());
        assertEquals("Token{type=IDENTIFIER, lexeme='velocity'}", token.toString());
    }
}
