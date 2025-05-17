package org.betonquest.betonquest.quest.condition.selector;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.quest.condition.number.Operation;

import javax.annotation.Nullable;

/**
 * The {@link Selector} to compare two numbers.
 */
public class ComparisonSelector implements Selector {

    /**
     * The {@link Operation} to use for the comparison.
     */
    private final Operation operation;

    /**
     * The number to compare with.
     * Is the number on the right side of the comparison.
     */
    private final Variable<Number> right;

    /**
     * Creates a new ComparisonSelector.
     *
     * @param operation the operation to use
     * @param right     the number to compare with
     */
    public ComparisonSelector(final Operation operation, final Variable<Number> right) {
        this.operation = operation;
        this.right = right;
    }

    @Override
    public boolean matches(@Nullable final Profile profile, final Number left) throws QuestException {
        return operation.check(left.doubleValue(), right.getValue(profile).doubleValue());
    }
}
