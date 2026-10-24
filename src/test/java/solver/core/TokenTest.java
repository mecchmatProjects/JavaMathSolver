package solver.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class TokenTest {

    @Test
    void gettersReturnConstructorValues() {
        Token token = new Token(TokenType.NUMBER, "3.14");
        assertEquals(TokenType.NUMBER, token.getType());
        assertEquals("3.14", token.getLexeme());
    }

    @Test
    void identifierKeepsItsLexeme() {
        assertEquals("x", new Token(TokenType.IDENTIFIER, "x").getLexeme());
    }

    @Test
    void singleCharacterOperatorWorks() {
        Token token = new Token(TokenType.PLUS, "+");
        assertEquals(TokenType.PLUS, token.getType());
        assertEquals("+", token.getLexeme());
    }

    @Test
    void numberLexemeStaysText() {
        assertEquals("2", new Token(TokenType.NUMBER, "2").getLexeme());
    }

    //C12 constructor validation

    @Test
    void validationNullTypeException(){
        assertThrows(
                NullPointerException.class,
                () -> new Token(null, "2"));
    }

    @Test
    void validationNullLexemeException(){
        assertThrows(
                NullPointerException.class,
                () -> new Token(TokenType.NUMBER, null));
    }

}
