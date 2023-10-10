package org.betonquest.betonquest.quest.event.cancel;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.config.QuestCanceler;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.id.QuestCancelerID;
import org.betonquest.betonquest.id.builder.QuestCancelerIDBuilder;
import org.betonquest.betonquest.quest.event.OnlineProfileRequiredEvent;

/**
 * Factory for the cancel event.
 */
public class CancelEventFactory implements EventFactory {
    /**
     * Logger factory to create a logger for events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Creates a new cancel event factory.
     *
     * @param loggerFactory logger factory to use
     */
    public CancelEventFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        final QuestCancelerID cancelerID = new QuestCancelerIDBuilder(instruction.getPackage(), instruction.next()).build();
        final QuestCanceler canceler = BetonQuest.getCanceler().get(cancelerID);
        return new OnlineProfileRequiredEvent(loggerFactory.create(CancelEvent.class), new CancelEvent(canceler), instruction.getPackage());
    }
}
