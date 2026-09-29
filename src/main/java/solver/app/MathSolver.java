package solver.app;

import java.util.Arrays;
import java.util.Locale;
import static solver.core.Output.*;
import static solver.core.Messages.*;
import static solver.core.ArgumentValidator.*;
import static solver.core.NumberParser.*;
import static solver.app.CommandCatalog.getExpectedArgsCount;
import static solver.app.CommandDispatcher.dispatch;
import static solver.app.CommandDispatcher.dispatchText;

/**
 * Точка входу JavaMathSolver.
 */
public final class MathSolver {

    public static final String VERSION = "0.2";

    private MathSolver() {
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("JavaMathSolver v" + VERSION);
            System.out.println(HINT_HELP);
            return;
        }

        String command = args[0].trim().toLowerCase(Locale.ROOT);

        int expected = getExpectedArgsCount(command);
        if (expected == UNKNOWN_COMMAND) {
            printUnknownCommand(args[0]);
            return;
        }

        if (!validateArgs(args.length - 1, expected)) {
            return;
        }

        // C0-b: текстові команди отримують вираз цілим рядком, без парсингу чисел
        if (expected == VARIADIC_TEXT) {
            dispatchText(command, joinText(args));
            return;
        }

        Number[] arguments = parseArguments(args);
        if (arguments == null) {
            return;
        }

        dispatch(command, arguments);
    }

    // C0-b: "2*x", "+", "3" -> "2*x + 3" (якщо користувач не взяв вираз у лапки)
    static String joinText(String[] args) {
        return String.join(" ", Arrays.copyOfRange(args, 1, args.length)).trim();
    }
}
