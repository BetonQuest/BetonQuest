package org.betonquest.betonquest.quest.objective.delay;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;

/**
 * Factory for creating {@link DelayObjective} instances from {@link Instruction}s.
 */
public class DelayObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the DelayObjectiveFactory.
     */
    public DelayObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Variable<Number> delay = instruction.get(Argument.NUMBER_NOT_LESS_THAN_ZERO);
        final Variable<Number> interval = instruction.getValue("interval", Argument.NUMBER_NOT_LESS_THAN_ONE, 20 * 10);
        return new DelayObjective(instruction, interval, delay);
    }
}
