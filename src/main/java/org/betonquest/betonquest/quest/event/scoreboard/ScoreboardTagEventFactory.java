package org.betonquest.betonquest.quest.event.scoreboard;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;

/**
 * Factory to create scoreboard tag events from {@link Instruction}s.
 */
public class ScoreboardTagEventFactory implements EventFactory {

    /**
     * Logger factory to create a logger for condition.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data used for condition check on the primary server thread.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the scoreboard tag event factory.
     *
     * @param loggerFactory the logger factory to create a logger for the condition
     * @param data          the data used for checking the condition on the main thread
     */
    public ScoreboardTagEventFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.data = data;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        final ScoreboardTagAction action = instruction.getEnum(ScoreboardTagAction.class);
        final String tag = instruction.next();
        final BetonQuestLogger logger = loggerFactory.create(ScoreboardTagEvent.class);
        return new PrimaryServerThreadEvent(new OnlineEventAdapter(
                new ScoreboardTagEvent(tag, action), logger, instruction.getPackage()
        ), data);
    }
}
