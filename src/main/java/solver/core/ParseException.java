package solver.core;

/** Thrown when the token stream does not match what the parser expects. */
public class ParseException extends RuntimeException {

    private final int position;

    public ParseException(String message, int position) {
        super(message);
        this.position = position;
    }

    /** Token index in the stream where the error was detected. */
    public int getPosition() {
        return position;
    }
}
