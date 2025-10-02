package org.betonquest.betonquest.quest.condition.number;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.QuestException;
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
        final Variable<Number> first = instruction.get(Argument.NUMBER);
        final Operation operation = instruction.get(Operation::fromSymbol).getValue(null);
        final Variable<Number> second = instruction.get(Argument.NUMBER);
        return new NumberCompareCondition(first, second, operation);
    }
}
