package org.betonquest.betonquest.quest.condition.ride;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.bukkit.entity.EntityType;

import java.util.Optional;

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
     * Create the ride condition factory.
     *
     * @param loggerFactory the logger factory to create a logger for the conditions
     */
    public RideConditionFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Optional<EntityType>> vehicle = instruction.enumeration(EntityType.class)
                .prefilterOptional(ANY_ENTITY, null).get();
        final BetonQuestLogger logger = loggerFactory.create(RideCondition.class);
        return new OnlineConditionAdapter(new RideCondition(vehicle), logger, instruction.getPackage());
    }
}
