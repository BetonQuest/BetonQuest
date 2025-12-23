package org.betonquest.betonquest.quest.condition.height;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;

/**
 * Factory for {@link HeightCondition}s.
 */
public class HeightConditionFactory implements PlayerConditionFactory {

    /**
     * Logger factory to create a logger for the conditions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the height factory.
     *
     * @param loggerFactory the logger factory to create a logger for the conditions
     */
    public HeightConditionFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<Number> height = instruction.get(value -> {
            try {
                if (value.matches("-?\\d+\\.?\\d*")) {
                    return Double.parseDouble(value);
                }
                return instruction.getParsers().location().apply(value).getY();
            } catch (final NumberFormatException e) {
                throw new QuestException("Could not parse number: " + value, e);
            }
        });
        final BetonQuestLogger log = loggerFactory.create(HeightCondition.class);
        return new OnlineConditionAdapter(new HeightCondition(height), log, instruction.getPackage());
    }
}
