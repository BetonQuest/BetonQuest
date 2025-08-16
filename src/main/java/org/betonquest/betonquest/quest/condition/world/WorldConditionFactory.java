package org.betonquest.betonquest.quest.condition.world;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;
import org.bukkit.World;

/**
 * Factory to create world conditions from {@link Instruction}s.
 */
public class WorldConditionFactory implements PlayerConditionFactory {

    /**
     * Logger factory to create a logger for the conditions.
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
     * Create the test for block condition factory.
     *
     * @param loggerFactory     the logger factory to create a logger for the conditions
     * @param data              the data used for checking the condition on the main thread
     * @param variableProcessor the processor to create new variables
     */
    public WorldConditionFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data, final VariableProcessor variableProcessor) {
        this.loggerFactory = loggerFactory;
        this.data = data;
        this.variableProcessor = variableProcessor;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<World> world = new Variable<>(variableProcessor, instruction.getPackage(), instruction.next(), Argument.WORLD);
        final BetonQuestLogger logger = loggerFactory.create(WorldCondition.class);
        return new PrimaryServerThreadPlayerCondition(
                new OnlineConditionAdapter(new WorldCondition(world), logger, instruction.getPackage()), data);
    }
}
