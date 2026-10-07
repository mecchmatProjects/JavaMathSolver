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

    // ===================================================================
    // CORE-13 — parseNumber: граничні випадки
    // ===================================================================

    @Test
    void explicitPlusSignAndSurroundingSpaces() {
        assertEquals(5L, NumberParser.parseNumber("+5"));
        assertEquals(7L, NumberParser.parseNumber("  7  "));
        assertEquals(-0.5, NumberParser.parseNumber("-.5"));
        assertEquals(1.0, NumberParser.parseNumber("1."));
    }

    @Test
    void scientificFormatIsAlwaysDouble() {
        assertEquals(1000.0, NumberParser.parseNumber("1e3"));
        assertEquals(1000.0, NumberParser.parseNumber("1E3"));
        assertEquals(1.5e-5, NumberParser.parseNumber("1.5e-5"));
        assertEquals(200.0, NumberParser.parseNumber("2e+2"));
        assertInstanceOf(Double.class, NumberParser.parseNumber("1e3"));
    }

    @Test
    void longBoundary() {
        assertEquals(Long.MAX_VALUE, NumberParser.parseNumber("9223372036854775807"));
        assertEquals(Long.MIN_VALUE, NumberParser.parseNumber("-9223372036854775808"));
        assertInstanceOf(Double.class, NumberParser.parseNumber("9223372036854775808")); // MAX + 1
    }

    @Test
    void rejectsIncompleteOrJavaSpecificFormats() {
        assertNull(NumberParser.parseNumber(""));
        assertNull(NumberParser.parseNumber("   "));
        assertNull(NumberParser.parseNumber("-"));
        assertNull(NumberParser.parseNumber("."));
        assertNull(NumberParser.parseNumber("1e"));
        assertNull(NumberParser.parseNumber("e5"));
        assertNull(NumberParser.parseNumber("--5"));
        assertNull(NumberParser.parseNumber("1.2.3"));
        assertNull(NumberParser.parseNumber("1_000"));
        assertNull(NumberParser.parseNumber("1d"));   // Double.parseDouble це б прийняв
        assertNull(NumberParser.parseNumber("1f"));
        assertNull(NumberParser.parseNumber("-1e400"));
    }

    // ===================================================================
    // CORE-13 — toLongExact: межі
    // ===================================================================

    @Test
    void toLongExactBoundaries() {
        assertEquals(Long.valueOf(0L), NumberParser.toLongExact(-0.0));
        assertEquals(Long.valueOf(-5L), NumberParser.toLongExact(-5.0));
        assertEquals(Long.valueOf(Long.MIN_VALUE), NumberParser.toLongExact(Long.MIN_VALUE));
        assertNull(NumberParser.toLongExact(1e19));  // поза long
        assertNull(NumberParser.toLongExact(0x1p63));
    }

    // ===================================================================
    // CORE-13 — parseArguments / toDoubleArray
    // ===================================================================

    @Test
    void parseArgumentsSkipsCommandName() {
        assertArrayEquals(new Number[] {2L, 3.5, 1e-5},
                NumberParser.parseArguments(new String[] {"add", "2", "3.5", "1e-5"}));
    }

    @Test
    void parseArgumentsWithoutNumbersGivesEmptyArray() {
        assertArrayEquals(new Number[0], NumberParser.parseArguments(new String[] {"help"}));
    }

    @Test
    void parseArgumentsStopsAtFirstInvalidAndReportsIt() {
        String[] args = {"add", "1", "abc", "x"};
        String out = solver.testutil.StdOut.capture(() -> assertNull(NumberParser.parseArguments(args)));
        assertEquals("Invalid number: abc", out);
    }

    @Test
    void toDoubleArrayConvertsMixedNumbers() {
        assertArrayEquals(new double[] {2.0, 3.5, -1.0},
                NumberParser.toDoubleArray(new Number[] {2L, 3.5, -1L}));
        assertArrayEquals(new double[0], NumberParser.toDoubleArray(new Number[0]));
    }
}
