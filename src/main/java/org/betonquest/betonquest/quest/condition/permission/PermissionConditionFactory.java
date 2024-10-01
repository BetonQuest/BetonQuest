package org.betonquest.betonquest.quest.condition.permission;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;

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
     * Create the permission factory.
     *
     * @param loggerFactory the logger factory
     * @param data          the data used for checking the condition on the main thread
     */
    public PermissionConditionFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws InstructionParseException {
        final String permission = instruction.next();
        final BetonQuestLogger log = loggerFactory.create(PermissionCondition.class);
        return new PrimaryServerThreadPlayerCondition(
                new OnlineConditionAdapter(new PermissionCondition(permission), log, instruction.getPackage()), data
        );
    }
}
