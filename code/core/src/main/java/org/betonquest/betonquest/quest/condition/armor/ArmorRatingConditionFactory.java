package org.betonquest.betonquest.quest.condition.armor;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;

/**
 * Factory for {@link ArmorRatingCondition}s from {@link Instruction}s.
 */
public class ArmorRatingConditionFactory implements PlayerConditionFactory {

    /**
     * Logger factory to create a logger for the conditions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the armor rating factory.
     *
     * @param loggerFactory the logger factory to create a logger for the conditions
     */
    public ArmorRatingConditionFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<Number> required = instruction.get(instruction.getParsers().number());
        final BetonQuestLogger log = loggerFactory.create(ArmorRatingCondition.class);
        return new OnlineConditionAdapter(new ArmorRatingCondition(required), log, instruction.getPackage());
    }
}
