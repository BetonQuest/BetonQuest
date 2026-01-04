package org.betonquest.betonquest.quest.event.run;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.betonquest.betonquest.kernel.processor.adapter.ActionAdapter;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Allows for running multiple actions.
 */
public class RunEvent implements NullableEvent {

    /**
     * Actions that the run event will execute.
     */
    private final List<ActionAdapter> actions;

    /**
     * Create a run event from the given instruction.
     *
     * @param actions events to run
     */
    public RunEvent(final List<ActionAdapter> actions) {
        this.actions = actions;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        for (final ActionAdapter event : actions) {
            event.fire(profile);
        }
    }
}
