package solver.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import solver.core.Messages;
import solver.core.ParseException;
import solver.core.Token;
import solver.core.TokenStream;
import solver.core.TokenType;
import solver.core.Tokenizer;

class ParserTest {

    private final Parser parser = new Parser();

    private static TokenStream stream(String expression) {
        return new TokenStream(Tokenizer.tokenize(expression));
    }

    @Test
    void nullStreamIsRejected() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> parser.parse((TokenStream) null));
        assertEquals(Messages.ERR_NULL_TOKEN_STREAM, e.getMessage());
    }

    @Test
    void nullStringIsRejected() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> parser.parse((String) null));
        assertEquals(Messages.ERR_NULL_EXPRESSION, e.getMessage());
    }

    @Test
    void emptyStreamIsParseError() {
        ParseException e = assertThrows(ParseException.class,
                () -> parser.parse(new TokenStream(new Token[0])));
        assertEquals(Messages.ERR_EMPTY_EXPRESSION, e.getMessage());
        assertEquals(0, e.getPosition());
    }

    @Test
    void blankStringIsParseError() {
        assertThrows(ParseException.class, () -> parser.parse("   "));
    }

    @Test
    void fullyConsumedStreamIsEmpty() {
        TokenStream s = stream("2");
        s.next();
        ParseException e = assertThrows(ParseException.class, () -> parser.parse(s));
        assertEquals(1, e.getPosition());
    }

    @Test
    void unknownTokenReportedWithPosition() {
        // NUMBER(2) PLUS(+) IDENTIFIER(x) UNKNOWN(@)
        ParseException e = assertThrows(ParseException.class, () -> parser.parse("2 + x@"));
        assertEquals(3, e.getPosition());
        assertEquals(String.format(Messages.ERR_UNKNOWN_TOKEN_AT, "@", 3), e.getMessage());
    }

    @Test
    void firstUnknownTokenWins() {
        // UNKNOWN(2..5) PLUS(+) IDENTIFIER(x) UNKNOWN(@)
        ParseException e = assertThrows(ParseException.class, () -> parser.parse("2..5 + x@"));
        assertEquals(0, e.getPosition());
    }

    @Test
    void unknownCheckStartsFromCurrentPosition() {
        TokenStream s = new TokenStream(new Token[] {
                new Token(TokenType.NUMBER, "1"),
                new Token(TokenType.PLUS, "+"),
                new Token(TokenType.UNKNOWN, "@")
        });
        s.next();
        ParseException e = assertThrows(ParseException.class, () -> parser.parse(s));
        assertEquals(2, e.getPosition());
    }

    @Test
    void validationDoesNotMoveCursor() {
        TokenStream s = stream("2*x + 3");
        assertThrows(UnsupportedOperationException.class, () -> parser.parse(s));
        assertEquals(0, s.position());
    }

    @Test
    void validExpressionIsNotImplementedYet() {
        UnsupportedOperationException e = assertThrows(UnsupportedOperationException.class,
                () -> parser.parse("2*x^2 + 3*x - 5"));
        assertEquals(Messages.ERR_PARSER_NOT_IMPLEMENTED, e.getMessage());
    }
}
