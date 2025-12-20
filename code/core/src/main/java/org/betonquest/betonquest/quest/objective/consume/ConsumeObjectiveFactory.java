package org.betonquest.betonquest.quest.objective.consume;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.instruction.Item;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.argument.InstructionIdentifierArgument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

/**
 * Factory for creating {@link ConsumeObjective} instances from {@link DefaultInstruction}s.
 */
public class ConsumeObjectiveFactory implements ObjectiveFactory {

    /**
     * The name of the argument that determines the amount of items to consume.
     */
    public static final String AMOUNT_ARGUMENT = "amount";

    /**
     * Constructs a new ConsumeObjectiveFactory.
     */
    public ConsumeObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final DefaultInstruction instruction) throws QuestException {
        final Variable<Item> item = instruction.get(InstructionIdentifierArgument.ITEM);
        final Variable<Number> targetAmount = instruction.getValue(AMOUNT_ARGUMENT, Argument.NUMBER_NOT_LESS_THAN_ONE, 1);
        return new ConsumeObjective(instruction, targetAmount, item);
    }
}
