package org.betonquest.betonquest.quest.action.scoreboard;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.online.OnlineActionAdapter;

/**
 * Factory to create scoreboard tag actions from {@link Instruction}s.
 */
public class ScoreboardTagActionFactory implements PlayerActionFactory {

    /**
     * Logger factory to create a logger for the actions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the scoreboard tag action factory.
     *
     * @param loggerFactory the logger factory to create a logger for the actions
     */
    public ScoreboardTagActionFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<ScoreboardTagOperation> action = instruction.enumeration(ScoreboardTagOperation.class).get();
        final Argument<String> tag = instruction.string().get();
        final BetonQuestLogger logger = loggerFactory.create(ScoreboardTagAction.class);
        return new OnlineActionAdapter(new ScoreboardTagAction(tag, action), logger, instruction.getPackage());
    }
}
