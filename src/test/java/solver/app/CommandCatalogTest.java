package solver.app;

import org.junit.jupiter.api.Test;

import solver.app.CommandCatalog.ArgKind;
import solver.core.ArgumentValidator;

import static org.junit.jupiter.api.Assertions.*;

class CommandCatalogTest {

    @Test
    void textCommandsHaveTextArgKind() {
        assertEquals(ArgKind.TEXT, CommandCatalog.getArgKind("tokenize"));
        assertEquals(ArgKind.TEXT, CommandCatalog.getArgKind("token-stats"));
        assertEquals(ArgKind.TEXT, CommandCatalog.getArgKind("validate-expr"));
    }

    @Test
    void otherCommandsAreNumeric() {
        assertEquals(ArgKind.NUMERIC, CommandCatalog.getArgKind("add"));
        assertEquals(ArgKind.NUMERIC, CommandCatalog.getArgKind("polygon"));
        assertEquals(ArgKind.NUMERIC, CommandCatalog.getArgKind("unknown-command"));
    }

    @Test
    void variadicValuesMatchRegulation() {
        assertEquals(-2, CommandCatalog.VARIADIC_NUMBERS);
        assertEquals(-3, CommandCatalog.VARIADIC_POINTS);
        assertEquals(-4, CommandCatalog.VARIADIC_POLYGON);
    }

    @Test
    void catalogAndValidatorAgree() {
        assertEquals(ArgumentValidator.VARIADIC_NUMBERS, CommandCatalog.VARIADIC_NUMBERS);
        assertEquals(ArgumentValidator.VARIADIC_POINTS, CommandCatalog.VARIADIC_POINTS);
        assertEquals(ArgumentValidator.VARIADIC_POLYGON, CommandCatalog.VARIADIC_POLYGON);
        assertEquals(ArgumentValidator.VARIADIC_TEXT, CommandCatalog.VARIADIC_TEXT);
    }

    @Test
    void polygonUsesPolygonMode() {
        assertEquals(CommandCatalog.VARIADIC_POLYGON, CommandCatalog.getExpectedArgsCount("polygon"));
    }

    @Test
    void textCommandsAreKnownToCatalog() {
        // кожна TEXT-команда мусить мати арність, інакше main відсіче її як Unknown command
        for (String command : new String[] {"tokenize", "token-stats", "validate-expr"}) {
            assertEquals(ArgKind.TEXT, CommandCatalog.getArgKind(command), command);
            assertEquals(CommandCatalog.VARIADIC_TEXT, CommandCatalog.getExpectedArgsCount(command), command);
        }
    }

    // ===================================================================
    // CORE-12 — арність за специфікацією
    // ===================================================================

    private static void assertArity(int expected, String... commands) {
        for (String command : commands) {
            assertEquals(expected, CommandCatalog.getExpectedArgsCount(command), command);
            assertEquals(ArgKind.NUMERIC, CommandCatalog.getArgKind(command), command);
        }
    }

    @Test
    void zeroArguments() {
        assertArity(0, "help");
    }

    @Test
    void oneArgument() {
        assertArity(1, "sqrt", "abs", "factorial", "fibonacci",
                "circle-area", "circle-circumference", "monte-carlo-triangle");
    }

    @Test
    void twoArguments() {
        assertArity(2, "add", "sub", "mul", "div", "pow", "solve-linear", "gcd",
                "quadrant", "rectangle-area", "rectangle-perimeter", "origin-distance",
                "ellipse-area", "cylinder-volume", "cone-volume", "torus-volume");
    }

    @Test
    void taylorSeriesTakeXAndEpsUnderBothNames() {
        assertArity(2,
                "taylor-sin", "sin-taylor", "taylor-cos", "series-b", "taylor-sinh", "series-c",
                "taylor-cosh", "series-h", "taylor-exp", "series-e", "taylor-ln-one-plus-x", "series-ln",
                "taylor-geom-series", "series-ye", "taylor-artanh", "series-j",
                "taylor-sqrt-one-plus-x", "series-k", "taylor-inv-sqrt-one-plus-x", "series-l",
                "taylor-asin", "series-m", "taylor-inv-sq", "series-z", "taylor-inv-cube", "series-i",
                "taylor-inv-one-plus-x2", "series-y");
    }

    @Test
    void threeArguments() {
        assertArity(3, "solve-quadratic", "max3", "triangle-area", "triangle-valid",
                "triangle-medians", "triangle-bisectors", "triangle-heights", "triangle-angles");
    }

    @Test
    void fourSixAndEightArguments() {
        assertArity(4, "distance", "manhattan-distance", "midpoint", "triangle-area-inradius", "circle-segment");
        assertArity(6, "collinear", "circle-line", "circles-intersect", "squares-intersect");
        assertArity(8, "rect-bounding-box");
    }

    @Test
    void unknownCommands() {
        assertEquals(ArgumentValidator.UNKNOWN_COMMAND, CommandCatalog.getExpectedArgsCount("hello"));
        assertEquals(ArgumentValidator.UNKNOWN_COMMAND, CommandCatalog.getExpectedArgsCount(""));
        // каталог чекає вже нормалізовану назву: регістр і пробіли прибирає main
        assertEquals(ArgumentValidator.UNKNOWN_COMMAND, CommandCatalog.getExpectedArgsCount("ADD"));
        assertEquals(ArgumentValidator.UNKNOWN_COMMAND, CommandCatalog.getExpectedArgsCount(" add"));
    }
}
