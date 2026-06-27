package org.betonquest.betonquest.lib.function.token;

import javax.annotation.RegEx;

import java.util.regex.Pattern;

/**
 * Defines the different types of tokens that can be used in function expressions.
 *
 * @since 3.1.0
 */
public enum FunctionTokenType {

    /**
     * Matches an identifier.
     *
     * @since 3.1.0
     */
    IDENTIFIER("^\\{(?<value>.+)\\}"),

    /**
     * Matches a string literal.
     *
     * @since 3.1.0
     */
    STRING("^\"(?<value>[^\"]*)\""),

    /**
     * Matches any decimal number.
     *
     * @since 3.1.0
     */
    NUMBER("^(?<value>[0-9]+(\\.[0-9]+)?)"),

    /**
     * Matches any alphanumeric string.
     *
     * @since 3.1.0
     */
    QUALIFIER("^(?<value>[a-zA-Z][a-zA-Z0-9]*)"),

    /**
     * Matches an opening bracket.
     *
     * @since 3.1.0
     */
    OPEN_BRACKET("^(?<value>\\()"),

    /**
     * Matches a closing bracket.
     *
     * @since 3.1.0
     */
    CLOSE_BRACKET("^(?<value>\\))"),

    /**
     * Matches a space character.
     *
     * @since 3.1.0
     */
    SPACE("^(?<value>\\s+)"),

    /**
     * Matches all tokens that are not matched by another token type.
     * They are considered to be operators.
     *
     * @since 3.1.0
     */
    OPERATOR("^(?<value>[^a-zA-Z0-9()\\s])"),

    /**
     * Matches any token.
     * This is used to represent an invalid token.
     *
     * @since 3.1.0
     */
    INVALID("^(?<value>.*)");

    /**
     * The regex pattern.
     */
    private final Pattern regex;

    /**
     * Creates a new FunctionTokenType.
     *
     * @param pattern the regex pattern
     * @since 3.1.0
     */
    FunctionTokenType(@RegEx final String pattern) {
        this.regex = Pattern.compile(pattern);
    }

    /**
     * Gets the regex pattern for this token type.
     *
     * @return the regex pattern
     * @since 3.1.0
     */
    public Pattern getRegex() {
        return this.regex;
    }
}
