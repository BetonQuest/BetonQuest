package org.betonquest.betonquest.quest.objective.arrow;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.bukkit.Location;

/**
 * Factory for creating {@link ArrowShootObjective} instances from {@link Instruction}s.
 */
public class ArrowShootObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the ArrowShootObjectiveFactory.
     */
    public ArrowShootObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Variable<Location> location = instruction.location().get();
        final Variable<Number> range = instruction.number().get();
        return new ArrowShootObjective(instruction, location, range);
    }
}
