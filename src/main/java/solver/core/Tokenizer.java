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
    // C7: String -> String[]. "2*x + sin(x) -> ["2","*","x","+","sin","(","x",")"]
    public static String[] lexemes(String expression){
        if(expression == null){
            throw new IllegalArgumentException(Messages.ERR_NULL_EXPRESSION);
        }
        // Токенів не більше, ніж символів: кожна лексема займає хоча б 1 символ.
        String[] buffer = new String[expression.length()];
        int count = 0;
        // Крок циклу це не i++, а перехід на кінець поточної лексеми.
        // continue теж виконує крок, тому пробіл просто пропускається.
        TokenType previous = null; // попередній токен (пробіли його не міняють)
        for(int i=0, end=0; i<expression.length(); i=end){
            char first = expression.charAt(i);
            boolean signed = isSignedNumberStart(expression,i,previous);
            end = signed ? scanNumber(expression, i+1) : scanEnd(expression,i);
            if(CharClassifier.isWhitespace(first)){
                continue;
            }

            char[] lexeme = new char[end-i];
            expression.getChars(i,end,lexeme,0);
            buffer[count] = new String(lexeme);
            count++;

            if (signed) {
                previous = numberType(expression, i + 1, end);
            } else if (isNumberStart(first)) {
                previous = numberType(expression, i, end);
            } else {
                previous = typeOfFirstChar(first);
            }
        }
        return Arrays.copyOf(buffer,count);
    }
    // C4: String -> TokenType[], "2*x + 3" -> NUMBER MULTIPLY IDENTIFIER PLUS NUMBER

    /**
     * Розбиває вираз на лексеми і повертає тип кожної лексеми.
     *
     * <ul>
     *   <li>пробіли й табуляції пропускаються і токенів не дають;</li>
     *   <li>число це жадібна послідовність цифр і крапок: {@code цифри} або
     *       {@code цифри.цифри} дають NUMBER, інакше ({@code 2..5}, {@code .5}) UNKNOWN;</li>
     *   <li>{@code -} приклеюється до числа, лише якщо далі йде цифра і це перший токен
     *       або попередній токен це оператор чи {@code (}: {@code -5} це NUMBER,
     *       а {@code 3-5} це NUMBER MINUS NUMBER;</li>
     *   <li>послідовність літер це один IDENTIFIER;</li>
     *   <li>{@code + - * / ^ ( )} це по одному токену відповідного типу;</li>
     *   <li>будь-який інший символ це окремий токен UNKNOWN.</li>
     * </ul>
     *
     * @throws IllegalArgumentException якщо {@code expression == null}
     */
    public static TokenType[] tokenizeTypes(String expression) {
        String[] Lexemes = lexemes(expression);

        TokenType[] buffer = new TokenType[Lexemes.length];
        int count = 0;

        TokenType previous = null;
        for (int i = 0;i<Lexemes.length;i++) {
            char first = Lexemes[i].charAt(0);
            boolean signed = isSignedNumberStart(Lexemes[i], 0, previous);
            TokenType type;
            if (signed) {
                type = numberType(Lexemes[i], 1, Lexemes[i].length());
            } else if (isNumberStart(first)) {
                type = numberType(Lexemes[i], 0, Lexemes[i].length());
            } else {
                type = typeOfFirstChar(first);
            }
            buffer[i] = type;
            previous = type;
        }

        return Arrays.copyOf(buffer, Lexemes.length);
    }

    /**
     * Повертає індекс КІНЦЯ (не включно) лексеми, що починається в позиції
     * {@code start}. Лексема це {@code s.substring(start, end)}.
     *
     * <p>Передумова: {@code 0 <= start < s.length()}.
     * Постумова: {@code start < результат <= s.length()}, тобто цикл
     * ніколи не зациклюється.
     *
     * <p>Пробіли і літери групуються в серію однакових символів, число
     * (цифри і крапки) читається жадібно, усе інше (оператори, дужки,
     * невідомі символи) це лексема з одного символа. Знак мінуса перед
     * числом тут не розглядається: це контекстне правило, див.
     * {@link #isSignedNumberStart}. Цей метод перевикористають C6-C7.
     */
    private static int scanEnd(String s, int start) {
        char c = s.charAt(start);

        if (CharClassifier.isWhitespace(c)) {
            return scanWhitespace(s, start);
        }
        if (isNumberStart(c)) {
            return scanNumber(s, start);
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

    // Число читається жадібно: усі цифри і крапки підряд ("2..5" це одна лексема).
    private static int scanNumber(String s, int start) {
        int end = start + 1;
        while (end < s.length()
                && (CharClassifier.isDigit(s.charAt(end)) || CharClassifier.isDecimalPoint(s.charAt(end)))) {
            end++;
        }
        return end;
    }

    private static boolean isNumberStart(char c) {
        return CharClassifier.isDigit(c) || CharClassifier.isDecimalPoint(c);
    }

    /**
     * Чи є {@code '-'} у позиції {@code i} знаком числа, а не оператором: далі
     * одразу йде цифра, і це перший токен або попередній токен це оператор чи {@code (}.
     */
    private static boolean isSignedNumberStart(String s, int i, TokenType previous) {
        if (s.charAt(i) != '-' || i + 1 >= s.length() || !CharClassifier.isDigit(s.charAt(i + 1))) {
            return false;
        }
        if (previous == null) {
            return true;
        }
        switch (previous) {
            case PLUS:
            case MINUS:
            case MULTIPLY:
            case DIVIDE:
            case POWER:
            case LEFT_PARENTHESIS:
                return true;
            default:
                return false;
        }
    }

    // NUMBER, якщо s[start, end) має вигляд "цифри" або "цифри.цифри", інакше UNKNOWN.
    private static TokenType numberType(String s, int start, int end) {
        int i = start;
        while (i < end && CharClassifier.isDigit(s.charAt(i))) {
            i++;
        }
        if (i == start) {
            return TokenType.UNKNOWN; // немає цифр перед крапкою: ".5"
        }
        if (i == end) {
            return TokenType.NUMBER;  // "цифри"
        }
        i++; // це крапка: лексема складається лише з цифр і крапок
        int fraction = i;
        while (i < end && CharClassifier.isDigit(s.charAt(i))) {
            i++;
        }
        return (i == end && i > fraction) ? TokenType.NUMBER : TokenType.UNKNOWN;
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
     * бо числа тут не розглядаються: їх тип визначає numberType).
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
