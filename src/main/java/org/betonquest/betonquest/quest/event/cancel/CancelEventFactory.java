package org.betonquest.betonquest.quest.event.cancel;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
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
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Creates a new cancel event factory.
     */
    public CancelEventFactory(final BetonQuestLogger log) {
        this.log = log;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        final String cancelerName = instruction.getPackage().getQuestPath() + "." + instruction.next();
        final QuestCanceler canceler = BetonQuest.getCanceler().get(cancelerName);
        if (canceler == null) {
            throw new InstructionParseException("Could not find canceler '" + cancelerName + "'");
        }
        return new OnlineProfileRequiredEvent(log, new CancelEvent(canceler), instruction.getPackage());
    }
}
