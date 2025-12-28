package org.betonquest.betonquest.quest.objective.delay;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
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
    public DefaultObjective parseInstruction(final Instruction instruction) throws QuestException {
        final Argument<Number> delay = instruction.number().atLeast(0).get();
        final Argument<Number> interval = instruction.number()
                .atLeast(1).get("interval", 20 * 10);
        return new DelayObjective(instruction, interval, delay);
    }
}
