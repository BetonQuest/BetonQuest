package org.betonquest.betonquest.quest.event.run;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.instruction.Instruction;

import java.util.List;

/**
 * Create new {@link RunIndependentEvent} from instruction.
 */
public class RunIndependentEventFactory implements StaticEventFactory {

    /**
     * Quest Type API.
     */
    private final QuestTypeAPI questTypeAPI;

    /**
     * Create new {@link RunIndependentEventFactory}.
     *
     * @param questTypeAPI the Quest Type API
     */
    public RunIndependentEventFactory(final QuestTypeAPI questTypeAPI) {
        this.questTypeAPI = questTypeAPI;
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws QuestException {
        final List<EventID> events = instruction.getIDList(instruction.getOptional("events"), EventID::new);
        return new RunIndependentEvent(questTypeAPI, events);
    }
}
