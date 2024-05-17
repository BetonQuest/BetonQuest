package org.betonquest.betonquest.quest.event.point;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.event.CallStaticEventAdapter;
import org.betonquest.betonquest.utils.Utils;

/**
 * Factory for the delete global point event.
 */
public class DeleteGlobalPointEventFactory implements EventFactory, StaticEventFactory {

    /**
     * Creates a new DeleteGlobalPointEventFactory.
     */
    public DeleteGlobalPointEventFactory() {
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws InstructionParseException {
        final String category = Utils.addPackage(instruction.getPackage(), instruction.next());
        return new DeleteGlobalPointEvent(category);
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        return new CallStaticEventAdapter(parseStaticEvent(instruction));
    }
}
