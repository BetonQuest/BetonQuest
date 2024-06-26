package org.betonquest.betonquest.quest.variable.constant;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.variable.Variable;
import org.jetbrains.annotations.Nullable;

/**
 * A variable that always evaluates to the same constant value.
 */
public class ConstantVariable implements Variable {
    /**
     * The constant value.
     */
    private final String constant;

    /**
     * Create a variable that always evaluates to the given constant.
     *
     * @param constant The constant value.
     */
    public ConstantVariable(final String constant) {
        this.constant = constant;
    }

    @Override
    public String getValue(@Nullable final Profile profile) {
        return constant;
    }
}
