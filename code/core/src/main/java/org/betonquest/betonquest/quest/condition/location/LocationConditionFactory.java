package org.betonquest.betonquest.quest.condition.location;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;
import org.bukkit.Location;

/**
 * Factory for {@link LocationCondition}s from {@link Instruction}s.
 */
public class LocationConditionFactory implements PlayerConditionFactory {

    /**
     * Data used for condition check on the primary server thread.
     */
    private final PrimaryServerThreadData data;

    /**
     * Logger factory to create a logger for the conditions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the test for location condition factory.
     *
     * @param data          the data used for checking the condition on the main thread
     * @param loggerFactory the logger factory to create a logger for the conditions
     */
    public LocationConditionFactory(final PrimaryServerThreadData data, final BetonQuestLoggerFactory loggerFactory) {
        this.data = data;
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<Location> loc = instruction.get(Argument.LOCATION);
        final Variable<Number> range = instruction.get(Argument.NUMBER);
        final BetonQuestLogger log = loggerFactory.create(LocationCondition.class);
        return new PrimaryServerThreadPlayerCondition(
                new OnlineConditionAdapter(new LocationCondition(loc, range), log, instruction.getPackage()), data);
    }
}
