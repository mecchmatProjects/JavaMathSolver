package solver.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static solver.core.ArgumentValidator.*;
import static solver.testutil.StdOut.capture;

class ArgumentValidatorTest {

    @Test
    void exactArity() {
        assertTrue(validateArgs(2, 2));
        assertFalse(validateArgs(1, 2));
        assertFalse(validateArgs(3, 2));
        assertTrue(validateArgs(0, 0));
    }

    @Test
    void polygonNeedsThreeVerticesAndPairs() {
        assertFalse(validateArgs(4, VARIADIC_POLYGON));
        assertTrue(validateArgs(6, VARIADIC_POLYGON));
        assertFalse(validateArgs(7, VARIADIC_POLYGON));
        assertTrue(validateArgs(8, VARIADIC_POLYGON));
    }

    @Test
    void pointsNeedPairs() {
        assertFalse(validateArgs(0, VARIADIC_POINTS));
        assertFalse(validateArgs(1, VARIADIC_POINTS));
        assertTrue(validateArgs(2, VARIADIC_POINTS));
        assertFalse(validateArgs(5, VARIADIC_POINTS));
        assertTrue(validateArgs(10, VARIADIC_POINTS));
    }

    @Test
    void numbersAndTextNeedAtLeastOne() {
        assertFalse(validateArgs(0, VARIADIC_NUMBERS));
        assertTrue(validateArgs(1, VARIADIC_NUMBERS));
        assertTrue(validateArgs(100, VARIADIC_NUMBERS));
        assertFalse(validateArgs(0, VARIADIC_TEXT));
        assertTrue(validateArgs(3, VARIADIC_TEXT));
    }

    // ===================================================================
    // CORE-12 / CORE-15 — яке повідомлення друкується
    // ===================================================================

    @Test
    void successPrintsNothing() {
        assertEquals("", capture(() -> assertTrue(validateArgs(2, 2))));
        assertEquals("", capture(() -> assertTrue(validateArgs(6, VARIADIC_POLYGON))));
    }

    @Test
    void tooFewAndTooManyHaveDifferentMessages() {
        assertEquals(Messages.ERR_NOT_ENOUGH_ARGS, capture(() -> validateArgs(1, 2)));
        assertEquals(Messages.ERR_TOO_MANY_ARGS, capture(() -> validateArgs(3, 2)));
        assertEquals(Messages.ERR_TOO_MANY_ARGS, capture(() -> validateArgs(1, 0))); // help 1
    }

    @Test
    void variadicMessages() {
        assertEquals(Messages.ERR_NOT_ENOUGH_ARGS, capture(() -> validateArgs(4, VARIADIC_POLYGON)));
        assertEquals(Messages.ERR_ODD_COORDINATES, capture(() -> validateArgs(7, VARIADIC_POLYGON)));
        assertEquals(Messages.ERR_ODD_COORDINATES, capture(() -> validateArgs(3, VARIADIC_POINTS)));
        assertEquals(Messages.ERR_NOT_ENOUGH_ARGS, capture(() -> validateArgs(0, VARIADIC_TEXT)));
    }

    @Test
    void tooFewIsCheckedBeforeParity() {
        // 5 чисел для багатокутника: і замало, і непарно — головне "замало"
        assertEquals(Messages.ERR_NOT_ENOUGH_ARGS, capture(() -> validateArgs(5, VARIADIC_POLYGON)));
    }

    @Test
    void minimumConstantsMatchRegulation() {
        assertEquals(6, POLYGON_MIN_ARGS);
        assertEquals(2, POINTS_MIN_ARGS);
        assertEquals(-1, UNKNOWN_COMMAND);
        assertEquals(-5, VARIADIC_TEXT);
    }
}
