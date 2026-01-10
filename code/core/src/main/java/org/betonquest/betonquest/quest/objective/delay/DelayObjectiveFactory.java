package org.betonquest.betonquest.quest.objective.delay;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;

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
    public Objective parseInstruction(final Instruction instruction, final ObjectiveService service) throws QuestException {
        final Argument<Number> delay = instruction.number().atLeast(0).get();
        final Argument<Number> interval = instruction.number()
                .atLeast(1).get("interval", 20 * 10);
        final FlagArgument<Boolean> ticks = instruction.bool().getFlag("ticks", true);
        final FlagArgument<Boolean> seconds = instruction.bool().getFlag("seconds", true);
        return new DelayObjective(service, interval, delay, ticks, seconds);
    }
}
