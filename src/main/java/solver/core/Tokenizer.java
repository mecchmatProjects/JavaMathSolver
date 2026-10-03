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
            throw new IllegalArgumentException("expression must not be null");
        }
        // Розмір невідомий заздалегідь: буфер із запасом + обрізання по факту
        char[] buffer = new char[expression.length()];
        int n = 0;
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            // C2 ще не злита, тому пробіли перевіряємо тут; C4 перейде на CharClassifier
            if (c != ' ' && c != '\t') {
                buffer[n] = c;
                n++;
            }
        }
        return Arrays.copyOf(buffer, n);
    }
}
