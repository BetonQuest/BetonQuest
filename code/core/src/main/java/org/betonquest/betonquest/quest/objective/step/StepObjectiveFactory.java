package org.betonquest.betonquest.quest.objective.step;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.util.BlockSelector;
import org.bukkit.Location;

/**
 * Factory for creating {@link StepObjective} instances from {@link Instruction}s.
 */
public class StepObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new StepObjectiveFactory instance.
     */
    public StepObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Variable<Location> loc = instruction.get(Argument.LOCATION);
        final BlockSelector selector = new BlockSelector(".*_PRESSURE_PLATE");
        return new StepObjective(instruction, loc, selector);
    }
}
