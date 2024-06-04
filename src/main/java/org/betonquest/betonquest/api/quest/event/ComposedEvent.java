package org.betonquest.betonquest.api.quest.event;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.jetbrains.annotations.Nullable;

/**
 * An {@link Event} which can be executed with a profile or static.
 * <p>
 * Common usage is when containing {@link org.betonquest.betonquest.api.Variable Variable}s can require a
 * {@link org.betonquest.betonquest.api.profiles.Profile Profile}.
 */
public interface ComposedEvent extends Event, StaticEvent {
    @Override
    void execute(@Nullable Profile profile) throws QuestRuntimeException;

    @Override
    default void execute() throws QuestRuntimeException {
        execute(null);
    }
}
