package org.betonquest.betonquest.compatibility.traincarts.conditions;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;

/**
 * Factory for the {@link TrainCartsRideCondition}.
 */
public class TrainCartsRideConditionFactory implements PlayerConditionFactory {

    /**
     * Logger factory to create a logger for condition.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the ride condition factory.
     *
     * @param loggerFactory the logger factory to create a logger for the condition
     */
    public TrainCartsRideConditionFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<String> trainName = instruction.string().get();
        final BetonQuestLogger logger = loggerFactory.create(TrainCartsRideCondition.class);
        return new OnlineConditionAdapter(new TrainCartsRideCondition(trainName), logger, instruction.getPackage());
    }
}
