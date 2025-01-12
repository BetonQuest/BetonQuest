package org.betonquest.betonquest.api.quest.event.nullable;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.exceptions.QuestException;

/**
 * An adapter to handle both the {@link Event} and {@link StaticEvent} with one
 * common implementation of the {@link NullableEvent}.
 */
public final class NullableEventAdapter implements Event, StaticEvent {
    /**
     * Common null-safe event implementation.
     */
    private final NullableEvent event;

    /**
     * Create an adapter that handles events via the given common implementation.
     *
     * @param event common null-safe event implementation
     */
    public NullableEventAdapter(final NullableEvent event) {
        this.event = event;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        event.execute(profile);
    }

    @Override
    public void execute() throws QuestException {
        event.execute(null);
    }
}

