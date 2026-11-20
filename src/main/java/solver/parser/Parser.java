package solver.parser;

import solver.algebra.Expression;
import solver.core.Messages;
import solver.core.ParseException;
import solver.core.Token;
import solver.core.TokenStream;
import solver.core.TokenType;
import solver.core.Tokenizer;

/**
 * C20: контракт майбутнього парсера.
 *
 * <p>Архітектурна межа: {@code Tokenizer != Parser}.
 * <ul>
 *   <li>{@link Tokenizer} відповідає на питання «які елементи містить текст?»
 *       ({@code String -> Token[]});</li>
 *   <li>{@code Parser} — «яка математична структура цих елементів?»
 *       ({@code TokenStream -> Expression}).</li>
 * </ul>
 *
 * <pre>
 * String -> Tokenizer -> Token[] -> TokenStream -> Parser -> Expression -> Solver
 * </pre>
 *
 * <p>Цільова граматика (EBNF), яку реалізуватиме наступна версія:
 * <pre>
 * expression = term   { ("+" | "-") term } ;
 * term       = power  { ("*" | "/") power } ;
 * power      = unary  [ "^" power ] ;            (* права асоціативність *)
 * unary      = [ "-" ] unary | primary ;
 * primary    = NUMBER
 *            | IDENTIFIER [ "(" expression ")" ] (* змінна або виклик функції *)
 *            | "(" expression ")" ;
 * </pre>
 *
 * <p>Що вже гарантує контракт (поточна версія):
 * <ul>
 *   <li>{@code null} на вході — {@link IllegalArgumentException};</li>
 *   <li>порожній вираз (одразу {@code EOF}) — {@link ParseException};</li>
 *   <li>лексична помилка ({@code UNKNOWN}-токен) — {@link ParseException} з позицією токена;</li>
 *   <li>коректний набір токенів — {@link UnsupportedOperationException}, доки не
 *       реалізовано повний розбір (потрібні класи A12–A14 з пакета {@code algebra}).</li>
 * </ul>
 *
 * <p>Парсер не має стану між викликами: усе, що змінюється під час розбору, —
 * це позиція курсора в переданому {@link TokenStream}.
 */
public class Parser {

    /**
     * Будує дерево виразу з потоку токенів, починаючи з поточної позиції.
     *
     * @param tokens потік токенів (не {@code null})
     * @return корінь дерева виразу
     * @throws IllegalArgumentException      якщо {@code tokens == null}
     * @throws ParseException                якщо вираз порожній або містить {@code UNKNOWN}-токен
     * @throws UnsupportedOperationException поки повний розбір не реалізовано
     */
    public Expression parse(TokenStream tokens) {
        if (tokens == null) {
            throw new IllegalArgumentException(Messages.ERR_NULL_TOKEN_STREAM);
        }
        if (tokens.current().getType() == TokenType.EOF) {
            throw new ParseException(Messages.ERR_EMPTY_EXPRESSION, tokens.position());
        }
        rejectUnknownTokens(tokens);

        // TODO(next lab): expression() за граматикою вище + consume(EOF) наприкінці.
        throw new UnsupportedOperationException(Messages.ERR_PARSER_NOT_IMPLEMENTED);
    }

    /**
     * Зручна точка входу для CLI: {@code String -> Tokenizer -> TokenStream -> parse}.
     *
     * @throws IllegalArgumentException якщо {@code expression == null}
     */
    public Expression parse(String expression) {
        if (expression == null) {
            throw new IllegalArgumentException(Messages.ERR_NULL_EXPRESSION);
        }
        return parse(new TokenStream(Tokenizer.tokenize(expression)));
    }

    /** Переглядає потік через peek(offset), не зсуваючи курсор. */
    private static void rejectUnknownTokens(TokenStream tokens) {
        for (int offset = 0; ; offset++) {
            Token token = tokens.peek(offset);
            if (token.getType() == TokenType.EOF) {
                return;
            }
            if (token.getType() == TokenType.UNKNOWN) {
                int position = tokens.position() + offset;
                throw new ParseException(
                        String.format(Messages.ERR_UNKNOWN_TOKEN_AT, token.getLexeme(), position),
                        position);
            }
        }
    }
}
