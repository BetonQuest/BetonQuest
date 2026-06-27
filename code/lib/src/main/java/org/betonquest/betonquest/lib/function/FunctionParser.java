package org.betonquest.betonquest.lib.function;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.function.FunctionDefinition;
import org.betonquest.betonquest.api.function.FunctionExpression;
import org.betonquest.betonquest.api.function.MathFunction;
import org.betonquest.betonquest.lib.function.token.DefaultTokens;
import org.betonquest.betonquest.lib.function.token.FunctionToken;

import java.util.List;

/**
 * Provides methods to parse a string into a {@link FunctionExpression}.
 *
 * @since 3.1.0
 */
public class FunctionParser {

    /**
     * The parser to parse function definitions.
     */
    private final FunctionDefinitionParser definitionParser;

    /**
     * The parser to parse function expressions.
     */
    private final FunctionExpressionParser expressionParser;

    /**
     * Creates a new FunctionParser with default parsers.
     *
     * @since 3.1.0
     */
    public FunctionParser() {
        this.definitionParser = new FunctionDefinitionParser();
        this.expressionParser = new FunctionExpressionParser();
    }

    /**
     * Parse a string into a {@link FunctionExpression}.
     *
     * @param functionTokens the tokens to parse
     * @return the parsed {@link FunctionExpression}
     * @throws QuestException if the function is invalid or cannot be parsed
     * @since 3.1.0
     */
    public MathFunction parseMathFunction(final List<FunctionToken> functionTokens) throws QuestException {
        final int separatorIndex = functionTokens.indexOf(DefaultTokens.DEFINITION_EXPRESSION_SEPARATOR);
        if (separatorIndex == -1) {
            throw new QuestException("Function definition separator not found.");
        }
        final List<FunctionToken> definitionTokens = functionTokens.subList(0, separatorIndex);
        final List<FunctionToken> expressionTokens = functionTokens.subList(separatorIndex + 1, functionTokens.size());

        if (definitionTokens.isEmpty()) {
            throw new QuestException("Function definition cannot be empty.");
        }
        if (expressionTokens.isEmpty()) {
            throw new QuestException("Function expression cannot be empty.");
        }

        final FunctionDefinition definition = definitionParser.parseDefinition(definitionTokens);
        final FunctionExpression expression = expressionParser.parseExpression(expressionTokens);
        return new DefaultMathFunction(definition, expression);
    }
}
