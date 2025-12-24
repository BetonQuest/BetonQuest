package org.betonquest.betonquest.quest.objective.step;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.BlockSelector;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.util.DefaultBlockSelector;
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
        final Variable<Location> loc = instruction.location().get();
        final BlockSelector selector = new DefaultBlockSelector(".*_PRESSURE_PLATE");
        return new StepObjective(instruction, loc, selector);
    }
}
