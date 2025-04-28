package org.betonquest.betonquest.quest.condition.moon;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerlessCondition;
import org.betonquest.betonquest.quest.condition.ThrowExceptionPlayerlessCondition;
import org.bukkit.World;

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
        final Variable<Number> moonCycle = instruction.get(Argument.NUMBER);
        final Variable<World> world = new Variable<>(variableProcessor, instruction.getPackage(), instruction.getValue("world", "%location.world%"),
                Argument.WORLD);
        return new PrimaryServerThreadPlayerCondition(
                new NullableConditionAdapter(new MoonCycleCondition(world, moonCycle)), data);
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        final String worldString = instruction.getValue("world");
        if (worldString == null) {
            return new ThrowExceptionPlayerlessCondition();
        }
        final Variable<Number> moonCycle = instruction.get(Argument.NUMBER);
        final Variable<World> world = new Variable<>(variableProcessor, instruction.getPackage(), worldString, Argument.WORLD);
        return new PrimaryServerThreadPlayerlessCondition(
                new NullableConditionAdapter(new MoonCycleCondition(world, moonCycle)), data);
    }
}
