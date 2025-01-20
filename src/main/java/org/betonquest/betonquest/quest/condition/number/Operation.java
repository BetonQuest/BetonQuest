package org.betonquest.betonquest.quest.condition.number;

import org.betonquest.betonquest.api.quest.QuestException;

/**
 * The type of compare.
 */
public enum Operation {

    /**
     * Checks if the first number is less than the second.
     */
    LESS((first, second) -> first < second),
    /**
     * Checks if the first number is less or equal to the second.
     */
    LESS_EQUAL((first, second) -> first <= second),
    /**
     * Checks if the first number is equal to the second.
     */
    EQUAL((first, second) -> first == second),
    /**
     * Checks if the first number is not equal to the second.
     */
    NOT_EQUAL((first, second) -> first != second),
    /**
     * Checks if the first number is greater or equal to the second.
     */
    GREATER_EQUAL((first, second) -> first >= second),
    /**
     * Checks if the first number is greater than the second.
     */
    GREATER((first, second) -> first > second);

    /**
     * The compare to use for this operation.
     */
    private final Compare compare;

    Operation(final Compare compare) {
        this.compare = compare;
    }

    /**
     * Converts a symbol to an operation.
     *
     * @param symbol the symbol to convert
     * @return the operation
     * @throws QuestException when the symbol is unknown
     */
    public static Operation fromSymbol(final String symbol) throws QuestException {
        return switch (symbol) {
            case "<" -> LESS;
            case "<=" -> LESS_EQUAL;
            case "=" -> EQUAL;
            case "!=" -> NOT_EQUAL;
            case ">=" -> GREATER_EQUAL;
            case ">" -> GREATER;
            default -> throw new QuestException("Unknown operation: " + symbol);
        };
    }

    /**
     * Checks if the Operation is true for the given numbers.
     *
     * @param first  the first number
     * @param second the second number
     * @return the result of the compare
     */
    public boolean check(final double first, final double second) {
        return compare.check(first, second);
    }

    /**
     * The compare interface.
     */
    private interface Compare {
        /**
         * Compares two numbers with an operand.
         *
         * @param first  number to compare
         * @param second number to compare
         * @return the result of the compare
         */
        boolean check(double first, double second);
    }
}
