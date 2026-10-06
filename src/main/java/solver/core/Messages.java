package solver.core;

/**
 * CORE-15: стандартизовані повідомлення.
 */
public final class Messages {

    public static final String ERR_POINTS_EMPTY = "Error: Points array must not be empty";
    public static final String ERR_POINTS_NULL_ELEMENT = "Error: Points array must not contain null";


    public static final String ERR_UNKNOWN_COMMAND = "Unknown command: ";
    public static final String ERR_INVALID_NUMBER = "Invalid number: ";
    public static final String ERR_NOT_ENOUGH_ARGS = "Error: Not enough arguments";
    public static final String ERR_TOO_MANY_ARGS = "Error: Too many arguments";
    public static final String ERR_DIVISION_BY_ZERO = "Error: Division by zero";
    public static final String ERR_INVALID_INPUT = "Error: Invalid mathematical input";
    public static final String ERR_ODD_COORDINATES = "Error: Coordinates must come in pairs";
    public static final String ERR_INVALID_TOKEN = "Error: Invalid token: ";
    public static final String HINT_HELP = "Use 'help' to see available commands.";
    public static final String ERR_NULL_EXPRESSION = "expression must not be null";

    private Messages() {
    }


}
