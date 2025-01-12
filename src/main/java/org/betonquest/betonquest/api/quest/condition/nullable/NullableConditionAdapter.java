package org.betonquest.betonquest.api.quest.condition.nullable;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.exceptions.QuestException;

/**
 * An adapter to handle both the {@link PlayerCondition} and {@link PlayerlessCondition}
 * with one common implementation of the {@link NullableCondition}.
 */
public final class NullableConditionAdapter implements PlayerCondition, PlayerlessCondition {
    /**
     * Common null-safe condition implementation.
     */
    private final NullableCondition condition;

    /**
     * Create an adapter that handles conditions via the given common implementation.
     *
     * @param condition common null-safe condition implementation
     */
    public NullableConditionAdapter(final NullableCondition condition) {
        this.condition = condition;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        return condition.check(profile);
    }

    @Override
    public boolean check() throws QuestException {
        return condition.check(null);
    }
}
