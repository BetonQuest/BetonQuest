package org.betonquest.betonquest.quest.condition.ride;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.thread.PrimaryServerThreadPlayerCondition;
import org.bukkit.entity.EntityType;

/**
 * Factory to create ride conditions from {@link Instruction}s.
 */
public class RideConditionFactory implements PlayerConditionFactory {

    /**
     * The string to match any entity.
     */
    private static final String ANY_ENTITY = "any";

    /**
     * Logger factory to create a logger for the conditions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data used for condition check on the primary server thread.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the ride condition factory.
     *
     * @param loggerFactory the logger factory to create a logger for the conditions
     * @param data          the data used for checking the condition on the main thread
     */
    public RideConditionFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<EntityType> vehicle = instruction.get(Argument.ENUM(EntityType.class).prefilter(ANY_ENTITY, null));
        final BetonQuestLogger logger = loggerFactory.create(RideCondition.class);
        return new PrimaryServerThreadPlayerCondition(
                new OnlineConditionAdapter(new RideCondition(vehicle), logger, instruction.getPackage()), data);
    }
}
