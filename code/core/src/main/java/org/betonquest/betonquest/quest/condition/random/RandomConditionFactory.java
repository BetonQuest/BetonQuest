package org.betonquest.betonquest.quest.condition.random;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;

import java.util.Random;

/**
 * Factory to create random conditions from {@link Instruction}s.
 */
public class RandomConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * Create the random condition factory.
     */
    public RandomConditionFactory() {
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
        final Variable<Number> valueMax;
        final Variable<Number> rangeOfRandom;
        try {
            valueMax = instruction.get(values[0], Argument.NUMBER);
            rangeOfRandom = instruction.get(values[1], Argument.NUMBER);
        } catch (final QuestException e) {
            throw new QuestException("Cannot parse randomness values: " + e.getMessage(), e);
        }
        return new RandomCondition(new Random(), valueMax, rangeOfRandom);
    }
}
