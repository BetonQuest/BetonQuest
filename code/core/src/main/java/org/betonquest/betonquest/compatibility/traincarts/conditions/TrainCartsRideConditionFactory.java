package org.betonquest.betonquest.compatibility.traincarts.conditions;

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

/**
 * Factory for the {@link TrainCartsRideCondition}.
 */
public class TrainCartsRideConditionFactory implements PlayerConditionFactory {
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
    public TrainCartsRideConditionFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<String> trainName = instruction.get(Argument.STRING);
        final BetonQuestLogger logger = loggerFactory.create(TrainCartsRideCondition.class);
        return new PrimaryServerThreadPlayerCondition(new OnlineConditionAdapter(new TrainCartsRideCondition(trainName),
                logger, instruction.getPackage()), data);
    }
}
