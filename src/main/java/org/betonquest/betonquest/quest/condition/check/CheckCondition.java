package org.betonquest.betonquest.quest.condition.check;

import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
import org.betonquest.betonquest.exceptions.QuestException;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Allows for checking multiple conditions with one instruction string.
 */
public class CheckCondition implements NullableCondition {

    /**
     * Conditions that will be checked by this condition. All must be true for this condition to be true as well.
     */
    private final List<Condition> internalConditions;

    /**
     * Create a check condition for the given instruction.
     *
     * @param internalConditions conditions that will be checked by this condition
     */
    public CheckCondition(final List<Condition> internalConditions) {
        this.internalConditions = internalConditions;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        for (final Condition condition : internalConditions) {
            if (!condition.handle(profile)) {
                return false;
            }
        }
        return true;
    }
}
