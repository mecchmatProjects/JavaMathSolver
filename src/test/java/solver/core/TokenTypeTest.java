package solver.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class TokenTypeTest {

    @Test
    void operatorTypeMapsPlus() {
        assertEquals(TokenType.PLUS, TokenType.operatorType('+'));
    }

    @Test
    void operatorTypeMapsMinus() {
        assertEquals(TokenType.MINUS, TokenType.operatorType('-'));
    }

    @Test
    void operatorTypeMapsMultiply() {
        assertEquals(TokenType.MULTIPLY, TokenType.operatorType('*'));
    }

    @Test
    void operatorTypeMapsDivide() {
        assertEquals(TokenType.DIVIDE, TokenType.operatorType('/'));
    }

    @Test
    void operatorTypeMapsPower() {
        assertEquals(TokenType.POWER, TokenType.operatorType('^'));
    }

    @Test
    void operatorTypeParenthesesAreNotOperators() {
        assertEquals(TokenType.UNKNOWN, TokenType.operatorType('('));
        assertEquals(TokenType.UNKNOWN, TokenType.operatorType(')'));
    }

    @Test
    void operatorTypeDigitsLettersAndSpacesAreUnknown() {
        assertEquals(TokenType.UNKNOWN, TokenType.operatorType('5'));
        assertEquals(TokenType.UNKNOWN, TokenType.operatorType('x'));
        assertEquals(TokenType.UNKNOWN, TokenType.operatorType('_'));
        assertEquals(TokenType.UNKNOWN, TokenType.operatorType(' '));
        assertEquals(TokenType.UNKNOWN, TokenType.operatorType('.'));
        assertEquals(TokenType.UNKNOWN, TokenType.operatorType('@'));
    }

    @Test
    void operatorTypeLookalikeUnicodeOperatorsAreUnknown() {
        // U+2212 (мінус з Word), U+00D7 (знак множення), U+00F7 (знак ділення)
        assertEquals(TokenType.UNKNOWN, TokenType.operatorType('\u2212'));
        assertEquals(TokenType.UNKNOWN, TokenType.operatorType('\u00d7'));
        assertEquals(TokenType.UNKNOWN, TokenType.operatorType('\u00f7'));
    }

    @Test
    void operatorTypeNeverReturnsNull() {
        for (char c = 0; c < 128; c++) {
            assertNotNull(TokenType.operatorType(c));
        }
    }

    @Test
    void enumContainsAllConstantsFromTheSpec() {
        String[] expected = {
            "NUMBER", "IDENTIFIER",
            "PLUS", "MINUS", "MULTIPLY", "DIVIDE", "POWER",
            "LEFT_PARENTHESIS", "RIGHT_PARENTHESIS",
            "UNKNOWN"
        };
        for (String name : expected) {
            assertEquals(name, TokenType.valueOf(name).name());
        }
    }
}
