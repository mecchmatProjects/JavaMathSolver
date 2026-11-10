package solver.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import solver.core.ParseException;
import solver.core.Token;
import solver.core.TokenStream;
import solver.core.TokenType;
import solver.core.Tokenizer;

/**
 * T5: межа Tokenizer ≠ Parser на повному конвеєрі
 * String -> Tokenizer -> Token[] -> TokenStream -> Parser.
 */
class ParserPipelineTest {

    private final Parser parser = new Parser();

    @Test
    void parserDoesNotConsumeTheStreamOnRejectedInput() {
        TokenStream s = new TokenStream(Tokenizer.tokenize("2*x^2 + 3*x - 5"));
        assertThrows(UnsupportedOperationException.class, () -> parser.parse(s));

        // курсор не зрушено: потік можна пройти повністю після виклику Parser
        assertEquals(0, s.position());
        int count = 0;
        while (s.hasNext()) {
            s.next();
            count++;
        }
        assertEquals(11, count);
    }

    @Test
    void parserStartsFromCurrentPositionOfSharedStream() {
        // "2 + @": після споживання "2 +" Parser бачить UNKNOWN саме на позиції 2
        TokenStream s = new TokenStream(Tokenizer.tokenize("2 + @"));
        s.consume(TokenType.NUMBER);
        s.consume(TokenType.PLUS);

        ParseException e = assertThrows(ParseException.class, () -> parser.parse(s));
        assertEquals(2, e.getPosition());
        assertEquals(2, s.position());
    }

    @Test
    void stringAndStreamEntryPointsAgree() {
        String[] invalid = {"", "   ", "x@", "2..5", "1 + 3.4.5"};
        for (String expression : invalid) {
            ParseException fromString = assertThrows(ParseException.class, () -> parser.parse(expression));
            ParseException fromStream = assertThrows(ParseException.class,
                    () -> parser.parse(new TokenStream(Tokenizer.tokenize(expression))));
            assertEquals(fromStream.getMessage(), fromString.getMessage(), expression);
            assertEquals(fromStream.getPosition(), fromString.getPosition(), expression);
        }
    }

    @Test
    void tokenizerNeverEmitsEofSoParserOwnsEndOfInput() {
        for (Token t : Tokenizer.tokenize("sin(x) + sqrt(y)")) {
            assertNotEquals(TokenType.EOF, t.getType());
        }
    }
}
