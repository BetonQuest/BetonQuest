package org.betonquest.betonquest.quest.variable.constant;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.variable.nullable.NullableVariable;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.jetbrains.annotations.Nullable;

/**
 * A variable that always evaluates to the same constant value.
 */
public class ConstantVariable implements NullableVariable {
    /**
     * The constant value.
     */
    private final Variable<String> constant;

    /**
     * Create a variable that always evaluates to the given constant.
     *
     * @param constant The constant value.
     */
    public ConstantVariable(final Variable<String> constant) {
        this.constant = constant;
    }

    @Override
    public String getValue(@Nullable final Profile profile) throws QuestException {
        return constant.getValue(profile);
    }
}
