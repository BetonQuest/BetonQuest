package org.betonquest.betonquest.quest.condition.permission;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;

/**
 * Factory for {@link PermissionCondition}s.
 */
public class PermissionConditionFactory implements PlayerConditionFactory {

    /**
     * Logger factory to create a logger for conditions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data used for condition check on the primary server thread.
     */
    private final PrimaryServerThreadData data;

    /**
     * Processor to create new variables.
     */
    private final VariableProcessor variableProcessor;

    /**
     * Creates a new factory for {@link PermissionCondition}s.
     *
     * @param loggerFactory     the logger factory
     * @param data              the data used for primary server access
     * @param variableProcessor the processor to create new variables
     */
    public PermissionConditionFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data, final VariableProcessor variableProcessor) {
        this.loggerFactory = loggerFactory;
        this.data = data;
        this.variableProcessor = variableProcessor;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final VariableString permission = new VariableString(variableProcessor, instruction.getPackage(), instruction.next());
        final BetonQuestLogger log = loggerFactory.create(PermissionCondition.class);
        return new PrimaryServerThreadPlayerCondition(
                new OnlineConditionAdapter(new PermissionCondition(permission), log, instruction.getPackage()), data
        );
    }
}
