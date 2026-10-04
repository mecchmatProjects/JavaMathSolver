package solver.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static solver.core.ArgumentValidator.*;

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
}
