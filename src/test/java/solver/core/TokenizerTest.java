package solver.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static solver.core.TokenType.*;

class TokenizerTest {

    @Test
    void stripsSpacesFromExpression() {
        assertArrayEquals(new char[] {'2', '*', 'x', '+', '3'}, Tokenizer.toCharacters("2*x + 3"));
    }

    @Test
    void keepsParenthesesAndLetters() {
        assertArrayEquals(new char[] {'s', 'i', 'n', '(', 'x', ')'}, Tokenizer.toCharacters("sin(x)"));
    }

    @Test
    void emptyStringGivesEmptyArray() {
        assertArrayEquals(new char[] {}, Tokenizer.toCharacters(""));
    }

    @Test
    void blankStringGivesEmptyArray() {
        assertArrayEquals(new char[] {}, Tokenizer.toCharacters(" "));
        assertArrayEquals(new char[] {}, Tokenizer.toCharacters("   \t  "));
    }

    @Test
    void stripsTabsAndRepeatedSpaces() {
        assertArrayEquals(new char[] {'1', '+', '2'}, Tokenizer.toCharacters("  1\t+   2 "));
    }

    @Test
    void keepsExpressionWithoutSpacesUnchanged() {
        assertArrayEquals(new char[] {'2', '*', 'x', '^', '2'}, Tokenizer.toCharacters("2*x^2"));
    }

