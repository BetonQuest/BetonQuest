package org.betonquest.betonquest.quest.objective.die;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.bukkit.Location;

/**
 * Factory for creating {@link DieObjective} instances from {@link Instruction}s.
 */
public class DieObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the DieObjectiveFactory.
     */
    public DieObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final boolean cancel = instruction.hasArgument("cancel");
        final Variable<Location> location = instruction.get(instruction.getValue("respawn"), Argument.LOCATION);
        return new DieObjective(instruction, cancel, location);
    }
}
