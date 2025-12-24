package org.betonquest.betonquest.quest.condition.random;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;

import java.util.concurrent.ThreadLocalRandom;

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
        final Variable<RandomChance> randomChanceVariable = instruction.parse(RandomChanceParser.CHANCE).get();
        return new RandomCondition(ThreadLocalRandom::current, randomChanceVariable);
    }
}
