package org.betonquest.betonquest.lib.function.token;

/***
 * Provider for expression tokens.
 *
 * @since 3.1.0
 */
@SuppressWarnings("PMD.DataClass")
public final class DefaultTokens {

    /**
     * The token used to separate parameters.
     *
     * @since 3.1.0
     */
    public static final FunctionToken DEFINITION_PARAMETER_LIST_SEPARATOR = new FunctionToken(FunctionTokenType.OPERATOR, ",");

    /**
     * The token used to separate a parameter's default value from its qualifier.
     *
     * @since 3.1.0
     */
    public static final FunctionToken DEFINITION_PARAMETER_DEFAULT_SEPARATOR = new FunctionToken(FunctionTokenType.OPERATOR, ":");

    /**
     * The token used to separate function definitions from their function expressions.
     *
     * @since 3.1.0
     */
    public static final FunctionToken DEFINITION_EXPRESSION_SEPARATOR = new FunctionToken(FunctionTokenType.OPERATOR, "=");

    /**
     * The token used to open a bracket for function expressions.
     *
     * @since 3.1.0
     */
    public static final FunctionToken FUNCTION_OPEN_BRACKET = new FunctionToken(FunctionTokenType.OPEN_BRACKET, "(");

    /**
     * The token used to close a bracket for function expressions.
     *
     * @since 3.1.0
     */
    public static final FunctionToken FUNCTION_CLOSE_BRACKET = new FunctionToken(FunctionTokenType.CLOSE_BRACKET, ")");

    /**
     * The token used to open a bracket for function definitions.
     *
     * @since 3.1.0
     */
    public static final FunctionToken DEFINITION_OPEN_BRACKET = new FunctionToken(FunctionTokenType.OPEN_BRACKET, "(");

    /**
     * The token used to close a bracket for function definitions.
     *
     * @since 3.1.0
     */
    public static final FunctionToken DEFINITION_CLOSE_BRACKET = new FunctionToken(FunctionTokenType.CLOSE_BRACKET, ")");

    /**
     * The token representing the boolean value <code>true</code>.
     *
     * @since 3.1.0
     */
    public static final FunctionToken KEYWORD_TRUE = new FunctionToken(FunctionTokenType.QUALIFIER, "true");

    /**
     * The token representing the boolean value <code>false</code>.
     *
     * @since 3.1.0
     */
    public static final FunctionToken KEYWORD_FALSE = new FunctionToken(FunctionTokenType.QUALIFIER, "false");

    /**
     * The token to represent an invalid token.
     *
     * @since 3.1.0
     */
    public static final FunctionToken INVALID = new FunctionToken(FunctionTokenType.INVALID, "");

    /**
     * The token used to separate list elements.
     *
     * @since 3.1.0
     */
    public static final FunctionToken FUNCTION_PARAMETER_SEPARATOR = new FunctionToken(FunctionTokenType.OPERATOR, ",");

    /**
     * The token used to mathematically invert a value or subtract two values.
     *
     * @since 3.1.0
     */
    public static final FunctionToken MINUS_OPERATOR = new FunctionToken(FunctionTokenType.OPERATOR, "-");

    /**
     * The token used to add two values.
     *
     * @since 3.1.0
     */
    public static final FunctionToken PLUS_OPERATOR = new FunctionToken(FunctionTokenType.OPERATOR, "+");

    /**
     * The token used to divide two values.
     *
     * @since 3.1.0
     */
    public static final FunctionToken MULTIPLY_OPERATOR = new FunctionToken(FunctionTokenType.OPERATOR, "*");

    /**
     * The token used to divide two values.
     *
     * @since 3.1.0
     */
    public static final FunctionToken DIVIDE_OPERATOR = new FunctionToken(FunctionTokenType.OPERATOR, "/");

    /**
     * The token used to calculate the remainder of two values.
     *
     * @since 3.1.0
     */
    public static final FunctionToken MODULO_OPERATOR = new FunctionToken(FunctionTokenType.OPERATOR, "%");

    /**
     * The token used to define the exponent of a value.
     *
     * @since 3.1.0
     */
    public static final FunctionToken POWER_OPERATOR = new FunctionToken(FunctionTokenType.OPERATOR, "^");

    /**
     * The token used to invert a boolean value.
     *
     * @since 3.1.0
     */
    public static final FunctionToken NOT_OPERATOR = new FunctionToken(FunctionTokenType.OPERATOR, "!");

    /**
     * The token used to open a ternary expression.
     *
     * @since 3.1.0
     */
    public static final FunctionToken PRIMARY_TERNARY_OPERATOR = new FunctionToken(FunctionTokenType.OPERATOR, "?");

    /**
     * The token used to provide the alternative value if the ternary condition is false.
     *
     * @since 3.1.0
     */
    public static final FunctionToken SECONDARY_TERNARY_OPERATOR = new FunctionToken(FunctionTokenType.OPERATOR, ":");

    /**
     * The token used to conjunct two boolean values.
     *
     * @since 3.1.0
     */
    public static final FunctionToken CONJUNCTION_OPERATOR = new FunctionToken(FunctionTokenType.OPERATOR, "&");

    /**
     * The token used to disjunct two boolean values.
     *
     * @since 3.1.0
     */
    public static final FunctionToken DISJUNCTION_OPERATOR = new FunctionToken(FunctionTokenType.OPERATOR, "|");

    /**
     * The token used to compare two values if the first is less than the second.
     *
     * @since 3.1.0
     */
    public static final FunctionToken LESS_OPERATOR = new FunctionToken(FunctionTokenType.OPERATOR, "<");

    /**
     * The token used to compare two values if the first is greater than the second.
     *
     * @since 3.1.0
     */
    public static final FunctionToken GREATER_OPERATOR = new FunctionToken(FunctionTokenType.OPERATOR, ">");

    /**
     * The token used to compare two values if the first is equal to the second.
     *
     * @since 3.1.0
     */
    public static final FunctionToken EQUAL_OPERATOR = new FunctionToken(FunctionTokenType.OPERATOR, "=");

    /**
     * Private constructor.
     */
    private DefaultTokens() {

    }
}
