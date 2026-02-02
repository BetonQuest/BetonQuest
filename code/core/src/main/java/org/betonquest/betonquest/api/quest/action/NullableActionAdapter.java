package org.betonquest.betonquest.api.quest.action;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;

/**
 * An adapter to handle both the {@link PlayerAction} and {@link PlayerlessAction} with one
 * common implementation of the {@link NullableAction}.
 */
public final class NullableActionAdapter implements PlayerAction, PlayerlessAction {

    /**
     * Common null-safe action implementation.
     */
    private final NullableAction action;

    /**
     * Create an adapter that handles actions via the given common implementation.
     *
     * @param action common null-safe action implementation
     */
    public NullableActionAdapter(final NullableAction action) {
        this.action = action;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        action.execute(profile);
    }

    @Override
    public void execute() throws QuestException {
        action.execute(null);
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return action.isPrimaryThreadEnforced();
    }
}
