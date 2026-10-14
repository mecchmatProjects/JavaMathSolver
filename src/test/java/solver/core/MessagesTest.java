package solver.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CORE-15: тексти повідомлень зафіксовані регламентом, їх не можна міняти мовчки.
 */
class MessagesTest {

    @Test
    void errorTextsMatchRegulation() {
        assertEquals("Unknown command: ", Messages.ERR_UNKNOWN_COMMAND);
        assertEquals("Invalid number: ", Messages.ERR_INVALID_NUMBER);
        assertEquals("Error: Not enough arguments", Messages.ERR_NOT_ENOUGH_ARGS);
        assertEquals("Error: Too many arguments", Messages.ERR_TOO_MANY_ARGS);
        assertEquals("Error: Division by zero", Messages.ERR_DIVISION_BY_ZERO);
        assertEquals("Error: Invalid mathematical input", Messages.ERR_INVALID_INPUT);
    }

    @Test
    void lab3MessagesAndHint() {
        assertEquals("Error: Coordinates must come in pairs", Messages.ERR_ODD_COORDINATES);
        assertEquals("Error: Invalid token: ", Messages.ERR_INVALID_TOKEN);
        assertEquals("Use 'help' to see available commands.", Messages.HINT_HELP);
        assertEquals("expression must not be null", Messages.ERR_NULL_EXPRESSION);
    }

    @Test
    void prefixMessagesEndWithSpaceForTheValue() {
        // до них дописується назва команди / токен: "Unknown command: hello"
        assertTrue(Messages.ERR_UNKNOWN_COMMAND.endsWith(": "));
        assertTrue(Messages.ERR_INVALID_NUMBER.endsWith(": "));
        assertTrue(Messages.ERR_INVALID_TOKEN.endsWith(": "));
    }
}
