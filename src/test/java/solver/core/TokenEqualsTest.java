package solver.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TokenEqualsTest {

    @Test
    void tokensWithSameTypeAndLexemeAreEqual() {
        Token a = new Token(TokenType.NUMBER, "2");
        Token b = new Token(TokenType.NUMBER, "2");
        assertNotSame(a, b);   // різні об'єкти (== дає false)
        assertEquals(a, b);    // але логічно рівні
    }

    @Test
    void differentLexemeMeansNotEqual() {
        assertNotEquals(new Token(TokenType.NUMBER, "2"), new Token(TokenType.NUMBER, "3"));
    }

    @Test
    void differentTypeMeansNotEqual() {
        assertNotEquals(new Token(TokenType.NUMBER, "2"), new Token(TokenType.IDENTIFIER, "2"));
    }

    @Test
    void numberLexemesAreComparedAsText() {
        assertNotEquals(new Token(TokenType.NUMBER, "2"), new Token(TokenType.NUMBER, "2.0"));
    }

    @Test
    void equalsIsReflexive() {
        Token a = new Token(TokenType.PLUS, "+");
        assertEquals(a, a);
    }

    @Test
    void equalsIsSymmetric() {
        Token a = new Token(TokenType.IDENTIFIER, "x");
        Token b = new Token(TokenType.IDENTIFIER, "x");
        assertTrue(a.equals(b));
        assertTrue(b.equals(a));
    }

    @Test
    void equalsIsTransitive() {
        Token a = new Token(TokenType.MINUS, "-");
        Token b = new Token(TokenType.MINUS, "-");
        Token c = new Token(TokenType.MINUS, "-");
        assertTrue(a.equals(b));
        assertTrue(b.equals(c));
        assertTrue(a.equals(c));
    }

    @Test
    void equalsNullIsFalseAndDoesNotThrow() {
        assertFalse(new Token(TokenType.NUMBER, "2").equals(null));
    }

    @Test
    void equalsOtherClassIsFalse() {
        assertFalse(new Token(TokenType.NUMBER, "2").equals("2"));
    }

    @Test
    void equalTokensHaveEqualHashCodes() {
        Token a = new Token(TokenType.NUMBER, "3.14");
        Token b = new Token(TokenType.NUMBER, "3.14");
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void hashCodeIsStableBetweenCalls() {
        Token a = new Token(TokenType.IDENTIFIER, "velocity");
        assertEquals(a.hashCode(), a.hashCode());
    }

    @Test
    void equalTokensCollapseInHashSet() {
        Set<Token> set = new HashSet<>();
        set.add(new Token(TokenType.IDENTIFIER, "x"));
        set.add(new Token(TokenType.IDENTIFIER, "x"));
        set.add(new Token(TokenType.NUMBER, "5"));
        assertEquals(2, set.size());
        assertTrue(set.contains(new Token(TokenType.NUMBER, "5")));
    }

    @Test
    void tokenArraysCanBeComparedElementWise() {
        // основа для регресійних тестів T4
        Token[] expected = {
                new Token(TokenType.NUMBER, "2"),
                new Token(TokenType.MULTIPLY, "*"),
                new Token(TokenType.IDENTIFIER, "x")
        };
        Token[] actual = {
                new Token(TokenType.NUMBER, "2"),
                new Token(TokenType.MULTIPLY, "*"),
                new Token(TokenType.IDENTIFIER, "x")
        };
        org.junit.jupiter.api.Assertions.assertArrayEquals(expected, actual);
    }
}