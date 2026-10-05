package solver.core;


public enum TokenType {
    NUMBER,
    IDENTIFIER,
    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE,
    POWER,
    LEFT_PARENTHESIS,
    RIGHT_PARENTHESIS,
    UNKNOWN;

    public static TokenType operatorType(char c) {
        switch (c) {
            case '+':
                return PLUS;
            case '-':
                return MINUS;
            case '*':
                return MULTIPLY;
            case '/':
                return DIVIDE;
            case '^':
                return POWER;
            default:
                return UNKNOWN;
        }
    }
}
