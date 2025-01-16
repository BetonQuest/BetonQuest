package org.betonquest.betonquest.quest.condition.number;

import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

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
        final VariableNumber first = instruction.get(VariableNumber::new);
        final Operation operation = Operation.fromSymbol(instruction.next());
        final VariableNumber second = instruction.get(VariableNumber::new);
        return new NumberCompareCondition(first, second, operation);
    }
}
