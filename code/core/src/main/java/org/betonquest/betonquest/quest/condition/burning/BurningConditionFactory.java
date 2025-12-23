package org.betonquest.betonquest.quest.condition.burning;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;

/**
 * Factory for {@link BurningCondition}s from {@link Instruction}s.
 */
public class BurningConditionFactory implements PlayerConditionFactory {

    /**
     * Logger factory to create a logger for the conditions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the burning factory.
     *
     * @param loggerFactory the logger factory to create a logger for the conditions
     */
    public BurningConditionFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) {
        final BetonQuestLogger log = loggerFactory.create(BurningCondition.class);
        return new OnlineConditionAdapter(new BurningCondition(), log, instruction.getPackage());
    }
}
