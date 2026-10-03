package solver.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CharClassifierTest {

    @Test
    void recognisesAsciiDigits() {
        assertTrue(CharClassifier.isDigit('0'));
        assertTrue(CharClassifier.isDigit('5'));
        assertTrue(CharClassifier.isDigit('9'));
        assertFalse(CharClassifier.isDigit('a'));
        assertFalse(CharClassifier.isDigit('.'));
    }

    @Test
    void rejectsNonAsciiDigits() {
        // Саме через ці символи заборонено Character.isDigit: він вважає їх цифрами
        assertFalse(CharClassifier.isDigit('٥'));  // ٥ арабсько-індійська 5
        assertFalse(CharClassifier.isDigit('५'));  // ५ деванагарі 5
        assertFalse(CharClassifier.isDigit('０'));  // ０ повноширинний 0
    }

    @Test
    void recognisesLettersAndUnderscore() {
        assertTrue(CharClassifier.isLetter('a'));
        assertTrue(CharClassifier.isLetter('z'));
        assertTrue(CharClassifier.isLetter('A'));
        assertTrue(CharClassifier.isLetter('Z'));
        assertTrue(CharClassifier.isLetter('_'));
        assertFalse(CharClassifier.isLetter('0'));
        assertFalse(CharClassifier.isLetter('-'));
    }

    @Test
    void recognisesOperators() {
        assertTrue(CharClassifier.isOperator('+'));
        assertTrue(CharClassifier.isOperator('-'));
        assertTrue(CharClassifier.isOperator('*'));
        assertTrue(CharClassifier.isOperator('/'));
        assertTrue(CharClassifier.isOperator('^'));
        assertFalse(CharClassifier.isOperator('('));
        assertFalse(CharClassifier.isOperator('%'));
    }

    @Test
    void recognisesDelimiters() {
        assertTrue(CharClassifier.isDelimiter('('));
        assertTrue(CharClassifier.isDelimiter(')'));
        assertFalse(CharClassifier.isDelimiter('['));
        assertFalse(CharClassifier.isDelimiter('{'));
    }

    @Test
    void recognisesSpaceAndTabOnly() {
        assertTrue(CharClassifier.isWhitespace(' '));
        assertTrue(CharClassifier.isWhitespace('\t'));
        assertFalse(CharClassifier.isWhitespace('\n'));
        assertFalse(CharClassifier.isWhitespace('x'));
    }

    @Test
    void recognisesDecimalPoint() {
        assertTrue(CharClassifier.isDecimalPoint('.'));
        assertFalse(CharClassifier.isDecimalPoint(','));
    }

    @Test
    void unknownCharactersBelongToNoClass() {
        for (char c : new char[] {'@', '#', 'é'}) {
            assertFalse(CharClassifier.isDigit(c), "isDigit " + c);
            assertFalse(CharClassifier.isLetter(c), "isLetter " + c);
            assertFalse(CharClassifier.isOperator(c), "isOperator " + c);
            assertFalse(CharClassifier.isDelimiter(c), "isDelimiter " + c);
            assertFalse(CharClassifier.isWhitespace(c), "isWhitespace " + c);
            assertFalse(CharClassifier.isDecimalPoint(c), "isDecimalPoint " + c);
        }
    }

    @Test
    void classesDoNotOverlap() {
        // Кожен символ виразу належить рівно одному класу
        for (char c : "0123456789abzABZ_+-*/^(). \t".toCharArray()) {
            int classes = 0;
            if (CharClassifier.isDigit(c)) classes++;
            if (CharClassifier.isLetter(c)) classes++;
            if (CharClassifier.isOperator(c)) classes++;
            if (CharClassifier.isDelimiter(c)) classes++;
            if (CharClassifier.isWhitespace(c)) classes++;
            if (CharClassifier.isDecimalPoint(c)) classes++;
            assertEquals(1, classes, "char '" + c + "' must belong to exactly one class");
        }
    }
}
