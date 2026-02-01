package org.betonquest.betonquest.quest.condition.permission;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;

/**
 * Factory for {@link PermissionCondition}s.
 */
public class PermissionConditionFactory implements PlayerConditionFactory {

    /**
     * Logger factory to create a logger for the conditions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Creates a new factory for {@link PermissionCondition}s.
     *
     * @param loggerFactory the logger factory to create a logger for the conditions
     */
    public PermissionConditionFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<String> permission = instruction.string().get();
        final BetonQuestLogger log = loggerFactory.create(PermissionCondition.class);
        return new OnlineConditionAdapter(new PermissionCondition(permission), log, instruction.getPackage());
    }
}
