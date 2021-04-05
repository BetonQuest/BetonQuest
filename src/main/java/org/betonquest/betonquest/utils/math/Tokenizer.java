package org.betonquest.betonquest.utils.math;

import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.math.tokens.*;
import org.betonquest.betonquest.utils.math.tokens.Number;
import org.betonquest.betonquest.variables.MathVariable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helps the {@link MathVariable} with parsing mathematical expressions.
 *
 * @deprecated This should be replaced in BQ 2.0 with a real expression parsing lib like
 * https://github.com/fasseg/exp4j
 */
@Deprecated
@SuppressWarnings({"PMD.GodClass"})
public class Tokenizer {

    /**
     * Floating point regular expression. It matches only if the tested string immediately starts with the number and
     * will not match any characters at the end that aren't part of the number.
     */
    private static final Pattern FP_REGEX = Pattern.compile("^[+-]?(?:NaN|Infinity|(?:0[xX](?:\\p{XDigit}+(?:\\.\\p{XDigit}*)?|\\.\\p{XDigit}+)[pP][+-]?\\p{Digit}+|(?:\\p{Digit}+(?:\\.\\p{Digit}*)?|\\.\\p{Digit}+)(?:[eE][+-]?\\p{Digit}+)?)[fFdD]?)");

    /**
     * Name of the package in which the tokenizer is operating.
     */
    private final String packageName;

    /**
     * Create a new Tokenizer in given package.
     *
     * @param packageName name of the package
     */
    public Tokenizer(final String packageName) {
        this.packageName = packageName;
    }

    /**
     * Parse the given mathematical expression into a so-called token.
     * This token can then be used to resolve the expression at runtime for a specific player.
     *
     * @param expression the expression that should be parsed
     * @return expression parsed as token
     * @throws InstructionParseException if the expression is invalid and therefore couldn't be parsed
     */
    public Token tokenize(final String expression) throws InstructionParseException {
        return tokenize(null, null, expression.replaceAll("\\s", ""));
    }

    /**
     * Internal method for recursive token parsing.
     * <p>
     * The method will walk through the expression from the left side to the right side.
     * Depending on the first character the type of the left token will be determined.
     * Between two tokens there must always be an operator.
     *
     * @param val1     token left of the given string, or null if {@code val2} will be first token
     * @param operator operator between the token ({@code val1}) and the string ({@code val2})
     * @param val2     string containing the rest of the expression that still needs to be parsed
     * @return parsed token
     * @throws InstructionParseException if the expression is invalid and therefore couldn't be parsed
     */
    @SuppressWarnings({"PMD.AssignmentInOperand", "PMD.CyclomaticComplexity", "PMD.NPathComplexity", "PMD.AvoidLiteralsInIfCondition",
            "PMD.NcssCount", "PMD.ExcessiveMethodLength"})
    private Token tokenize(final Token val1, final Operator operator, final String val2) throws InstructionParseException {
        if (val2.isEmpty()) {
            if (operator != null) {
                throw new InstructionParseException("invalid calculation (operator missing second value)");
            }
            throw new InstructionParseException("missing calculation");
        }

        //Parse next token in line (from left to right)
        int start = 0;
        int index = start;
        char chr = val2.charAt(index++);
        boolean isNegated = false;

        if (chr == '-') {
            if (val2.length() == 1) {
                throw new InstructionParseException("invalid calculation (negation missing value)");
            }
            chr = val2.charAt(index++);
            isNegated = true;
            start++;
        }

        Token nextInLine = null;
        final Matcher numberMatcher;
        if (chr == '(' || chr == '[') { //tokenize parenthesis
            int depth = 1;
            final char opening = chr;
            for (; index < val2.length(); index++) {
                chr = val2.charAt(index);
                if (chr == '(' || chr == '[') {
                    depth++;
                } else if (chr == ')' || chr == ']') {
                    depth--;
                }

                if (depth == 0) {
                    if (index == start + 1) {
                        throw new InstructionParseException("invalid calculation (empty parenthesis)");
                    }
                    if (opening == '(' && chr != ')' || opening == '[' && chr != ']') {
                        throw new InstructionParseException("invalid calculation (parenthesis / brackets mismatch)");
                    }

                    nextInLine = new Parenthesis(tokenize(null, null, val2.substring(start + 1, index)), opening, chr);
                    break;
                }
            }
            if (nextInLine == null) {
                throw new InstructionParseException("invalid calculation (unbalanced parenthesis)");
            }


        } else if (chr == '|') { //tokenize absolute values
            int depth = 0;
            for (; index < val2.length(); index++) {
                chr = val2.charAt(index);
                if (chr == '(' || chr == '[') {
                    depth++;
                } else if (chr == ')' || chr == ']') {
                    depth--;
                }

                if (depth == 0 && chr == '|') {
                    if (index == start + 1) {
                        throw new InstructionParseException("invalid calculation (empty absolute value)");
                    }

                    nextInLine = new AbsoluteValue(tokenize(null, null, val2.substring(start + 1, index)));
                    break;
                }
            }
            if (nextInLine == null) {
                throw new InstructionParseException("invalid calculation (unbalanced absolute value)");
            }

        } else if ((numberMatcher = FP_REGEX.matcher(val2)).find()) { //tokenize numbers
            isNegated = false;
            index = numberMatcher.end() - 1;
            nextInLine = new Number(Double.parseDouble(numberMatcher.group()));

        } else if (Operator.isOperator(chr)) { //error handling
            if (operator == null) {
                throw new InstructionParseException("invalid calculation (operator missing first value)");
            }
            throw new InstructionParseException("invalid calculation (doubled operators)");

        } else { //tokenize variables
            for (; index < val2.length(); index++) {
                chr = val2.charAt(index);
                if (Operator.isOperator(chr) || "([|])".contains(String.valueOf(chr))) {
                    break;
                }
            }
            try {
                nextInLine = new Variable(new VariableNumber(packageName, "%" + val2.substring(start, index--) + "%"));
            } catch (InstructionParseException e) {
                throw new InstructionParseException("invalid calculation (" + e.getMessage() + ")", e);
            }
        }

        if (isNegated) {
            nextInLine = new Negation(nextInLine);
        }

        if (index < val2.length() - 1) {
            chr = val2.charAt(++index);
            if (!Operator.isOperator(chr)) {
                if (chr == ')' || chr == ']') {
                    throw new InstructionParseException("invalid calculation (unbalanced parenthesis)");
                }
                throw new InstructionParseException("invalid calculation (missing operator)");
            }
            final Operator nextOperator = Operator.valueOf(chr);
            final String newVal = val2.substring(++index);

            //no token left of this token, parse next
            if (operator == null) {
                return tokenize(nextInLine, nextOperator, newVal);
            }

            //next operation has higher priority, tokenize it first
            if (nextOperator.getPriority() > operator.getPriority()) {
                return new Operation(val1, operator, tokenize(nextInLine, nextOperator, newVal));
            }

            //next operation has lower priority, tokenize this first
            return tokenize(new Operation(val1, operator, nextInLine), nextOperator, newVal);
        } else {
            if (operator == null) {
                return nextInLine;
            } else {
                return new Operation(val1, operator, nextInLine);
            }
        }
    }
}
