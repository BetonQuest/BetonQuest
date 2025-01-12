package org.betonquest.betonquest.quest.condition.number;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.jetbrains.annotations.Nullable;

/**
 * The condition class to compare two numbers.
 */
public class NumberCompareCondition implements NullableCondition {

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
     * Creates a new NumberCompareCondition.
     *
     * @param first     the first number
     * @param second    the second number
     * @param operation the operation to use
     */
    public NumberCompareCondition(final VariableNumber first, final VariableNumber second, final Operation operation) {
        this.first = first;
        this.second = second;
        this.operation = operation;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        return operation.check(first.getValue(profile).doubleValue(), second.getValue(profile).doubleValue());
    }
}
