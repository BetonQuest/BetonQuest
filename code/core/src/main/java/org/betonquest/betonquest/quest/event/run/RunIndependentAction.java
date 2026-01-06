package org.betonquest.betonquest.quest.event.run;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.action.ActionID;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.quest.event.CallPlayerlessActionAdapter;

import java.util.List;

/**
 * Runs specified actions player independently.
 * <p>
 * Although the implementation is a {@link PlayerlessAction}, using it in a static context does not make much sense.
 * Recommended usage is to wrap it in a {@link CallPlayerlessActionAdapter} and using it to call static actions
 * from non-static context.
 */
public class RunIndependentAction implements PlayerlessAction {

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * List of Actions to run.
     */
    private final Argument<List<ActionID>> actions;

    /**
     * Create a new RunIndependentAction instance.
     *
     * @param questTypeApi the Quest Type API
     * @param actions      the actions to run
     */
    public RunIndependentAction(final QuestTypeApi questTypeApi, final Argument<List<ActionID>> actions) {
        this.questTypeApi = questTypeApi;
        this.actions = actions;
    }

    @Override
    public void execute() throws QuestException {
        questTypeApi.actions(null, actions.getValue(null));
    }
}
