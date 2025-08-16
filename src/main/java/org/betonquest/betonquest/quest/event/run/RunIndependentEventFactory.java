package org.betonquest.betonquest.quest.event.run;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.event.EventID;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.Variable;

import java.util.List;

/**
 * Create new {@link RunIndependentEvent} from instruction.
 */
public class RunIndependentEventFactory implements PlayerlessEventFactory {

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * Create new {@link RunIndependentEventFactory}.
     *
     * @param questTypeApi the Quest Type API
     */
    public RunIndependentEventFactory(final QuestTypeApi questTypeApi) {
        this.questTypeApi = questTypeApi;
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        final Variable<List<EventID>> events = instruction.getValueList("events", EventID::new);
        return new RunIndependentEvent(questTypeApi, events);
    }
}
