package solver.app;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import solver.core.ArgumentValidator;

import static org.junit.jupiter.api.Assertions.*;
import static solver.testutil.StdOut.capture;

/**
 * CORE-16: довідка і її узгодженість з каталогом та диспетчером.
 * Якщо хтось додасть команду в help, але забуде CommandCatalog або CommandDispatcher
 * (або навпаки), ці тести впадуть.
 */
class HelpPrinterTest {

    // "  add a b    a + b" -> "add";   "... [series-b]" -> аліас "series-b"
    private static final Pattern USAGE_LINE = Pattern.compile("^ {2}(\\S+)");
    private static final Pattern ALIAS = Pattern.compile("\\[([a-z0-9-]+)]\\s*$");

    private static final String[] ALL_COMMANDS = {
            "help",
            "add", "sub", "mul", "div", "pow", "sqrt", "abs",
            "solve-linear", "solve-quadratic", "max3", "gcd", "factorial", "fibonacci",
            "taylor-sin", "taylor-cos", "taylor-sinh", "taylor-cosh", "taylor-exp",
            "taylor-ln-one-plus-x", "taylor-geom-series", "taylor-artanh", "taylor-sqrt-one-plus-x",
            "taylor-inv-sqrt-one-plus-x", "taylor-asin", "taylor-inv-sq", "taylor-inv-cube",
            "taylor-inv-one-plus-x2",
            "distance", "origin-distance", "manhattan-distance", "midpoint", "quadrant", "collinear",
            "circle-area", "circle-circumference", "rectangle-area", "rectangle-perimeter", "ellipse-area",
            "triangle-valid", "triangle-area", "triangle-medians", "triangle-bisectors", "triangle-heights",
            "triangle-angles", "triangle-area-inradius", "cylinder-volume", "cone-volume", "torus-volume",
            "circle-segment", "circle-line", "circles-intersect", "squares-intersect", "rect-bounding-box",
            "polygon", "monte-carlo-triangle",
            "tokenize", "token-stats", "validate-expr",
    };

    private static String help() {
        return capture(HelpPrinter::printHelp);
    }

    private static List<String> commandsListedInHelp() {
        List<String> commands = new ArrayList<>();
        for (String line : help().split("\n")) {
            Matcher usage = USAGE_LINE.matcher(line);
            if (usage.find()) {
                commands.add(usage.group(1));
                Matcher alias = ALIAS.matcher(line);
                if (alias.find()) {
                    commands.add(alias.group(1));
                }
            }
        }
        return commands;
    }

    @Test
    void headerShowsVersionAndUsage() {
        String[] lines = help().split("\n");
        assertEquals("JavaMathSolver v" + MathSolver.VERSION, lines[0]);
        assertTrue(lines[1].startsWith("Usage: "));
        assertTrue(lines[1].contains("solver.app.MathSolver"));
    }

    @Test
    void helpListsEveryImplementedCommand() {
        List<String> listed = commandsListedInHelp();
        for (String command : ALL_COMMANDS) {
            assertTrue(listed.contains(command), "help не містить " + command);
        }
    }

    @Test
    void everyCommandInHelpIsKnownToCatalog() {
        for (String command : commandsListedInHelp()) {
            assertNotEquals(ArgumentValidator.UNKNOWN_COMMAND, CommandCatalog.getExpectedArgsCount(command),
                    "help показує " + command + ", але CommandCatalog її не знає");
        }
    }

    @Test
    void everyCommandInHelpReachesItsHandler() {
        for (String command : commandsListedInHelp()) {
            String out = capture(() -> MathSolver.main(sampleArgs(command)));
            assertFalse(out.startsWith(solver.core.Messages.ERR_UNKNOWN_COMMAND),
                    command + " є в help, але CommandDispatcher її не обробляє");
            assertFalse(out.startsWith("Error: Not enough") || out.startsWith("Error: Too many"),
                    command + ": арність у help/каталозі не збігається: " + out);
        }
    }

    // Аргументи потрібної кількості, щоб команда дійшла до dispatch
    private static String[] sampleArgs(String command) {
        int count = CommandCatalog.getExpectedArgsCount(command);
        if (CommandCatalog.getArgKind(command) == CommandCatalog.ArgKind.TEXT) {
            return new String[] {command, "x"};
        }
        if (count == CommandCatalog.VARIADIC_POLYGON) {
            return new String[] {command, "0", "0", "1", "0", "0", "1"};
        }
        String[] args = new String[count + 1];
        args[0] = command;
        for (int i = 1; i <= count; i++) {
            args[i] = "1";
        }
        return args;
    }
}
