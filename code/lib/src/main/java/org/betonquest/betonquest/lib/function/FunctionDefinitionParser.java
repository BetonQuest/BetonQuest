package org.betonquest.betonquest.lib.function;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.function.FunctionDefinition;
import org.betonquest.betonquest.lib.function.token.FunctionToken;

import java.util.List;

/**
 * Provides methods to parse a list of {@link FunctionToken}s into a {@link FunctionDefinition}.
 *
 * @since 3.1.0
 */
public class FunctionDefinitionParser {

    /**
     * Default constructor.
     *
     * @since 3.1.0
     */
    public FunctionDefinitionParser() {
    }

    /**
     * Parse tokens into a {@link FunctionDefinition} by consuming the tokens in the given list.
     *
     * @param functionTokens the tokens to parse
     * @return the parsed {@link FunctionDefinition}
     * @throws QuestException if the function definition is invalid or cannot be parsed
     * @since 3.1.0
     */
    public FunctionDefinition parseDefinition(final List<FunctionToken> functionTokens) throws QuestException {
        final TokenScanner tokenScanner = new TokenScanner(functionTokens);
        if (DefinitionSymbols.DEFINITION.matches(tokenScanner)) {
            return DefinitionSymbols.DEFINITION.parse(tokenScanner);
        }
        throw new QuestException("Invalid function definition.");
    }
}
