package org.betonquest.betonquest.util.math;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.instruction.argument.parser.NumberParser;
import org.betonquest.betonquest.api.service.placeholder.PlaceholderManager;
import org.betonquest.betonquest.lib.instruction.argument.DefaultArgument;
import org.betonquest.betonquest.quest.placeholder.math.MathPlaceholder;
import org.betonquest.betonquest.util.math.tokens.AbsoluteValue;
import org.betonquest.betonquest.util.math.tokens.Negation;
import org.betonquest.betonquest.util.math.tokens.Number;
import org.betonquest.betonquest.util.math.tokens.Operation;
import org.betonquest.betonquest.util.math.tokens.Parenthesis;
import org.betonquest.betonquest.util.math.tokens.Token;
import org.betonquest.betonquest.util.math.tokens.Variable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helps the {@link MathPlaceholder} with parsing mathematical expressions.
 *
 * @deprecated This should be replaced with a real expression parsing lib
 */
@Deprecated
@SuppressWarnings("ParameterName")
public class Tokenizer {

    /**
     * Floating point regular expression. It matches only if the tested string immediately starts with the number and
     * will not match any characters at the end that aren't part of the number.
     */
    private static final Pattern FP_REGEX = Pattern.compile("^[+-]?(?:NaN|Infinity|(?:0[xX](?:\\p{XDigit}+(?:\\.\\p{XDigit}*)?|\\.\\p{XDigit}+)[pP][+-]?\\p{Digit}+|(?:\\p{Digit}+(?:\\.\\p{Digit}*)?|\\.\\p{Digit}+)(?:[eE][+-]?\\p{Digit}+)?)[fFdD]?)");

    /**
     * Backslash escapement regular expression for removing escaping. Use {@link Matcher#replaceAll(String)} with
     * {@code "$1"} as argument.
     */
    private static final Pattern ESCAPE_REGEX = Pattern.compile("\\\\(.)");

    /**
     * The {@link PlaceholderManager} to create and resolve placeholders.
     */
    private final PlaceholderManager placeholders;

    /**
     * Name of the package in which the tokenizer is operating.
     */
    private final QuestPackage pack;

    /**
     * Create a new Tokenizer in given package.
     *
     * @param placeholders the {@link PlaceholderManager} to create and resolve placeholders
     * @param pack         name of the package
     */
    public Tokenizer(final PlaceholderManager placeholders, final QuestPackage pack) {
        this.placeholders = placeholders;
        this.pack = pack;
    }

    /**
     * Parse the given mathematical expression into a so-called token.
     * This token can then be used to resolve the expression at runtime for a specific player.
     *
     * @param expression the expression that should be parsed
     * @return expression parsed as token
     * @throws QuestException if the expression is invalid and therefore couldn't be parsed
     */
    public Token tokenize(final String expression) throws QuestException {
        final String val = expression.replaceAll("\\s", "");
        if (val.isEmpty()) {
            throw new QuestException("missing calculation");
        }
        final ParsedToken parsed = tokenizeChain(val, 0, Integer.MIN_VALUE, false);
        if (parsed.next() < val.length()) {
            throw new QuestException("invalid calculation (unbalanced parenthesis)");
        }
        return parsed.token();
    }

    /**
     * Parses a chain of values and operators with the given minimal operator priority.
     * <p>
     * Values of operators with a higher priority are parsed recursively, so the resulting token honours the operator
     * priorities. Parsing stops at the end of the given string, at a closing parenthesis or bracket, or at the first
     * operator with a lower priority than the given one.
     *
     * @param val           the string containing the expression that still needs to be parsed
     * @param start         the index of the first character of the chain
     * @param minPriority   the minimal priority an operator needs to be part of this chain
     * @param afterOperator whether the chain starts right after an operator
     * @return the parsed token and the index of the first character that was not consumed
     * @throws QuestException if the expression is invalid and therefore couldn't be parsed
     */
    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    private ParsedToken tokenizeChain(final String val, final int start, final int minPriority, final boolean afterOperator) throws QuestException {
        final ParsedToken first = parseValue(val, start, afterOperator);
        Token result = first.token();
        int index = first.next();
        while (index < val.length()) {
            final char chr = val.charAt(index);
            if (chr == ')' || chr == ']') {
                break;
            }
            if (chr == '}') {
                throw new QuestException("invalid calculation (unbalanced curly brace)");
            }
            if (!Operator.isOperator(chr)) {
                throw new QuestException("invalid calculation (missing operator)");
            }
            final Operator operator = Operator.valueOf(chr);
            if (operator.getPriority() < minPriority) {
                break;
            }
            final ParsedToken operand = tokenizeChain(val, index + 1, operator.getPriority() + 1, true);
            result = new Operation(result, operator, operand.token());
            index = operand.next();
        }
        return new ParsedToken(result, index);
    }

