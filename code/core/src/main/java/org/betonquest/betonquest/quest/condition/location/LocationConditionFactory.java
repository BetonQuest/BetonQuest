package org.betonquest.betonquest.quest.condition.location;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.bukkit.Location;

/**
 * Factory for {@link LocationCondition}s from {@link Instruction}s.
 */
public class LocationConditionFactory implements PlayerConditionFactory {

    /**
     * Logger factory to create a logger for the conditions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the test for location condition factory.
     *
     * @param loggerFactory the logger factory to create a logger for the conditions
     */
    public LocationConditionFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Location> loc = instruction.location().get();
        final Argument<Number> range = instruction.number().get();
        final BetonQuestLogger log = loggerFactory.create(LocationCondition.class);
        return new OnlineConditionAdapter(new LocationCondition(loc, range), log, instruction.getPackage());
    }
}
