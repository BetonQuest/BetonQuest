package org.betonquest.betonquest.quest.condition.moon;

import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.location.VariableWorld;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerlessCondition;
import org.betonquest.betonquest.quest.condition.ThrowExceptionPlayerlessCondition;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;

/**
 * Factory to create moon cycle conditions from {@link Instruction}s.
 */
public class MoonCycleConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * Data used for condition check on the primary server thread.
     */
    private final PrimaryServerThreadData data;

    /**
     * The variable processor used to process variables.
     */
    private final VariableProcessor variableProcessor;

    /**
     * Create the weather condition factory.
     *
     * @param data              the data used for checking the condition on the main thread
     * @param variableProcessor the variable processor used to process variables
     */
    public MoonCycleConditionFactory(final PrimaryServerThreadData data, final VariableProcessor variableProcessor) {
        this.data = data;
        this.variableProcessor = variableProcessor;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final VariableNumber moonCycle = instruction.get(VariableNumber::new);
        final VariableWorld world = new VariableWorld(variableProcessor, instruction.getPackage(), instruction.getOptional("world", "%location.world%"));
        return new PrimaryServerThreadPlayerCondition(
                new NullableConditionAdapter(new MoonCycleCondition(world, moonCycle)), data);
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        final String worldString = instruction.getOptional("world");
        if (worldString == null) {
            return new ThrowExceptionPlayerlessCondition();
        }
        final VariableNumber moonCycle = instruction.get(VariableNumber::new);
        final VariableWorld world = new VariableWorld(variableProcessor, instruction.getPackage(), worldString);
        return new PrimaryServerThreadPlayerlessCondition(
                new NullableConditionAdapter(new MoonCycleCondition(world, moonCycle)), data);
    }
}
