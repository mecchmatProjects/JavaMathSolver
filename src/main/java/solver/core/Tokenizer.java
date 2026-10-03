package solver.core;

import java.util.Arrays;

/**
 * C1: математичний tokenizer на масивах.
 */
public final class Tokenizer {

    private Tokenizer() {
    }

    // C1: символи виразу без пробілів, "2*x + 3" -> ['2','*','x','+','3']
    public static char[] toCharacters(String expression) {
        if (expression == null) {
            throw new IllegalArgumentException(Messages.ERR_NULL_EXPRESSION);
        }
        // Розмір невідомий заздалегідь: буфер із запасом + обрізання по факту
        char[] buffer = new char[expression.length()];
        int n = 0;
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (!CharClassifier.isWhitespace(c)) {
                buffer[n] = c;
                n++;
            }
        }
        return Arrays.copyOf(buffer, n);
    }

    // C4: String -> TokenType[], "2*x + 3" -> NUMBER MULTIPLY IDENTIFIER PLUS NUMBER

    /**
     * Розбиває вираз на лексеми і повертає тип кожної лексеми.
     *
     * <ul>
     *   <li>пробіли й табуляції пропускаються і токенів не дають;</li>
     *   <li>послідовність цифр це один NUMBER;</li>
     *   <li>послідовність літер це один IDENTIFIER;</li>
     *   <li>{@code + - * / ^ ( )} це по одному токену відповідного типу;</li>
     *   <li>будь-який інший символ це окремий токен UNKNOWN.</li>
     * </ul>
     *
     * @throws IllegalArgumentException якщо {@code expression == null}
     */
    public static TokenType[] tokenizeTypes(String expression) {
        if (expression == null) {
            throw new IllegalArgumentException(Messages.ERR_NULL_EXPRESSION);
        }

        // Токенів не більше, ніж символів: кожна лексема займає хоча б 1 символ.
        TokenType[] buffer = new TokenType[expression.length()];
        int count = 0;

        // Крок циклу це не i++, а перехід на кінець поточної лексеми.
        // continue теж виконує крок, тому пробіл просто пропускається.
        for (int i = 0, end = 0; i < expression.length(); i = end) {
            end = scanEnd(expression, i);
            char first = expression.charAt(i);
            if (CharClassifier.isWhitespace(first)) {
                continue;
            }
            buffer[count] = typeOfFirstChar(first);
            count++;
        }

        return Arrays.copyOf(buffer, count);
    }

    /**
     * Повертає індекс КІНЦЯ (не включно) лексеми, що починається в позиції
     * {@code start}. Лексема це {@code s.substring(start, end)}.
     *
     * <p>Передумова: {@code 0 <= start < s.length()}.
     * Постумова: {@code start < результат <= s.length()}, тобто цикл
     * ніколи не зациклюється.
     *
     * <p>Поки що: пробіли, цифри і літери групуються в серію однакових
     * символів, усе інше (оператори, дужки, невідомі символи) це лексема
     * з одного символа. Цей метод перевикористають C5-C7.
     */
    private static int scanEnd(String s, int start) {
        char c = s.charAt(start);

        if (CharClassifier.isWhitespace(c)) {
            return scanWhitespace(s, start);
        }
        if (CharClassifier.isDigit(c)) {
            return scanDigits(s, start);
        }
        if (CharClassifier.isLetter(c)) {
            return scanLetters(s, start);
        }
        return start + 1;
    }

    private static int scanWhitespace(String s, int start) {
        int end = start + 1;
        while (end < s.length() && CharClassifier.isWhitespace(s.charAt(end))) {
            end++;
        }
        return end;
    }

    private static int scanDigits(String s, int start) {
        int end = start + 1;
        while (end < s.length() && CharClassifier.isDigit(s.charAt(end))) {
            end++;
        }
        return end;
    }

    private static int scanLetters(String s, int start) {
        int end = start + 1;
        while (end < s.length() && CharClassifier.isLetter(s.charAt(end))) {
            end++;
        }
        return end;
    }

    /**
     * Тип лексеми за її першим символом (для C4 цього достатньо,
     * бо scanEnd групує лише однорідні серії).
     */
    private static TokenType typeOfFirstChar(char c) {
        if (CharClassifier.isDigit(c)) {
            return TokenType.NUMBER;
        }
        if (CharClassifier.isLetter(c)) {
            return TokenType.IDENTIFIER;
        }
        if (c == '(') {
            return TokenType.LEFT_PARENTHESIS;
        }
        if (c == ')') {
            return TokenType.RIGHT_PARENTHESIS;
        }
        // + - * / ^ дають свій тип, усе інше дає UNKNOWN.
        return TokenType.operatorType(c);
    }
}
