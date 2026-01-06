package org.betonquest.betonquest.quest.action.run;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.action.ActionID;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;

import java.util.Collections;
import java.util.List;

/**
 * Create new {@link RunIndependentAction} from instruction.
 */
public class RunIndependentActionFactory implements PlayerlessActionFactory {

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * Create new {@link RunIndependentActionFactory}.
     *
     * @param questTypeApi the Quest Type API
     */
    public RunIndependentActionFactory(final QuestTypeApi questTypeApi) {
        this.questTypeApi = questTypeApi;
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        final Argument<List<ActionID>> events = instruction.parse(ActionID::new).list().get("actions", Collections.emptyList());
        return new RunIndependentAction(questTypeApi, events);
    }
}
