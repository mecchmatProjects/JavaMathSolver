package solver.core;

/**
 * C2: класифікація символів виразу за класами лексем.
 */
public final class CharClassifier {

    private CharClassifier() {
    }

    // C2: '0'..'9'. Свідомо без Character.isDigit: той приймає й арабські
    // та інші цифри Unicode, а tokenizer має розуміти лише ASCII.
    public static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    // C2: 'a'..'z', 'A'..'Z', '_' — початок ідентифікатора
    public static boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    // C2: + - * / ^
    public static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';
    }

    // C2: дужки
    public static boolean isDelimiter(char c) {
        return c == '(' || c == ')';
    }

    // C2: пробіл і табуляція
    public static boolean isWhitespace(char c) {
        return c == ' ' || c == '\t';
    }

    // C2: десяткова крапка
    public static boolean isDecimalPoint(char c) {
        return c == '.';
    }
}
