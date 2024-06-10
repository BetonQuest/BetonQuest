package org.betonquest.betonquest.api.quest.condition;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.jetbrains.annotations.Nullable;

/**
 * An {@link PlayerCondition} which can be executed with a profile or playerless.
 * <p>
 * Common usage is when containing {@link org.betonquest.betonquest.api.Variable Variable}s can require a
 * {@link org.betonquest.betonquest.api.profiles.Profile}.
 */
public interface ComposedCondition extends PlayerCondition, PlayerlessCondition {
    @Override
    boolean check(@Nullable Profile profile) throws QuestRuntimeException;

    @Override
    default boolean check() throws QuestRuntimeException {
        return check(null);
    }
}
