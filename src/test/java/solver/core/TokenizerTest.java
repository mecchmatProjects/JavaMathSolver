package solver.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenizerTest {

    @Test
    void stripsSpacesFromExpression() {
        assertArrayEquals(new char[] {'2', '*', 'x', '+', '3'}, Tokenizer.toCharacters("2*x + 3"));
    }

    @Test
    void keepsParenthesesAndLetters() {
        assertArrayEquals(new char[] {'s', 'i', 'n', '(', 'x', ')'}, Tokenizer.toCharacters("sin(x)"));
    }

    @Test
    void emptyStringGivesEmptyArray() {
        assertArrayEquals(new char[] {}, Tokenizer.toCharacters(""));
    }

    @Test
    void blankStringGivesEmptyArray() {
        assertArrayEquals(new char[] {}, Tokenizer.toCharacters(" "));
        assertArrayEquals(new char[] {}, Tokenizer.toCharacters("   \t  "));
    }

    @Test
    void stripsTabsAndRepeatedSpaces() {
        assertArrayEquals(new char[] {'1', '+', '2'}, Tokenizer.toCharacters("  1\t+   2 "));
    }

    @Test
    void keepsExpressionWithoutSpacesUnchanged() {
        assertArrayEquals(new char[] {'2', '*', 'x', '^', '2'}, Tokenizer.toCharacters("2*x^2"));
    }

    @Test
    void nullExpressionIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> Tokenizer.toCharacters(null));
    }
}
