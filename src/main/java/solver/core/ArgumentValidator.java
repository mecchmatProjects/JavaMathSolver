package solver.core;

import static solver.core.Output.*;
import static solver.core.Messages.*;

/**
 * CORE-12 / C0-b: перевірка кількості аргументів.
 *
 * <p>Додатне значення {@code expected} означає точну кількість аргументів.
 * Від'ємні значення позначають службові режими (невідома команда та variadic-команди).
 */
public final class ArgumentValidator {

    // CORE-12: службові значення арності
    public static final int UNKNOWN_COMMAND = -1;

    // C0-b: variadic-режими
    /** Багатокутник: >= 3 вершини (6 чисел), парна кількість. */
    public static final int VARIADIC_POLYGON = -2;
    /** Набір чисел: >= 1 число (statistics, normalize, ...). */
    public static final int VARIADIC_NUMBERS = -3;
    /** Набір точок: >= 1 точка (2 числа), парна кількість (centroid, bounding-box, ...). */
    public static final int VARIADIC_POINTS = -4;
    /** Текстовий вираз: >= 1 слово, аргументи не парсяться як числа (tokenize, ...). */
    public static final int VARIADIC_TEXT = -5;

    public static final int POLYGON_MIN_ARGS = 6;
    public static final int POINTS_MIN_ARGS = 2;

    private ArgumentValidator() {
    }

    // CORE-12: перевірка кількості аргументів
    public static boolean validateArgs(int actualCount, int expected) {
        switch (expected) {
            case VARIADIC_POLYGON:
                return requireAtLeast(actualCount, POLYGON_MIN_ARGS) && requireEven(actualCount);
            case VARIADIC_POINTS:
                return requireAtLeast(actualCount, POINTS_MIN_ARGS) && requireEven(actualCount);
            case VARIADIC_NUMBERS:
            case VARIADIC_TEXT:
                return requireAtLeast(actualCount, 1);
            default:
                return requireExactly(actualCount, expected);
        }
    }

    private static boolean requireExactly(int actualCount, int expected) {
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

    private static boolean requireAtLeast(int actualCount, int min) {
        if (actualCount < min) {
            printError(ERR_NOT_ENOUGH_ARGS);
            return false;
        }
        return true;
    }

    private static boolean requireEven(int actualCount) {
        if (actualCount % 2 != 0) {
            printError(ERR_ODD_COORDINATES);
            return false;
        }
        return true;
    }
}
