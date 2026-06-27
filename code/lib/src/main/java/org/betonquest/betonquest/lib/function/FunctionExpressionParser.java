package org.betonquest.betonquest.lib.function;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.function.FunctionExpression;
import org.betonquest.betonquest.lib.function.token.FunctionToken;

import java.util.List;

/**
 * Parses a list of {@link FunctionToken}s into a {@link FunctionExpression}.
 *
 * @since 3.1.0
 */
public class FunctionExpressionParser {

    /**
     * Creates a new {@link FunctionExpressionParser}.
     *
     * @since 3.1.0
     */
    public FunctionExpressionParser() {
    }

    /**
     * Parse tokens into a {@link FunctionExpression} by consuming the tokens in the given list.
     *
     * @param functionTokens the tokens to parse
     * @return the parsed {@link FunctionExpression}
     * @throws QuestException if the function expression is invalid or cannot be parsed
     * @since 3.1.0
     */
    public FunctionExpression parseExpression(final List<FunctionToken> functionTokens) throws QuestException {
        final TokenScanner scanner = new TokenScanner(functionTokens);
        return ExpressionSymbols.EXPRESSION.get().parse(scanner);
    }
}
