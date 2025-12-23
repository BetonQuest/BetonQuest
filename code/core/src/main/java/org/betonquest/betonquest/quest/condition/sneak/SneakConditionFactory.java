package org.betonquest.betonquest.quest.condition.sneak;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;

/**
 * Factory for {@link SneakCondition}s.
 */
public class SneakConditionFactory implements PlayerConditionFactory {

    /**
     * Logger factory to create a logger for the conditions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the sneak factory.
     *
     * @param loggerFactory the logger factory to create a logger for the conditions
     */
    public SneakConditionFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) {
        final BetonQuestLogger log = loggerFactory.create(SneakCondition.class);
        return new OnlineConditionAdapter(new SneakCondition(), log, instruction.getPackage());
    }
}
