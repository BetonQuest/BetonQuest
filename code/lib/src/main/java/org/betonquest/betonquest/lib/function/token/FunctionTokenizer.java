package org.betonquest.betonquest.lib.function.token;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Provides methods to tokenize a string into a list of tokens.
 *
 * @since 3.1.0
 */
public class FunctionTokenizer {

    /**
     * Default constructor.
     *
     * @since 3.1.0
     */
    public FunctionTokenizer() {
    }

    /**
     * Tokenize a string into a list of tokens.
     *
     * @param input the string to tokenize
     * @return the list of tokens
     * @since 3.1.0
     */
    public List<FunctionToken> tokenize(final String input) {
        String current = input;
        final List<FunctionToken> tokens = new ArrayList<>();
        while (!current.isEmpty()) {
            current = consumeNextToken(current, tokens);
        }
        return tokens;
    }

    private String consumeNextToken(final String input, final List<FunctionToken> tokens) {
        for (final FunctionTokenType tokenType : FunctionTokenType.values()) {
            final Matcher matcher = tokenType.getRegex().matcher(input);
            if (matcher.find()) {
                final int end = matcher.end();
                final String token = input.substring(0, end);
                tokens.add(new FunctionToken(tokenType, token));
                return input.substring(end);
            }
        }
        throw new IllegalArgumentException("No token found for input: '%s'".formatted(input));
    }
}
