package solver.core;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static solver.testutil.StdOut.capture;

class OutputTest {

    // ===================================================================
    // CORE-14 — printResult(double)
    // ===================================================================

    @Test
    void doubleHasSixDecimalPlaces() {
        assertEquals("Result: 5.000000", capture(() -> Output.printResult(5.0)));
        assertEquals("Result: 3.141593", capture(() -> Output.printResult(Math.PI)));
        assertEquals("Result: -2.500000", capture(() -> Output.printResult(-2.5)));
    }

    @Test
    void negativeZeroIsPrintedAsZero() {
        assertEquals("Result: 0.000000", capture(() -> Output.printResult(-0.0)));
    }

    @Test
    void tinyNegativeThatRoundsToZeroHasNoSign() {
        // голий %f дав би "-0.000000" (напр. taylor-sin 3.14159265358979 1e-12)
        assertEquals("Result: 0.000000", capture(() -> Output.printResult(-1e-9)));
        assertEquals("Result: 0.000000", capture(() -> Output.printResult(-4.9e-7)));
        assertEquals("Result: -0.000001", capture(() -> Output.printResult(-5e-7))); // уже не нуль
    }

    @Test
    void nonFiniteDoubleIsInvalidInput() {
        assertEquals(Messages.ERR_INVALID_INPUT, capture(() -> Output.printResult(Double.NaN)));
        assertEquals(Messages.ERR_INVALID_INPUT, capture(() -> Output.printResult(Double.POSITIVE_INFINITY)));
        assertEquals(Messages.ERR_INVALID_INPUT, capture(() -> Output.printResult(Double.NEGATIVE_INFINITY)));
    }

    @Test
    void decimalSeparatorIsAlwaysDot() {
        Locale saved = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("uk-UA")); // у цій локалі роздільник — кома
            assertEquals("Result: 1.500000", capture(() -> Output.printResult(1.5)));
        } finally {
            Locale.setDefault(saved);
        }
    }

    // ===================================================================
    // CORE-14 — інші перевантаження
    // ===================================================================

    @Test
    void longIsPrintedWithoutDecimals() {
        assertEquals("Result: 120", capture(() -> Output.printResult(120L)));
        assertEquals("Result: -9223372036854775808", capture(() -> Output.printResult(Long.MIN_VALUE)));
    }

    @Test
    void booleanIsPrintedAsTrueOrFalse() {
        assertEquals("Result: true", capture(() -> Output.printResult(true)));
        assertEquals("Result: false", capture(() -> Output.printResult(false)));
    }

    @Test
    void stringIsPrintedVerbatim() {
        assertEquals("Result: No real roots", capture(() -> Output.printResult("No real roots")));
        assertEquals("Result: 100%", capture(() -> Output.printResult("100%"))); // % не ламає формат
    }

    // ===================================================================
    // CORE-15 — помилки
    // ===================================================================

    @Test
    void printErrorPrintsMessageAsIs() {
        assertEquals(Messages.ERR_DIVISION_BY_ZERO, capture(() -> Output.printError(Messages.ERR_DIVISION_BY_ZERO)));
    }

    @Test
    void unknownCommandPrintsNameAndHint() {
        assertEquals("Unknown command: hello\n" + Messages.HINT_HELP,
                capture(() -> Output.printUnknownCommand("hello")));
    }
}
