package org.betonquest.betonquest.quest.condition.height;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;

/**
 * Factory for {@link HeightCondition}s.
 */
public class HeightConditionFactory implements PlayerConditionFactory {

    /**
     * Logger factory to create a logger for conditions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data used for condition check on the primary server thread.
     */
    private final PrimaryServerThreadData data;

    /**
     * The variable processor.
     */
    private final VariableProcessor variableProcessor;

    /**
     * Create the height factory.
     *
     * @param loggerFactory     the logger factory
     * @param data              the data used for checking the condition on the main thread
     * @param variableProcessor the variable processor
     */
    public HeightConditionFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data, final VariableProcessor variableProcessor) {
        this.loggerFactory = loggerFactory;
        this.data = data;
        this.variableProcessor = variableProcessor;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final BetonQuestLogger log = loggerFactory.create(HeightCondition.class);
        final String string = instruction.next();
        final Variable<Number> height = new Variable<>(variableProcessor, instruction.getPackage(), string, (value) -> {
            try {
                final double parsedValue;
                if (value.matches("-?\\d+\\.?\\d*")) {
                    parsedValue = Double.parseDouble(value);
                } else {
                    parsedValue = VariableLocation.parse(value).getY();
                }

                return parsedValue;
            } catch (final NumberFormatException e) {
                throw new QuestException("Could not parse number: " + value, e);
            }
        });
        return new PrimaryServerThreadPlayerCondition(
                new OnlineConditionAdapter(new HeightCondition(height), log, instruction.getPackage()), data
        );
    }
}
