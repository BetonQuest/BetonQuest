package org.betonquest.betonquest.quest.objective.step;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.BlockSelector;
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
    public DefaultObjective parseInstruction(final Instruction instruction) throws QuestException {
        final Argument<Location> loc = instruction.location().get();
        final BlockSelector selector = new DefaultBlockSelector(".*_PRESSURE_PLATE");
        return new StepObjective(instruction, loc, selector);
    }
}