    @Test
    void nullExpressionIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> Tokenizer.toCharacters(null));
    }

    // Додано в C4: повідомлення про null береться з Messages.
    @Test
    void toCharactersNullThrowsWithMessageFromMessages() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class, () -> Tokenizer.toCharacters(null));
        assertEquals(Messages.ERR_NULL_EXPRESSION, ex.getMessage());
    }

    // ===================================================================
    // C4 — tokenizeTypes
    // Не перевіряємо "3.14", "x1" і "-5": це змінили C5 і C6 (так задумано),
    // і тести C4 після цього не мають ламатися.
    // ===================================================================

    @Test
    void tokenizeTypesSpecExample() {
        assertArrayEquals(
                new TokenType[] {NUMBER, MULTIPLY, IDENTIFIER, PLUS, NUMBER},
                Tokenizer.tokenizeTypes("2*x + 3"));
    }

    @Test
    void tokenizeTypesDemoExampleFromC10() {
        TokenType[] types = Tokenizer.tokenizeTypes("2*x^2 + 3*x - 5");
        assertEquals(11, types.length);
        assertArrayEquals(
                new TokenType[] {
                        NUMBER, MULTIPLY, IDENTIFIER, POWER, NUMBER,
                        PLUS, NUMBER, MULTIPLY, IDENTIFIER, MINUS, NUMBER
                },
                types);
    }

    @Test
    void tokenizeTypesEmptyString() {
        assertEquals(0, Tokenizer.tokenizeTypes("").length);
    }

    @Test
    void tokenizeTypesOnlyWhitespace() {
        assertEquals(0, Tokenizer.tokenizeTypes("   ").length);
        assertEquals(0, Tokenizer.tokenizeTypes("\t \t").length);
    }

    @Test
    void tokenizeTypesNullThrowsWithMessageFromMessages() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class, () -> Tokenizer.tokenizeTypes(null));
        assertEquals(Messages.ERR_NULL_EXPRESSION, ex.getMessage());
    }

    @Test
    void tokenizeTypesSingleNumberAndSingleIdentifier() {
        assertArrayEquals(new TokenType[] {NUMBER}, Tokenizer.tokenizeTypes("5"));
        assertArrayEquals(new TokenType[] {IDENTIFIER}, Tokenizer.tokenizeTypes("x"));
    }

    @Test
    void tokenizeTypesMultiDigitNumberIsOneToken() {
        assertArrayEquals(new TokenType[] {NUMBER}, Tokenizer.tokenizeTypes("123"));
        assertArrayEquals(new TokenType[] {NUMBER}, Tokenizer.tokenizeTypes("007"));
    }

    @Test
    void tokenizeTypesMultiLetterIdentifierIsOneToken() {
        assertArrayEquals(new TokenType[] {IDENTIFIER}, Tokenizer.tokenizeTypes("velocity"));
        assertArrayEquals(new TokenType[] {IDENTIFIER}, Tokenizer.tokenizeTypes("a_b"));
        assertArrayEquals(new TokenType[] {IDENTIFIER}, Tokenizer.tokenizeTypes("_"));
    }

    @Test
    void tokenizeTypesAllOperators() {
        assertArrayEquals(
                new TokenType[] {PLUS, MINUS, MULTIPLY, DIVIDE, POWER},
                Tokenizer.tokenizeTypes("+-*/^"));
    }

    @Test
    void tokenizeTypesParentheses() {
        assertArrayEquals(
                new TokenType[] {LEFT_PARENTHESIS, RIGHT_PARENTHESIS},
                Tokenizer.tokenizeTypes("()"));
        assertArrayEquals(
                new TokenType[] {
                        LEFT_PARENTHESIS, LEFT_PARENTHESIS, IDENTIFIER,
                        RIGHT_PARENTHESIS, RIGHT_PARENTHESIS
                },
                Tokenizer.tokenizeTypes("((x))"));
    }

    @Test
    void tokenizeTypesFunctionCallShape() {
        assertArrayEquals(
                new TokenType[] {IDENTIFIER, LEFT_PARENTHESIS, IDENTIFIER, RIGHT_PARENTHESIS},
                Tokenizer.tokenizeTypes("sin(x)"));
    }

    @Test
    void tokenizeTypesSpacesDoNotChangeResult() {
        TokenType[] compact = Tokenizer.tokenizeTypes("2*x+3");
        assertArrayEquals(compact, Tokenizer.tokenizeTypes("2 * x + 3"));
        assertArrayEquals(compact, Tokenizer.tokenizeTypes("  2  *x +  3  "));
        assertArrayEquals(compact, Tokenizer.tokenizeTypes("2\t*\tx\t+\t3"));
    }

    @Test
    void tokenizeTypesWhitespaceSplitsLexemes() {
        assertArrayEquals(new TokenType[] {NUMBER, NUMBER}, Tokenizer.tokenizeTypes("12 34"));
        assertArrayEquals(new TokenType[] {IDENTIFIER, IDENTIFIER}, Tokenizer.tokenizeTypes("ab cd"));
    }

    @Test
    void tokenizeTypesDigitFollowedByLetterGivesTwoTokens() {
        // неявне множення це робота Parser, а не tokenizer
        assertArrayEquals(new TokenType[] {NUMBER, IDENTIFIER}, Tokenizer.tokenizeTypes("2x"));
    }

    @Test
    void tokenizeTypesDoesNotCheckSyntax() {
        assertArrayEquals(
                new TokenType[] {NUMBER, PLUS, PLUS, NUMBER},
                Tokenizer.tokenizeTypes("2 + + 3"));
    }

    @Test
    void tokenizeTypesUnknownCharacters() {
        assertArrayEquals(new TokenType[] {UNKNOWN}, Tokenizer.tokenizeTypes("@"));
        assertArrayEquals(new TokenType[] {UNKNOWN}, Tokenizer.tokenizeTypes("#"));
        assertArrayEquals(new TokenType[] {UNKNOWN}, Tokenizer.tokenizeTypes("\u00e9"));
        assertArrayEquals(new TokenType[] {IDENTIFIER, UNKNOWN}, Tokenizer.tokenizeTypes("x@"));
    }

    @Test
    void tokenizeTypesEachUnknownCharacterIsItsOwnToken() {
        assertArrayEquals(new TokenType[] {UNKNOWN, UNKNOWN}, Tokenizer.tokenizeTypes("@@"));
        assertArrayEquals(
                new TokenType[] {NUMBER, UNKNOWN, UNKNOWN, NUMBER},
                Tokenizer.tokenizeTypes("1@#2"));
    }

    @Test
    void tokenizeTypesUnicodeLookalikeOperatorsAreUnknown() {
        // U+00D7 (знак множення) не є '*'
        assertArrayEquals(
                new TokenType[] {NUMBER, UNKNOWN, NUMBER},
                Tokenizer.tokenizeTypes("2\u00d73"));
        // U+2212 (мінус з Word) не є '-'
        assertArrayEquals(
                new TokenType[] {NUMBER, UNKNOWN, NUMBER},
                Tokenizer.tokenizeTypes("5\u22123"));
    }

    @Test
    void tokenizeTypesResultHasExactLengthAndNoNulls() {
        // буфер довжини expression.length() не має «протікати» в результат
        TokenType[] types = Tokenizer.tokenizeTypes("2*x + 3");
        assertEquals(5, types.length);
        for (TokenType type : types) {
            assertNotNull(type);
        }
    }

    @Test
    void tokenizeTypesBufferIsLargeEnoughWhenEveryCharIsAToken() {
        // найгірший випадок: токенів рівно стільки, скільки символів
        TokenType[] types = Tokenizer.tokenizeTypes("+-*/^()");
        assertEquals(7, types.length);
        assertArrayEquals(
                new TokenType[] {
                        PLUS, MINUS, MULTIPLY, DIVIDE, POWER,
                        LEFT_PARENTHESIS, RIGHT_PARENTHESIS
                },
                types);
    }

    @Test
    void tokenizeTypesLongInput() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("12 + x * ");
        }
        sb.append("7");
        TokenType[] types = Tokenizer.tokenizeTypes(sb.toString());
        assertEquals(4 * 1000 + 1, types.length);
        assertEquals(NUMBER, types[0]);
        assertEquals(NUMBER, types[types.length - 1]);
    }

    // ===================================================================
    // C5 — числа: десяткові, зі знаком, некоректні
    // ===================================================================

    @Test
    void tokenizeTypesDecimalNumbers() {
        assertArrayEquals(new TokenType[] {NUMBER}, Tokenizer.tokenizeTypes("3.14"));
        assertArrayEquals(new TokenType[] {NUMBER}, Tokenizer.tokenizeTypes("0.001"));
        assertArrayEquals(new TokenType[] {NUMBER}, Tokenizer.tokenizeTypes("007.50"));
    }

    @Test
    void tokenizeTypesDecimalInExpression() {
        assertArrayEquals(
                new TokenType[] {NUMBER, MULTIPLY, IDENTIFIER},
                Tokenizer.tokenizeTypes("12.5*x"));
        assertArrayEquals(
                new TokenType[] {NUMBER, MULTIPLY, IDENTIFIER, MINUS, NUMBER},
                Tokenizer.tokenizeTypes("12.5*x - 3"));
    }

    @Test
    void tokenizeTypesMinusAtStartIsPartOfNumber() {
        assertArrayEquals(new TokenType[] {NUMBER}, Tokenizer.tokenizeTypes("-5"));
        assertArrayEquals(new TokenType[] {NUMBER}, Tokenizer.tokenizeTypes("-3.14"));
        assertArrayEquals(new TokenType[] {NUMBER}, Tokenizer.tokenizeTypes("  -5  "));
    }

    @Test
    void tokenizeTypesBinaryMinusStaysMinus() {
        assertArrayEquals(new TokenType[] {NUMBER, MINUS, NUMBER}, Tokenizer.tokenizeTypes("3-5"));
        assertArrayEquals(new TokenType[] {NUMBER, MINUS, NUMBER}, Tokenizer.tokenizeTypes("3 - 5"));
        assertArrayEquals(new TokenType[] {IDENTIFIER, MINUS, NUMBER}, Tokenizer.tokenizeTypes("x-5"));
        assertArrayEquals(
                new TokenType[] {RIGHT_PARENTHESIS, MINUS, NUMBER},
                Tokenizer.tokenizeTypes(")-5"));
    }

    @Test
    void tokenizeTypesMinusAfterOperatorIsPartOfNumber() {
        assertArrayEquals(
                new TokenType[] {NUMBER, MULTIPLY, NUMBER},
                Tokenizer.tokenizeTypes("2*-3"));
        assertArrayEquals(
                new TokenType[] {NUMBER, MULTIPLY, NUMBER},
                Tokenizer.tokenizeTypes("2 * -3"));
        assertArrayEquals(
                new TokenType[] {NUMBER, PLUS, NUMBER},
                Tokenizer.tokenizeTypes("2+-3"));
        assertArrayEquals(
                new TokenType[] {NUMBER, POWER, NUMBER},
                Tokenizer.tokenizeTypes("2^-1"));
        assertArrayEquals(
                new TokenType[] {NUMBER, MINUS, NUMBER},
                Tokenizer.tokenizeTypes("3--5"));
    }

    @Test
    void tokenizeTypesMinusAfterLeftParenthesisIsPartOfNumber() {
        assertArrayEquals(
                new TokenType[] {LEFT_PARENTHESIS, NUMBER, RIGHT_PARENTHESIS},
                Tokenizer.tokenizeTypes("(-5)"));
    }

    @Test
    void tokenizeTypesMinusNotFollowedByDigitStaysMinus() {
        assertArrayEquals(new TokenType[] {MINUS, NUMBER}, Tokenizer.tokenizeTypes("- 5"));
        assertArrayEquals(new TokenType[] {MINUS, IDENTIFIER}, Tokenizer.tokenizeTypes("-x"));
        assertArrayEquals(new TokenType[] {MINUS}, Tokenizer.tokenizeTypes("-"));
        assertArrayEquals(
                new TokenType[] {MINUS, LEFT_PARENTHESIS, NUMBER, RIGHT_PARENTHESIS},
                Tokenizer.tokenizeTypes("-(5)"));
    }

    @Test
    void tokenizeTypesMalformedNumbersAreSingleUnknownToken() {
        assertArrayEquals(new TokenType[] {UNKNOWN}, Tokenizer.tokenizeTypes("2..5"));
        assertArrayEquals(new TokenType[] {UNKNOWN}, Tokenizer.tokenizeTypes("3.4.5"));
        assertArrayEquals(new TokenType[] {UNKNOWN}, Tokenizer.tokenizeTypes(".5"));
        assertArrayEquals(new TokenType[] {UNKNOWN}, Tokenizer.tokenizeTypes("5."));
        assertArrayEquals(new TokenType[] {UNKNOWN}, Tokenizer.tokenizeTypes("."));
        assertArrayEquals(new TokenType[] {UNKNOWN}, Tokenizer.tokenizeTypes("-2..5"));
    }

    @Test
    void tokenizeTypesMalformedNumberInsideExpression() {
        assertArrayEquals(
                new TokenType[] {NUMBER, PLUS, UNKNOWN, MULTIPLY, IDENTIFIER},
                Tokenizer.tokenizeTypes("1 + 2..5 * x"));
    }

    @Test
    void tokenizeTypesDotBetweenNonDigitsIsUnknown() {
        assertArrayEquals(
                new TokenType[] {IDENTIFIER, UNKNOWN, IDENTIFIER},
                Tokenizer.tokenizeTypes("x.y"));
    }

    @Test
    void tokenizeTypesSpecExampleStillGivesElevenTokens() {
        // "- 5" з пробілом: мінус бінарний, число 5 окремо
        assertEquals(11, Tokenizer.tokenizeTypes("2*x^2 + 3*x - 5").length);
        assertEquals(11, Tokenizer.tokenizeTypes("2*x^2+3*x-5").length);
    }

    // ===================================================================
    // C7 — String[] lexemes
    // ===================================================================
    @Test
    void lexemesBasicTest() {
        assertArrayEquals(
                new String[] {"2","*","x","+","sin","(","x",")"},
                Tokenizer.lexemes("2*x + sin(x)")
        );
    }

    @Test
    void decimalsAndSignedNumbersIntoLexemes() {
        assertArrayEquals(
                new String[] {"12", "+", "3.4", "*", "(", "-5", "/", "2", ")"},
                Tokenizer.lexemes("12 + 3.4 * ( -5 / 2 )"));
    }

    @Test
    void sameOperatorsNextToSignedNumbersIntoDifferentLexemes() {
        assertArrayEquals(
                new String[]{"5", "-", "-3"},
                Tokenizer.lexemes("5 - -3"));
    }

    @Test
    void shouldReturnEmptyArrayForEmptyString() {
        assertEquals(0, Tokenizer.lexemes("").length);
    }

    @Test
    void specialCharactersProcessedProperly() {
        assertArrayEquals(new String[] {"x","@"}, Tokenizer.lexemes("x@"));
    }

    @Test
    void malformedNumbersAreSingleLexemes() {
        assertArrayEquals(new String[] {"2..5"}, Tokenizer.lexemes("2..5"));
        assertArrayEquals(new String[] {"3.4.5"}, Tokenizer.lexemes("3.4.5"));
        assertArrayEquals(new String[] {".5"}, Tokenizer.lexemes(".5"));
        assertArrayEquals(new String[] {"5."}, Tokenizer.lexemes("5."));
        assertArrayEquals(new String[] {"."}, Tokenizer.lexemes("."));
        assertArrayEquals(new String[] {"-2..5"}, Tokenizer.lexemes("-2..5"));
    }

    @Test
    void lexemesNullExceptionThrow() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class, () -> Tokenizer.lexemes(null));
        assertEquals(Messages.ERR_NULL_EXPRESSION, ex.getMessage());
    }

    @Test
    void lexemesTabEmptyArray() {
        assertEquals(0, Tokenizer.lexemes("\t").length);
    }

    @Test
    void lexemesMinusOperatorSeparate() {
        assertArrayEquals(new String[] {"x","-","5"}, Tokenizer.lexemes("x - 5"));
    }
  
    // C6 — ідентифікатори: літера або '_', далі літери, цифри, '_'
    // ===================================================================

    @Test
    void tokenizeTypesIdentifiersFromSpec() {
        for (String name : new String[] {"x", "y", "x1", "velocity", "mass", "sin", "cos", "sqrt"}) {
            assertArrayEquals(new TokenType[] {IDENTIFIER}, Tokenizer.tokenizeTypes(name), name);
        }
    }

    @Test
    void tokenizeTypesIdentifierMayContainDigitsAndUnderscore() {
        assertArrayEquals(new TokenType[] {IDENTIFIER}, Tokenizer.tokenizeTypes("x1y2"));
        assertArrayEquals(new TokenType[] {IDENTIFIER}, Tokenizer.tokenizeTypes("a_1"));
        assertArrayEquals(new TokenType[] {IDENTIFIER}, Tokenizer.tokenizeTypes("_x"));
        assertArrayEquals(new TokenType[] {IDENTIFIER}, Tokenizer.tokenizeTypes("_1"));
        assertArrayEquals(new TokenType[] {IDENTIFIER}, Tokenizer.tokenizeTypes("Mass2"));
    }

    @Test
    void tokenizeTypesIdentifierCannotStartWithDigit() {
        assertArrayEquals(new TokenType[] {NUMBER, IDENTIFIER}, Tokenizer.tokenizeTypes("1x"));
        assertArrayEquals(new TokenType[] {NUMBER, IDENTIFIER}, Tokenizer.tokenizeTypes("2x"));
        assertArrayEquals(new TokenType[] {NUMBER, IDENTIFIER}, Tokenizer.tokenizeTypes("12abc3"));
    }

    @Test
    void tokenizeTypesFunctionsAreIdentifiers() {
        assertArrayEquals(
                new TokenType[] {
                        IDENTIFIER, LEFT_PARENTHESIS, IDENTIFIER, RIGHT_PARENTHESIS, PLUS,
                        IDENTIFIER, LEFT_PARENTHESIS, IDENTIFIER, RIGHT_PARENTHESIS
                },
                Tokenizer.tokenizeTypes("sin(x) + sqrt(y)"));
    }

    @Test
    void tokenizeTypesIdentifiersWithDigitsInExpression() {
        assertArrayEquals(
                new TokenType[] {IDENTIFIER, PLUS, IDENTIFIER},
                Tokenizer.tokenizeTypes("x1+y2"));
        assertArrayEquals(
                new TokenType[] {IDENTIFIER, MULTIPLY, NUMBER},
                Tokenizer.tokenizeTypes("x1*2"));
    }

    @Test
    void tokenizeTypesDigitsAfterIdentifierDoNotMergeWithFollowingNumber() {
        // пробіл розділяє лексеми: "x1 2" це ідентифікатор і число
        assertArrayEquals(new TokenType[] {IDENTIFIER, NUMBER}, Tokenizer.tokenizeTypes("x1 2"));
    }

    @Test
    void tokenizeTypesMinusAfterIdentifierWithDigitIsBinary() {
        assertArrayEquals(
                new TokenType[] {IDENTIFIER, MINUS, NUMBER},
                Tokenizer.tokenizeTypes("x1-5"));
    }

    // ===================================================================
    // C9 — Token statistics
    // ===================================================================

    @Test
    void basicTokenStatisticsTest() {
        assertArrayEquals(new int[] {1,3,2,2},
                Tokenizer.tokenStatistics("2*x + sin(x)"));
    }

    @Test
    void tokenStatisticsSpecExpression() {
        // Методичка C9 пише "Operators: 3", але у виразі 4 оператори: * + * -
        assertArrayEquals(new int[] {2, 4, 4, 2},
                Tokenizer.tokenStatistics("2*x + 3*x - sin(x)"));
    }

    @Test
    void tokenStatisticsEmptyOrBlankIsAllZeros() {
        assertArrayEquals(new int[] {0, 0, 0, 0}, Tokenizer.tokenStatistics(""));
        assertArrayEquals(new int[] {0, 0, 0, 0}, Tokenizer.tokenStatistics(" \t "));
    }

    @Test
    void tokenStatisticsAlwaysHasFourCategories() {
        assertEquals(4, Tokenizer.tokenStatistics("").length);
        assertEquals(4, Tokenizer.tokenStatistics("x").length);
    }

    @Test
    void tokenStatisticsIgnoresUnknownTokens() {
        assertArrayEquals(new int[] {0, 1, 0, 0}, Tokenizer.tokenStatistics("2..5 @ x"));
        assertArrayEquals(new int[] {0, 0, 0, 0}, Tokenizer.tokenStatistics("@#$"));
    }

    @Test
    void tokenStatisticsSignedNumberIsOneNumberNotOperator() {
        assertArrayEquals(new int[] {1, 0, 0, 0}, Tokenizer.tokenStatistics("-5"));
        assertArrayEquals(new int[] {2, 0, 1, 0}, Tokenizer.tokenStatistics("3 - 5"));
        assertArrayEquals(new int[] {2, 0, 1, 0}, Tokenizer.tokenStatistics("2*-3"));
    }

    @Test
    void tokenStatisticsCountsEveryOperatorAndParenthesis() {
        assertArrayEquals(new int[] {0, 0, 5, 2}, Tokenizer.tokenStatistics("+-*/^()"));
        assertArrayEquals(new int[] {0, 1, 0, 6}, Tokenizer.tokenStatistics("((( x )))"));
    }

    @Test
    void tokenStatisticsSumEqualsNumberOfKnownTokens() {
        String expr = "sin(x)^2 + cos(x)^2 - 1 / velocity @";
        int[] stats = Tokenizer.tokenStatistics(expr);
        int known = 0;
        for (TokenType type : Tokenizer.tokenizeTypes(expr)) {
            if (type != UNKNOWN) {
                known++;
            }
        }
        assertEquals(known, stats[0] + stats[1] + stats[2] + stats[3]);
    }

    @Test
    void tokenStatisticsNullThrows() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class, () -> Tokenizer.tokenStatistics(null));
        assertEquals(Messages.ERR_NULL_EXPRESSION, ex.getMessage());
    }

    // ===================================================================
    // Узгодженість: lexemes і tokenizeTypes мають однакову довжину,
    // а склеєні лексеми = вираз без пробілів (нічого не губиться і не дублюється)
    // ===================================================================

    private static final String[] SAMPLE_EXPRESSIONS = {
            "", "x", "2*x + 3", "2*x^2 + 3*x - 5", "sin(x) + sqrt(y)", "-5", "3--5", "(-5)",
            "1 + 2..5 * x", "x@#y", "12abc3", "  \t ", "a_1*_b2/3.75^-2", "x😀y",
    };

    @Test
    void lexemesAndTypesHaveSameLength() {
        for (String expr : SAMPLE_EXPRESSIONS) {
            assertEquals(Tokenizer.lexemes(expr).length, Tokenizer.tokenizeTypes(expr).length, expr);
        }
    }

    @Test
    void lexemesConcatenateBackToExpressionWithoutWhitespace() {
        for (String expr : SAMPLE_EXPRESSIONS) {
            assertEquals(new String(Tokenizer.toCharacters(expr)), String.join("", Tokenizer.lexemes(expr)), expr);
        }
    }

    @Test
    void noLexemeIsEmptyOrContainsWhitespace() {
        for (String expr : SAMPLE_EXPRESSIONS) {
            for (String lexeme : Tokenizer.lexemes(expr)) {
                assertFalse(lexeme.isEmpty(), expr);
                assertFalse(lexeme.contains(" ") || lexeme.contains("\t"), expr + " -> '" + lexeme + "'");
            }
        }
    }

    @Test
    void lexemesForC10DemoExample() {
        assertArrayEquals(
                new String[] {"2", "*", "x", "^", "2", "+", "3", "*", "x", "-", "5"},
                Tokenizer.lexemes("2*x^2 + 3*x - 5"));
    }

    // ===================================================================
    // Символи поза BMP (surrogate-пари): одна лексема, а не дві половинки
    // ===================================================================

    @Test
    void surrogatePairIsOneUnknownLexeme() {
        String emoji = "😀"; // 😀
        assertArrayEquals(new String[] {"x", emoji}, Tokenizer.lexemes("x" + emoji));
        assertArrayEquals(new TokenType[] {IDENTIFIER, UNKNOWN}, Tokenizer.tokenizeTypes("x" + emoji));
        assertArrayEquals(new String[] {emoji, emoji}, Tokenizer.lexemes(emoji + emoji));
    }

    @Test
    void mathItalicLetterIsNotAnIdentifier() {
        String mathX = "𝑥"; // 𝑥 (U+1D465) — не ASCII-літера
        assertArrayEquals(new String[] {"2", "*", mathX}, Tokenizer.lexemes("2*" + mathX));
        assertFalse(Tokenizer.isValid("2*" + mathX));
    }

    @Test
    void loneSurrogateIsSingleCharUnknownLexeme() {
        assertArrayEquals(new String[] {"\uD83D", "x"}, Tokenizer.lexemes("\uD83Dx"));
        assertArrayEquals(new String[] {"\uDE00"}, Tokenizer.lexemes("\uDE00"));
        assertArrayEquals(new TokenType[] {UNKNOWN, IDENTIFIER}, Tokenizer.tokenizeTypes("\uD83Dx"));
    }

    @Test
    void newlineIsNotWhitespaceForTokenizer() {
        // C2: пробіл і табуляція — і все; '\n' дає UNKNOWN (так validate-expr його й покаже)
        assertArrayEquals(new TokenType[] {IDENTIFIER, UNKNOWN, PLUS, NUMBER}, Tokenizer.tokenizeTypes("x\n+1"));
    }

    // ===================================================================
    // C8 — correctness check
    // ===================================================================

    @Test
    void validExpressions() {
        assertTrue(Tokenizer.isValid("2*x + 3"));
        assertTrue(Tokenizer.isValid("-5"));
        assertTrue(Tokenizer.isValid("sin(x) + sqrt(y)"));
        assertTrue(Tokenizer.isValid("2 + + 3")); // синтаксис — справа Parser
    }

    @Test
    void invalidTokens() {
        assertFalse(Tokenizer.isValid("2..5"));
        assertFalse(Tokenizer.isValid("3.4.5"));
        assertFalse(Tokenizer.isValid("x@"));
        assertFalse(Tokenizer.isValid(".5"));
    }

    @Test
    void emptyOrBlankIsInvalid() {
        assertFalse(Tokenizer.isValid(""));
        assertFalse(Tokenizer.isValid(" "));
        assertFalse(Tokenizer.isValid("\t"));
    }

    @Test
    void nullThrows() {
        assertThrows(IllegalArgumentException.class, () -> Tokenizer.isValid(null));
    }
}
