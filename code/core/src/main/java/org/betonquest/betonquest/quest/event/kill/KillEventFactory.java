package org.betonquest.betonquest.quest.event.kill;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;

/**
 * Factory for the kill event.
 */
public class KillEventFactory implements PlayerEventFactory {

    /**
     * Logger factory to create a logger for the events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Creates the kill event factory.
     *
     * @param loggerFactory the logger factory to create a logger for the events
     */
    public KillEventFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) {
        return new OnlineEventAdapter(new KillEvent(),
                loggerFactory.create(KillEvent.class), instruction.getPackage());
    }
}
