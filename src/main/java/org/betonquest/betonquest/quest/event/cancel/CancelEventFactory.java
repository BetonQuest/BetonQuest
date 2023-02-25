package org.betonquest.betonquest.quest.event.cancel;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.config.QuestCanceler;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.event.OnlineProfileRequiredEvent;

/**
 * Factory for the cancel event.
 */
public class CancelEventFactory implements EventFactory {

    /**
     * Creates a new cancel event factory.
     */
    public CancelEventFactory() {
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        final QuestCanceler canceler = BetonQuest.getCanceler().get(instruction.getPackage().getQuestPath() + "." + instruction.next());
        if (canceler == null) {
            throw new InstructionParseException("Canceler is null");
        }
        return new OnlineProfileRequiredEvent(new CancelEvent(canceler), instruction.getPackage());
    }
}
