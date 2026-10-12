package solver.core;

/**
 * CORE-15: стандартизовані повідомлення.
 */
public final class Messages {

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
    public static final String ERR_POINTS_EMPTY = "Error: Points array must not be empty";
    public static final String ERR_POINTS_NULL_ELEMENT = "Error: Points array must not contain null";
    public static final String ERR_POINT_NULL = "Error: Point must not be null";
    public static final String ERR_POINTS_NOT_FINITE = "Error: Point coordinates must be finite numbers";
    public static final String ERR_HULL_MIN_POINTS = "Error: Convex hull needs at least 3 points";
    public static final String ERR_POLYGON_MIN_VERTICES = "Error: Polygon needs at least 3 vertices";
    public static final String ERR_POLYGON_NOT_SIMPLE = "Error: Polygon must be simple (no self-intersections)";
    public static final String ERR_POLYGON_NOT_TRIANGULABLE = "Error: Polygon cannot be triangulated";
    public static final String ERR_HULL_DEGENERATE = "Error: Convex hull is degenerate";

    private Messages() {
    }


}
