package org.betonquest.betonquest.quest.condition.height;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
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
     * Variable processor to process variables.
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
    public PlayerCondition parsePlayer(final Instruction instruction) throws InstructionParseException {
        final BetonQuestLogger log = loggerFactory.create(HeightCondition.class);
        final String string = instruction.next();
        final QuestPackage pack = instruction.getPackage();
        final VariableNumber height;
        if (string.matches("-?\\d+\\.?\\d*")) {
            try {
                height = new VariableNumber(variableProcessor, pack, string);
            } catch (final InstructionParseException e) {
                throw new InstructionParseException("Could not parse height", e);
            }
        } else {
            try {
                height = new VariableNumber(variableProcessor, pack, String.valueOf(new VariableLocation(variableProcessor, pack, string).getValue(null).getY()));
            } catch (final QuestRuntimeException e) {
                throw new InstructionParseException("Could not parse height", e);
            }
        }
        return new PrimaryServerThreadPlayerCondition(
                new OnlineConditionAdapter(new HeightCondition(height), log, instruction.getPackage()), data
        );
    }
}
