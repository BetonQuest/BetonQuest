package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * The condition class to compare two numbers.
 */
public abstract class BaseNumberCompareCondition extends Condition {
    /**
     * Creates the number compare condition.
     *
     * @param instruction instruction to parse
     */
    protected BaseNumberCompareCondition(final Instruction instruction) {
        super(instruction, false);
        this.persistent = true;
        this.staticness = true;
    }

    /**
     * Get the first number.
     *
     * @param profile the profile to get the number from
     * @return the number
     * @throws QuestRuntimeException when the number cannot be parsed
     * @throws IllegalStateException when getting the number fails caused by an invalid state
     *                               that should cause the condition to be false
     */
    @SuppressWarnings("PMD.AvoidUncheckedExceptionsInSignatures")
    protected abstract Double getFirst(Profile profile) throws QuestRuntimeException, IllegalStateException;

    /**
     * Gets the second number.
     *
     * @param profile the profile to get the number from
     * @return the number
     * @throws QuestRuntimeException when the number cannot be parsed
     * @throws IllegalStateException when getting the number fails caused by an invalid state
     *                               that should cause the condition to be false
     */
    @SuppressWarnings("PMD.AvoidUncheckedExceptionsInSignatures")
    protected abstract Double getSecond(Profile profile) throws QuestRuntimeException, IllegalStateException;

    /**
     * Get the operation.
     *
     * @return the operation
     */
    protected abstract Operation getOperation();

    /**
     * Converts a symbol to an operation.
     *
     * @param symbol the symbol to convert
     * @return the operation
     * @throws InstructionParseException when the symbol is unknown
     */
    protected Operation fromSymbol(final String symbol) throws InstructionParseException {
        return switch (symbol) {
            case "<" -> Operation.LESS;
            case "<=" -> Operation.LESS_EQUAL;
            case "=" -> Operation.EQUAL;
            case "!=" -> Operation.NOT_EQUAL;
            case ">=" -> Operation.GREATER_EQUAL;
            case ">" -> Operation.GREATER;
            default -> throw new InstructionParseException("Unknown operation: " + symbol);
        };
    }

    @Override
    protected Boolean execute(final Profile profile) throws QuestRuntimeException {
        try {
            return getOperation().compare.check(getFirst(profile), getSecond(profile));
        } catch (final IllegalStateException e) {
            return false;
        }
    }

    /**
     * The type of compare
     */
    protected enum Operation {
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
