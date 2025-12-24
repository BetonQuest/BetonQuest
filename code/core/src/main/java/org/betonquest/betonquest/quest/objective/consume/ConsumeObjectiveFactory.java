package org.betonquest.betonquest.quest.objective.consume;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

/**
 * Factory for creating {@link ConsumeObjective} instances from {@link Instruction}s.
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
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Variable<ItemWrapper> item = instruction.item().get();
        final Variable<Number> targetAmount = instruction.number().atLeast(1).get(AMOUNT_ARGUMENT, 1);
        return new ConsumeObjective(instruction, targetAmount, item);
    }
}
