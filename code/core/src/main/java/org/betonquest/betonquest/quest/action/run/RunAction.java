package org.betonquest.betonquest.quest.action.run;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.nullable.NullableAction;
import org.betonquest.betonquest.kernel.processor.adapter.ActionAdapter;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Allows for running multiple actions.
 */
public class RunAction implements NullableAction {

    /**
     * Actions that the run event will execute.
     */
    private final List<ActionAdapter> actions;

    /**
     * Create a run event from the given instruction.
     *
     * @param actions events to run
     */
    public RunAction(final List<ActionAdapter> actions) {
        this.actions = actions;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        for (final ActionAdapter event : actions) {
            event.fire(profile);
        }
    }
}
