package solver.core;

import static solver.core.Output.*;
import static solver.core.Messages.*;

/**
 * CORE-12: перевірка кількості аргументів.
 */
public final class ArgumentValidator {

    // CORE-12: службові значення арності
    public static final int UNKNOWN_COMMAND = -1;
    public static final int VARIADIC = -2;
    public static final int VARIADIC_MIN_ARGS = 6;

    private ArgumentValidator() {
    }

    // CORE-12: перевірка кількості аргументів
    public static boolean validateArgs(int actualCount, int expected) {
        if (expected == VARIADIC) {
            if (actualCount < VARIADIC_MIN_ARGS) {
                printError(ERR_NOT_ENOUGH_ARGS);
                return false;
            }
            return true;
        }
        if (actualCount < expected) {
            printError(ERR_NOT_ENOUGH_ARGS);
            return false;
        }
        if (actualCount > expected) {
            printError(ERR_TOO_MANY_ARGS);
            return false;
        }
        return true;
    }
}
