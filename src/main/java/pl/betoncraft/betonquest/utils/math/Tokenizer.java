package pl.betoncraft.betonquest.utils.math;

import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.math.tokens.AbsoluteValue;
import pl.betoncraft.betonquest.utils.math.tokens.Number;
import pl.betoncraft.betonquest.utils.math.tokens.Operation;
import pl.betoncraft.betonquest.utils.math.tokens.Parenthesis;
import pl.betoncraft.betonquest.utils.math.tokens.Token;
import pl.betoncraft.betonquest.utils.math.tokens.Variable;
import pl.betoncraft.betonquest.variables.MathVariable;

import java.text.DecimalFormat;

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
     * Decimal separator allowed in numbers
     */
    private final char decimalSeparator;

    /**
     * Name of the package in which the tokenizer is operating
     */
    private final String packageName;

    /**
     * Create a new Tokenizer in given package with given decimal separator
     *
     * @param packageName      name of the package
     * @param decimalSeparator decimal separator char (, or .)
     */
    public Tokenizer(final String packageName, final char decimalSeparator) {
        this.packageName = packageName;
        this.decimalSeparator = decimalSeparator;
    }

    /**
     * Create a new Tokenizer in given package with system default decimal separator
     *
     * @param packageName name of the package
     */
    public Tokenizer(final String packageName) {
        this(
                packageName,
                ((DecimalFormat) DecimalFormat.getInstance()).getDecimalFormatSymbols().getDecimalSeparator()
        );
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
     * Internal method for recursive token parsing
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
    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity", "PMD.AvoidLiteralsInIfCondition",
            "PMD.NcssCount", "PMD.ExcessiveMethodLength"})
    private Token tokenize(final Token val1, final Operator operator, final String val2) throws InstructionParseException {
        if (val2.isEmpty() && operator != null) {
            throw new InstructionParseException("invalid calculation (operator missing second value)");
        } else if (val2.isEmpty()) {
            throw new InstructionParseException("missing calculation");
        }

        //Parse next token in line (from left to right)
        Token nextInLine = null;
        int index = 0;
        char chr = val2.charAt(index++);

        if (chr == '(' || chr == '[') { //tokenize parenthesis
            int depth = 1;
            for (; index < val2.length(); index++) {
                chr = val2.charAt(index);
                if (chr == '(' || chr == '[') {
                    depth++;
                } else if (chr == ')' || chr == ']') {
                    depth--;
                }

                if (depth == 0) {
                    if (index == 1) {
                        throw new InstructionParseException("invalid calculation (empty parenthesis)");
                    }

                    nextInLine = new Parenthesis(tokenize(null, null, val2.substring(1, index)));
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
                    if (index == 1) {
                        throw new InstructionParseException("invalid calculation (empty absolute value)");
                    }

                    nextInLine = new AbsoluteValue(tokenize(null, null, val2.substring(1, index)));
                    break;
                }
            }
            if (nextInLine == null) {
                throw new InstructionParseException("invalid calculation (unbalanced absolute value)");
            }

        } else if (Character.isDigit(chr) || chr == '-') { //tokenize numbers
            for (; index < val2.length(); index++) {
                chr = val2.charAt(index);
                if (!Character.isDigit(chr) && chr != decimalSeparator) {
                    break;
                }
            }
            try {
                nextInLine = new Number(Double.parseDouble(val2.substring(0, index--)));
            } catch (NumberFormatException e) {
                throw new InstructionParseException("invalid calculation (invalid number)", e);
            }

        } else if (Character.isAlphabetic(chr)) { //tokenize variables
            //FIXME this assumes all variables start with an alphabetic character
            for (; index < val2.length(); index++) {
                chr = val2.charAt(index);
                if (Operator.isOperator(chr) || "([|])".contains(String.valueOf(chr))) {
                    break;
                }
            }
            try {
                nextInLine = new Variable(new VariableNumber(packageName, "%" + val2.substring(0, index--) + "%"));
            } catch (InstructionParseException e) {
                throw new InstructionParseException("invalid calculation (" + e.getMessage() + ")", e);
            }

        } else { //error handling
            if (chr == ')' || chr == ']') {
                throw new InstructionParseException("invalid calculation (unbalanced parenthesis)");
            } else if (Operator.isOperator(chr) && operator != null) {
                throw new InstructionParseException("invalid calculation (doubled operators)");
            } else if (Operator.isOperator(chr) && operator == null) {
                throw new InstructionParseException("invalid calculation (operator missing first value)");
            } else {
                throw new InstructionParseException("invalid calculation (unknown reason)");
            }
        }

        if (index < val2.length() - 1) {
            try {
                chr = val2.charAt(++index);
                if (!Operator.isOperator(chr)) {
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

            } catch (InstructionParseException e) {
                throw new InstructionParseException(e.getMessage(), e);
            }
        } else {
            if (operator == null) {
                return nextInLine;
            } else {
                return new Operation(val1, operator, nextInLine);
            }
        }
    }
}
