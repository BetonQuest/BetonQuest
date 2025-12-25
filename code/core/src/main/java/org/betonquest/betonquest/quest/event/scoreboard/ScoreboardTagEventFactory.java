package org.betonquest.betonquest.quest.event.scoreboard;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;

/**
 * Factory to create scoreboard tag events from {@link Instruction}s.
 */
public class ScoreboardTagEventFactory implements PlayerEventFactory {

    /**
     * Logger factory to create a logger for the events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the scoreboard tag event factory.
     *
     * @param loggerFactory the logger factory to create a logger for the events
     */
    public ScoreboardTagEventFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<ScoreboardTagAction> action = instruction.enumeration(ScoreboardTagAction.class).get();
        final Argument<String> tag = instruction.string().get();
        final BetonQuestLogger logger = loggerFactory.create(ScoreboardTagEvent.class);
        return new OnlineEventAdapter(new ScoreboardTagEvent(tag, action), logger, instruction.getPackage());
    }
}
