package org.betonquest.betonquest.quest.placeholder.constant;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.placeholder.nullable.NullablePlaceholder;
import org.jetbrains.annotations.Nullable;

/**
 * A placeholder that always evaluates to the same constant value.
 */
public class ConstantPlaceholder implements NullablePlaceholder {

    /**
     * The constant value.
     */
    private final Argument<String> constant;

    /**
     * Create a placeholder that always evaluates to the given constant.
     *
     * @param constant The constant value.
     */
    public ConstantPlaceholder(final Argument<String> constant) {
        this.constant = constant;
    }

    @Override
    public String getValue(@Nullable final Profile profile) throws QuestException {
        return constant.getValue(profile);
    }
}
