package org.betonquest.betonquest.quest.condition.facing;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;

/**
 * Factory for {@link FacingCondition}s.
 */
public class FacingConditionFactory implements PlayerConditionFactory {

    /**
     * Logger factory to create a logger for the conditions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the facing factory.
     *
     * @param loggerFactory the logger factory to create a logger for the conditions
     */
    public FacingConditionFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<Direction> direction = instruction.get(instruction.getParsers().forEnum(Direction.class));
        final BetonQuestLogger log = loggerFactory.create(FacingCondition.class);
        return new OnlineConditionAdapter(new FacingCondition(direction), log, instruction.getPackage());
    }
}
