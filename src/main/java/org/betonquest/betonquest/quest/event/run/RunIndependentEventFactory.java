package org.betonquest.betonquest.quest.event.run;

import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.instruction.Instruction;

import java.util.List;

/**
 * Create new {@link RunIndependentEvent} from instruction.
 */
public class RunIndependentEventFactory implements StaticEventFactory {

    /**
     * Create new {@link RunIndependentEventFactory}.
     */
    public RunIndependentEventFactory() {
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws QuestException {
        final List<EventID> events = List.of(instruction.getIDArray(instruction.getOptional("events"), EventID::new));
        return new RunIndependentEvent(events);
    }
}
