package org.betonquest.betonquest.quest.event.run;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.BetonQuestAPI;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.id.EventID;

import java.util.List;

/**
 * Create new {@link RunIndependentEvent} from instruction.
 */
public class RunIndependentEventFactory implements StaticEventFactory {

    /**
     * BetonQuest API.
     */
    private final BetonQuestAPI questAPI;

    /**
     * Create new {@link RunIndependentEventFactory}.
     *
     * @param questAPI the BetonQuest API
     */
    public RunIndependentEventFactory(final BetonQuestAPI questAPI) {
        this.questAPI = questAPI;
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws QuestException {
        final List<EventID> events = instruction.getList(instruction.getOptional("events"), instruction::getEvent);
        return new RunIndependentEvent(questAPI, events);
    }
}
