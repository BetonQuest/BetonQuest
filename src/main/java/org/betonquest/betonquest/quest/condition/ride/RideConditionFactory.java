package org.betonquest.betonquest.quest.condition.ride;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;
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
     * Logger factory to create a logger for condition.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data used for condition check on the primary server thread.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the ride condition factory.
     *
     * @param loggerFactory the logger factory to create a logger for the condition
     * @param data          the data used for checking the condition on the main thread
     */
    public RideConditionFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final String name = instruction.next();
        final EntityType vehicle;
        if (ANY_ENTITY.equalsIgnoreCase(name)) {
            vehicle = null;
        } else {
            vehicle = instruction.getEnum(name, EntityType.class);
        }
        final BetonQuestLogger logger = loggerFactory.create(RideCondition.class);
        return new PrimaryServerThreadPlayerCondition(
                new OnlineConditionAdapter(new RideCondition(vehicle), logger, instruction.getPackage()), data);
    }
}
