package org.betonquest.betonquest.quest.condition.number;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;

/**
 * The condition factory for the number compare condition.
 */
public class NumberCompareConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * Creates the number compare condition factory.
     */
    public NumberCompareConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        return new NullableConditionAdapter(parse(instruction));
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        return new NullableConditionAdapter(parse(instruction));
    }

    private NumberCompareCondition parse(final Instruction instruction) throws QuestException {
        final Argument<Number> first = instruction.number().get();
        final Operation operation = instruction.parse(Operation::fromSymbol).get().getValue(null);
        final Argument<Number> second = instruction.number().get();
        return new NumberCompareCondition(first, second, operation);
    }
}
