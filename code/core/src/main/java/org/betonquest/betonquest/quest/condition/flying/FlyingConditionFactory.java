package org.betonquest.betonquest.quest.condition.flying;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;

/**
 * Factory for {@link FlyingCondition}s.
 */
public class FlyingConditionFactory implements PlayerConditionFactory {

    /**
     * Logger factory to create a logger for the conditions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the flying factory.
     *
     * @param loggerFactory the logger factory to create a logger for the conditions
     */
    public FlyingConditionFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) {
        final BetonQuestLogger log = loggerFactory.create(FlyingCondition.class);
        return new OnlineConditionAdapter(new FlyingCondition(), log, instruction.getPackage());
    }
}
