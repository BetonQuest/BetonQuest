package org.betonquest.betonquest.quest.event.cancel;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.config.QuestCanceler;
import org.betonquest.betonquest.exception.ObjectNotFoundException;
import org.betonquest.betonquest.id.QuestCancelerID;
import org.betonquest.betonquest.instruction.Instruction;

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
    public Event parseEvent(final Instruction instruction) throws QuestException {
        final QuestPackage pack = instruction.getPackage();
        final String identifier = instruction.next();
        try {
            final QuestCancelerID cancelerID = new QuestCancelerID(pack, identifier);
            final QuestCanceler canceler = BetonQuest.getCanceler().get(cancelerID);
            if (canceler == null) {
                throw new QuestException("Quest canceler '" + cancelerID.getFullID() + "' does not exist."
                        + " Ensure it was loaded without errors.");
            }
            return new OnlineEventAdapter(new CancelEvent(canceler), loggerFactory.create(CancelEvent.class), pack);
        } catch (final ObjectNotFoundException e) {
            throw new QuestException("Quest canceler '" + pack.getQuestPath() + "." + identifier + "' does not exist."
                    + " Ensure it was loaded without errors.", e);
        }
    }
}
