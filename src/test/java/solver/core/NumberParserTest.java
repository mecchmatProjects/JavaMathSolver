package solver.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NumberParserTest {

    @Test
    void parsesIntegerAsLong() {
        assertEquals(42L, NumberParser.parseNumber("42"));
        assertEquals(-7L, NumberParser.parseNumber("-7"));
    }

    @Test
    void parsesDecimalAndScientificAsDouble() {
        assertEquals(3.14, NumberParser.parseNumber("3.14"));
        assertEquals(1e-5, NumberParser.parseNumber("1e-5"));
        assertEquals(0.5, NumberParser.parseNumber(".5"));
    }

    @Test
    void hugeIntegerFallsBackToDouble() {
        assertEquals(1e20, NumberParser.parseNumber("100000000000000000000"));
    }

    @Test
    void rejectsInvalidFormats() {
        assertNull(NumberParser.parseNumber("abc"));
        assertNull(NumberParser.parseNumber("1,5"));
        assertNull(NumberParser.parseNumber("NaN"));
        assertNull(NumberParser.parseNumber("Infinity"));
        assertNull(NumberParser.parseNumber("1e400"));
        assertNull(NumberParser.parseNumber("0x10"));
    }

    @Test
    void toLongExactAcceptsOnlyIntegers() {
        assertEquals(Long.valueOf(5L), NumberParser.toLongExact(5L));
        assertEquals(Long.valueOf(1000L), NumberParser.toLongExact(1e3));
        assertNull(NumberParser.toLongExact(2.5));
    }
}
