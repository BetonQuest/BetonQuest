package org.betonquest.betonquest.api.quest.action.nullable;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;

/**
 * An adapter to handle both the {@link PlayerAction} and {@link PlayerlessAction} with one
 * common implementation of the {@link NullableAction}.
 */
public final class NullableActionAdapter implements PlayerAction, PlayerlessAction {

    /**
     * Common null-safe event implementation.
     */
    private final NullableAction event;

    /**
     * Create an adapter that handles events via the given common implementation.
     *
     * @param event common null-safe event implementation
     */
    public NullableActionAdapter(final NullableAction event) {
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

    @Override
    public boolean isPrimaryThreadEnforced() {
        return event.isPrimaryThreadEnforced();
    }
}
