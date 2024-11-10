package org.betonquest.betonquest.quest.condition.scoreboard;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;

/**
 * Factory to create scoreboard tag conditions from {@link Instruction}s.
 */
public class ScoreboardTagConditionFactory implements PlayerConditionFactory {

    /**
     * Data used for condition check on the primary server thread.
     */
    private final PrimaryServerThreadData data;

    /**
     * Logger factory to create a logger for condition.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the scoreboard tag condition factory.
     *
     * @param data          the data used for checking the condition on the main thread
     * @param loggerFactory the logger factory to create a logger for the condition
     */
    public ScoreboardTagConditionFactory(final PrimaryServerThreadData data, final BetonQuestLoggerFactory loggerFactory) {
        this.data = data;
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws InstructionParseException {
        final String tag = instruction.next();
        final BetonQuestLogger logger = loggerFactory.create(ScoreboardTagCondition.class);
        return new PrimaryServerThreadPlayerCondition(
                new OnlineConditionAdapter(new ScoreboardTagCondition(tag), logger, instruction.getPackage()),
                data
        );
    }
}
