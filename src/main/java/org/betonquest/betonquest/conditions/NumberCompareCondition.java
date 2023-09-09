package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * The condition class to compare two numbers.
 */
public class NumberCompareCondition extends Condition {
    /**
     * The number on the left side.
     */
    private final VariableNumber first;

    /**
     * The number of the right side.
     */
    private final VariableNumber second;

    /**
     * The compare operand between the numbers used for comparing.
     */
    private final Operation operation;

    /**
     * Creates the number compare condition.
     *
     * @param instruction instruction to parse
     * @throws InstructionParseException when the instruction cannot be parsed
     */
    public NumberCompareCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        this.persistent = true;
        this.staticness = true;
        this.first = instruction.getVarNum();
        this.operation = fromSymbol(instruction.next());
        this.second = instruction.getVarNum();
    }

    private Operation fromSymbol(final String symbol) throws InstructionParseException {
        return switch (symbol) {
            case "<" -> Operation.LESS;
            case "<=" -> Operation.LESS_EQUAL;
            case "=" -> Operation.EQUAL;
            case ">=" -> Operation.GREATER_EQUAL;
            case ">" -> Operation.GREATER;
            default -> throw new InstructionParseException("Unknown operation: " + symbol);
        };
    }

    @Override
    protected Boolean execute(final Profile profile) {
        return operation.compare.check(first.getDouble(profile), second.getDouble(profile));
    }

    /**
     * The type of compare
     */
    private enum Operation {
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
