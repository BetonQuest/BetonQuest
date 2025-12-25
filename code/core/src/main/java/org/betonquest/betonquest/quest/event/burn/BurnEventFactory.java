package org.betonquest.betonquest.quest.event.burn;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;

/**
 * Factory to create burn events from {@link Instruction}s.
 */
public class BurnEventFactory implements PlayerEventFactory {

    /**
     * Logger factory to create a logger for the events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the burn event factory.
     *
     * @param loggerFactory the logger factory to create a logger for the events
     */
    public BurnEventFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Number> duration = instruction.number().get("duration").orElse(null);
        if (duration == null) {
            throw new QuestException("Missing duration!");
        }
        return new OnlineEventAdapter(new BurnEvent(duration), loggerFactory.create(BurnEvent.class), instruction.getPackage());
    }
}
