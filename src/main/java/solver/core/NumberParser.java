package solver.core;

import java.util.regex.Pattern;
import static solver.core.Output.*;
import static solver.core.Messages.*;

/**
 * CORE-13: парсинг чисел з аргументів командного рядка.
 */
public final class NumberParser {

    // CORE-13: допустимі формати чисел
    private static final Pattern INTEGER_PATTERN = Pattern.compile("[+-]?\\d+");
    private static final Pattern DECIMAL_PATTERN = Pattern.compile("[+-]?(\\d+\\.?\\d*|\\.\\d+)([eE][+-]?\\d+)?");

    private NumberParser() {
    }

    // CORE-13: парсинг усіх аргументів після назви команди
    public static Number[] parseArguments(String[] args) {
        Number[] result = new Number[args.length - 1];
        for (int i = 1; i < args.length; i++) {
            Number value = parseNumber(args[i]);
            if (value == null) {
                printError(ERR_INVALID_NUMBER + args[i]);
                return null;
            }
            result[i - 1] = value;
        }
        return result;
    }

    // Long для цілих у межах long, Double для решти; null, якщо формат некоректний
    public static Number parseNumber(String raw) {
        String token = raw.trim();
        try {
            if (INTEGER_PATTERN.matcher(token).matches()) {
                try {
                    return Long.parseLong(token);
                } catch (NumberFormatException overflow) {
                    // ціле поза межами long -> парситься як double нижче
                }
            } else if (!DECIMAL_PATTERN.matcher(token).matches()) {
                return null;
            }
            double value = Double.parseDouble(token);
            return Double.isFinite(value) ? value : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // Ціле значення аргументу (приймає 5, 5.0, 1e3); null, якщо число не ціле
    public static Long toLongExact(Number n) {
        if (n instanceof Long) {
            return (Long) n;
        }
        double v = n.doubleValue();
        if (v != Math.rint(v) || Math.abs(v) >= 0x1p63) {
            return null;
        }
        return (long) v;
    }

    public static double[] toDoubleArray(Number[] arguments) {
        double[] result = new double[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            result[i] = arguments[i].doubleValue();
        }
        return result;
    }
}