    /**
     * Parses the single value starting at the given index.
     * <p>
     * Depending on the first character the value is a number, a variable, a parenthesis, a bracket or an absolute
     * value. A value may be preceded by a negation.
     *
     * @param val           the string containing the expression that still needs to be parsed
     * @param start         the index of the first character of the value
     * @param afterOperator whether the value starts right after an operator
     * @return the parsed token and the index of the first character that was not consumed
     * @throws QuestException if the expression is invalid and therefore couldn't be parsed
     */
    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.AvoidLiteralsInIfCondition", "PMD.CognitiveComplexity",
            "PMD.AvoidDeeplyNestedIfStmts"})
    private ParsedToken parseValue(final String val, final int start, final boolean afterOperator) throws QuestException {
        if (start >= val.length()) {
            throw new QuestException("invalid calculation (operator missing second value)");
        }

        int valueStart = start;
        int index = start;
        char chr = val.charAt(index++);
        boolean isNegated = false;

        if (chr == '-') {
            if (val.length() == index) {
                throw new QuestException("invalid calculation (negation missing value)");
            }
            chr = val.charAt(index++);
            isNegated = true;
            valueStart++;
        }

        final Token nextInLine;
        if (chr == '{') {
            index = findCurlyBraceVariableEnd(val, index);
            final String rawVariableName = val.substring(valueStart + 1, index);
            nextInLine = createVariable(ESCAPE_REGEX.matcher(rawVariableName).replaceAll("$1"));
            index++;
        } else if (chr == '(' || chr == '[') {
            index = findParenthesisEnd(val, index);
            if (index == valueStart + 1) {
                throw new QuestException("invalid calculation (empty parenthesis)");
            }
            final char opening = chr;
            chr = val.charAt(index);
            if (opening == '(' && chr != ')' || opening == '[' && chr != ']') {
                throw new QuestException("invalid calculation (parenthesis / brackets mismatch)");
            }
            nextInLine = new Parenthesis(tokenize(val.substring(valueStart + 1, index)), opening, chr);
            index++;
        } else if (chr == '|') {
            index = findAbsoluteEnd(val, index);
            if (index == valueStart + 1) {
                throw new QuestException("invalid calculation (empty absolute value)");
            }
            nextInLine = new AbsoluteValue(tokenize(val.substring(valueStart + 1, index)));
            index++;
        } else {
            final Matcher numberMatcher = FP_REGEX.matcher(val);
            numberMatcher.region(start, val.length());
            if (numberMatcher.lookingAt()) {
                isNegated = false;
                index = numberMatcher.end();
                nextInLine = new Number(Double.parseDouble(numberMatcher.group()));
            } else if (Operator.isOperator(chr)) {
                if (!afterOperator) {
                    throw new QuestException("invalid calculation (operator missing first value)");
                }
                throw new QuestException("invalid calculation (doubled operators)");
            } else {
                for (; index < val.length(); index++) {
                    chr = val.charAt(index);
                    if (Operator.isOperator(chr) || "{([|])}".contains(String.valueOf(chr))) {
                        break;
                    }
                }
                nextInLine = createVariable(val.substring(valueStart, index));
            }
        }

        if (isNegated) {
            return new ParsedToken(new Negation(nextInLine), index);
        }
        return new ParsedToken(nextInLine, index);
    }

    /**
     * Creates a variable token for the given placeholder name.
     *
     * @param variableName the name of the placeholder without the surrounding percentage signs
     * @return the created variable token
     * @throws QuestException if the variable couldn't be created
     */
    private Token createVariable(final String variableName) throws QuestException {
        try {
            return new Variable(new DefaultArgument<>(placeholders, pack, "%" + variableName + "%", NumberParser.DEFAULT));
        } catch (final QuestException e) {
            throw new QuestException("invalid calculation (" + e.getMessage() + ")", e);
        }
    }

    private int findAbsoluteEnd(final String val, final int startIndex) throws QuestException {
        int index = startIndex;
        for (; index < val.length(); index++) {
            switch (val.charAt(index)) {
                case '{':
                    index = findCurlyBraceVariableEnd(val, index + 1);
                    break;
                case '(':
                case '[':
                    index = findParenthesisEnd(val, index + 1);
                    break;
                case '|':
                    return index;
                default:
                    break;
            }
        }
        throw new QuestException("invalid calculation (unbalanced absolute value)");
    }

    private int findParenthesisEnd(final String val, final int startIndex) throws QuestException {
        int index = startIndex;
        int depth = 1;
        for (; index < val.length(); index++) {
            switch (val.charAt(index)) {
                case '{':
                    index = findCurlyBraceVariableEnd(val, index + 1);
                    break;
                case '(':
                case '[':
                    depth++;
                    break;
                case ')':
                case ']':
                    depth--;
                    if (depth == 0) {
                        return index;
                    }
                    break;
                default:
                    break;
            }
        }
        throw new QuestException("invalid calculation (unbalanced parenthesis)");
    }

    private int findCurlyBraceVariableEnd(final String val, final int startIndex) throws QuestException {
        int index = startIndex;
        for (; index < val.length(); index++) {
            switch (val.charAt(index)) {
                case '\\':
                    index++;
                    break;
                case '}':
                    return index;
                default:
                    break;
            }
        }
        throw new QuestException("invalid calculation (unbalanced curly brace)");
    }

    /**
     * A parsed part of an expression together with the index of the first character that was not consumed.
     *
     * @param token the parsed token
     * @param next  the index of the first character that was not consumed
     */
    private record ParsedToken(Token token, int next) {

    }
}
