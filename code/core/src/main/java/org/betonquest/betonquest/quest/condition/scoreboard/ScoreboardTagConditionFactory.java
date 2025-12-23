package org.betonquest.betonquest.quest.condition.scoreboard;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;

/**
 * Factory to create scoreboard tag conditions from {@link Instruction}s.
 */
public class ScoreboardTagConditionFactory implements PlayerConditionFactory {

    /**
     * Logger factory to create a logger for the conditions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the scoreboard tag condition factory.
     *
     * @param loggerFactory the logger factory to create a logger for the conditions
     */
    public ScoreboardTagConditionFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<String> tag = instruction.get(instruction.getParsers().string());
        final BetonQuestLogger logger = loggerFactory.create(ScoreboardTagCondition.class);
        return new OnlineConditionAdapter(new ScoreboardTagCondition(tag), logger, instruction.getPackage());
    }
}
