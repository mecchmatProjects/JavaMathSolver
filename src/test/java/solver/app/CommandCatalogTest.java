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
}
