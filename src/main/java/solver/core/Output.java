package solver.core;

import java.util.Locale;
import static solver.core.Messages.*;

/**
 * CORE-14: єдина точка виводу результатів і помилок.
 */
public final class Output {

    private static final String NEGATIVE_ZERO = "-0.000000";
    private static final String POSITIVE_ZERO = "0.000000";

    private Output() {
    }

    // CORE-14: єдиний формат виводу
    public static void printResult(double value) {
        if (!Double.isFinite(value)) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        String formatted = String.format(Locale.ROOT, "%f", value);
        if (formatted.equals(NEGATIVE_ZERO)) {
            formatted = POSITIVE_ZERO; // -0.0 і все, що округлюється до нуля (-1e-9)
        }
        System.out.printf(Locale.ROOT, "Result: %s%n", formatted);
    }

    public static void printResult(long value) {
        System.out.printf(Locale.ROOT, "Result: %d%n", value);
    }

    public static void printResult(boolean value) {
        System.out.printf(Locale.ROOT, "Result: %b%n", value);
    }

    public static void printResult(String value) {
        System.out.printf(Locale.ROOT, "Result: %s%n", value);
    }

    // C10: рядок багаторядкового результату (tokenize, token-stats) без префікса "Result: "
    public static void printLine(String text) {
        System.out.println(text);
    }

    /**
     * Видима форма лексеми для виводу: керівні та невидимі символи не друкуються "як є",
     * бо ламають рядок ("\n") або зникають. {@code "x\n"} → {@code "x\\n"},
     * U+200B → {@code "\\u200B"}. Звичайний текст (і emoji) повертається без змін.
     */
    public static String displayLexeme(String lexeme) {
        StringBuilder sb = new StringBuilder(lexeme.length());
        for (int i = 0; i < lexeme.length(); i++) {
            char c = lexeme.charAt(i);
            switch (c) {
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (Character.isISOControl(c) || isInvisible(c)) {
                        sb.append(String.format(Locale.ROOT, "\\u%04X", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        return sb.toString();
    }

    // Пробіли нульової ширини, BOM, NBSP тощо: у терміналі їх не видно
    private static boolean isInvisible(char c) {
        if (c == ' ') {
            return false;
        }
        int type = Character.getType(c);
        return type == Character.FORMAT
                || type == Character.SPACE_SEPARATOR
                || type == Character.LINE_SEPARATOR
                || type == Character.PARAGRAPH_SEPARATOR;
    }

    // CORE-15: єдина точка виводу помилок
    public static void printError(String message) {
        System.out.println(message);
    }

    public static void printUnknownCommand(String command) {
        printError(ERR_UNKNOWN_COMMAND + command);
        System.out.println(HINT_HELP);
    }
}
