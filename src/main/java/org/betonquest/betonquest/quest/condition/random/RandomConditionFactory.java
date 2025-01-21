package org.betonquest.betonquest.quest.condition.random;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;

import java.util.Random;

/**
 * Factory to create random conditions from {@link Instruction}s.
 */
public class RandomConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * The variable processor used to process variables.
     */
    private final VariableProcessor variableProcessor;

    /**
     * Create the random condition factory.
     *
     * @param variableProcessor the variable processor used to process variables
     */
    public RandomConditionFactory(final VariableProcessor variableProcessor) {
        this.variableProcessor = variableProcessor;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        return new NullableConditionAdapter(parse(instruction));
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        return new NullableConditionAdapter(parse(instruction));
    }

    private RandomCondition parse(final Instruction instruction) throws QuestException {
        final String[] values = instruction.next().split("-");
        final int expectedLength = 2;
        if (values.length != expectedLength) {
            throw new QuestException("Wrong randomness format. Use <chance>-<max>");
        }
        final VariableNumber valueMax;
        final VariableNumber rangeOfRandom;
        try {
            valueMax = new VariableNumber(variableProcessor, instruction.getPackage(), values[0]);
            rangeOfRandom = new VariableNumber(variableProcessor, instruction.getPackage(), values[1]);
        } catch (final QuestException e) {
            throw new QuestException("Cannot parse randomness values", e);
        }
        return new RandomCondition(new Random(), valueMax, rangeOfRandom);
    }
}
