package org.betonquest.betonquest.quest.condition.armor;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;

/**
 * Factory for {@link ArmorRatingCondition}s from {@link Instruction}s.
 */
public class ArmorRatingConditionFactory implements PlayerConditionFactory {

    /**
     * Logger factory to create a logger for the conditions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data used for condition check on the primary server thread.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the armor rating factory.
     *
     * @param loggerFactory the logger factory to create a logger for the conditions
     * @param data          the data used for checking the condition on the main thread
     */
    public ArmorRatingConditionFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<Number> required = instruction.get(Argument.NUMBER);
        final BetonQuestLogger log = loggerFactory.create(ArmorRatingCondition.class);
        return new PrimaryServerThreadPlayerCondition(
                new OnlineConditionAdapter(new ArmorRatingCondition(required), log, instruction.getPackage()), data);
    }
}
