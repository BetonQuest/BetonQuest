package org.betonquest.betonquest.utils.math;

import org.betonquest.betonquest.exceptions.QuestException;

import java.util.Arrays;

/**
 * Operators that can be used between two tokens for performing an arithmetic expression.
 *
 * @deprecated This should be replaced in BQ 2.0 with a real expression parsing lib like
 * <a href="https://github.com/fasseg/exp4j">fasseg/exp4j</a>
 */
@Deprecated
public enum Operator {

    /**
     * <b>+</b> Operator, adds two values.
     */
    PLUS('+', 1) {
        @Override
        public double calculate(final double val1, final double val2) {
            return val1 + val2;
        }
    },

    /**
     * <b>-</b> Operator, subtracts two values.
     */
    MINUS('-', 1) {
        @Override
        public double calculate(final double val1, final double val2) {
            return val1 - val2;
        }
    },

    /**
     * <b>*</b> Operator, multiplies two values.
     */
    MULTIPLY('*', 2) {
        @Override
        public double calculate(final double val1, final double val2) {
            return val1 * val2;
        }
    },

    /**
     * <b>/</b> Operator, divides a value by another.
     */
    DIVIDE('/', 2) {
        @Override
        public double calculate(final double val1, final double val2) {
            return val1 / val2;
        }
    },

    /**
     * <b>%</b> Operator, returns the modulo of two values.
     */
    MODULO('%', 2) {
        @Override
        public double calculate(final double val1, final double val2) {
            return val1 % val2;
        }
    },

    /**
     * <b>^</b> Operator, returns the power of a value.
     */
    POW('^', 3) {
        @Override
        public double calculate(final double val1, final double val2) {
            return Math.pow(val1, val2);
        }
    },

    /**
     * <b>~</b> Operator, rounds the first value to the number of decimal digits given by the second value.
     */
    ROUND('~', 0) {
        @Override
        public double calculate(final double val1, final double val2) {
            //Make sure that edge cases (like 0.5) are always rounded up, not down:
            final double corrected = Double.longBitsToDouble(Double.doubleToLongBits(val1) + 1);
            final double exp = Math.pow(10, Math.round(val2));
            return Math.round(corrected * exp) / exp;
        }
    };

    /**
     * Symbol representing the operator.
     */
    private final char symbol;

    /**
     * Priority of the operation (power before point before line).
     */
    private final int priority;

    Operator(final char symbol, final int priority) {
        this.symbol = symbol;
        this.priority = priority;
    }

    /**
     * Finds the Operator represented by the given symbol.
     *
     * @param chr the symbol representing an operator
     * @return the operator of the symbol
     * @throws QuestException if no operator exists for the given character
     */
    public static Operator valueOf(final char chr) throws QuestException {
        return Arrays.stream(values()).filter(operator -> operator.symbol == chr).findAny()
                .orElseThrow(() -> new QuestException(chr + " is not a valid operator"));
    }

    /**
     * Checks if the given character represents an Operator.
     *
     * @param chr character to check
     * @return true if the character is an operator, false otherwise
     */
    public static boolean isOperator(final char chr) {
        return Arrays.stream(values()).anyMatch(operator -> operator.symbol == chr);
    }

    /**
     * Returns the Symbol representing the operator.
     *
     * @return the symbol character
     */
    public char getSymbol() {
        return symbol;
    }

    /**
     * Returns the priority of the operator (power before point before line).
     *
     * @return the priority (higher should be calculated first)
     */
    public int getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return String.valueOf(getSymbol());
    }

    /**
     * Performs the operation on two values.
     *
     * @param val1 first value
     * @param val2 second value
     * @return result of the operation
     */
    public abstract double calculate(double val1, double val2);
}
