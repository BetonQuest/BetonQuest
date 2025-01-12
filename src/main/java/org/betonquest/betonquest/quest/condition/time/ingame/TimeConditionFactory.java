package org.betonquest.betonquest.quest.condition.time.ingame;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.location.VariableWorld;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerlessCondition;
import org.betonquest.betonquest.quest.condition.ThrowExceptionPlayerlessCondition;
import org.betonquest.betonquest.quest.condition.time.TimeFrame;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;

/**
 * Factory to create test for time conditions from {@link Instruction}s.
 */
public class TimeConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * Data used for condition check on the primary server thread.
     */
    private final PrimaryServerThreadData data;

    /**
     * The variable processor used to process variables.
     */
    private final VariableProcessor variableProcessor;

    /**
     * Create the test for time condition factory.
     *
     * @param data              the data used for checking the condition on the main thread
     * @param variableProcessor the variable processor used to process variables
     */
    public TimeConditionFactory(final PrimaryServerThreadData data, final VariableProcessor variableProcessor) {
        this.data = data;
        this.variableProcessor = variableProcessor;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final TimeFrame timeFrame = TimeFrame.parse(instruction.next());
        final VariableWorld world = new VariableWorld(variableProcessor, instruction.getPackage(), instruction.getOptional("world", "%location.world%"));
        return new PrimaryServerThreadPlayerCondition(
                new NullableConditionAdapter(new TimeCondition(timeFrame, world)), data);
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        final String worldString = instruction.getOptional("world");
        if (worldString == null) {
            return new ThrowExceptionPlayerlessCondition();
        }
        final TimeFrame timeFrame = TimeFrame.parse(instruction.next());
        final VariableWorld world = new VariableWorld(variableProcessor, instruction.getPackage(), worldString);
        return new PrimaryServerThreadPlayerlessCondition(
                new NullableConditionAdapter(new TimeCondition(timeFrame, world)), data);
    }
}
