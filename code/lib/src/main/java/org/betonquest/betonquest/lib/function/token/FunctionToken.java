package org.betonquest.betonquest.lib.function.token;

import java.util.regex.Matcher;

/**
 * Represents a function token with a type and value.
 *
 * @param type           the type of the token
 * @param value          the value of the token
 * @param containedValue the value of the token without syntactic sugar
 * @since 3.1.0
 */
public record FunctionToken(FunctionTokenType type, String value, String containedValue) {

    /**
     * Creates a new FunctionToken.
     *
     * @param type  the type of the token
     * @param value the value of the token
     * @since 3.1.0
     */
    public FunctionToken(final FunctionTokenType type, final String value) {
        this(type, value, resolveContainedValue(type, value));
    }

    private static String resolveContainedValue(final FunctionTokenType type, final String value) {
        final Matcher matcher = type.getRegex().matcher(value);
        if (!matcher.matches()) {
            return value;
        }
        return matcher.group("value");
    }
}
