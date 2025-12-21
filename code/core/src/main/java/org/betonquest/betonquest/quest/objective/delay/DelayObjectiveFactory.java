package org.betonquest.betonquest.quest.objective.delay;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.DecoratedArgument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

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
        final DecoratedArgument<Number> delayParser = instruction.getParsers().number()
                .validate(value -> value.doubleValue() < 0, "Delay must be a non-negative number, got: '%s'");
        final DecoratedArgument<Number> intervalParser = instruction.getParsers().number()
                .validate(value -> value.doubleValue() < 1, "Interval must be at least 1, got: '%s'");
        final Variable<Number> delay = instruction.get(delayParser);
        final Variable<Number> interval = instruction.getValue("interval", intervalParser, 20 * 10);
        return new DelayObjective(instruction, interval, delay);
    }
}
