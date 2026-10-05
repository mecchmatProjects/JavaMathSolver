package solver.core;

import java.util.Locale;
import static solver.core.Messages.*;

/**
 * CORE-14: єдина точка виводу результатів і помилок.
 */
public final class Output {

    private Output() {
    }

    // CORE-14: єдиний формат виводу
    public static void printResult(double value) {
        if (!Double.isFinite(value)) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        if (value == 0.0) {
            value = 0.0; // -0.0 -> 0.0
        }
        System.out.printf(Locale.ROOT, "Result: %f%n", value);
    }

    public static void printResult(long value) {
        System.out.printf(Locale.ROOT, "Result: %d%n", value);
    }

    public static void printResult(boolean value) {
        System.out.printf(Locale.ROOT, "Result: %b%n", value);
    }

    public static void printResult(String value) {
        System.out.printf(Locale.ROOT, "Result: %s%n", value);
    }

    // CORE-15: єдина точка виводу помилок
    public static void printError(String message) {
        System.out.println(message);
    }

    public static void printUnknownCommand(String command) {
        printError(ERR_UNKNOWN_COMMAND + command);
        System.out.println(HINT_HELP);
    }
}
