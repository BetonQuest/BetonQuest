package org.betonquest.betonquest.quest.event.run;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.event.EventID;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;

import java.util.Collections;
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
        final Argument<List<EventID>> events = instruction.parse(EventID::new).getList("events", Collections.emptyList());
        return new RunIndependentEvent(questTypeApi, events);
    }
}
