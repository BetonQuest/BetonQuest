package org.betonquest.betonquest.quest.event.scoreboard;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.online.OnlineActionAdapter;

/**
 * Factory to create scoreboard tag events from {@link Instruction}s.
 */
public class ScoreboardTagEventFactory implements PlayerActionFactory {

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
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<ScoreboardTagOperation> action = instruction.enumeration(ScoreboardTagOperation.class).get();
        final Argument<String> tag = instruction.string().get();
        final BetonQuestLogger logger = loggerFactory.create(ScoreboardTagEvent.class);
        return new OnlineActionAdapter(new ScoreboardTagEvent(tag, action), logger, instruction.getPackage());
    }
}
