package org.betonquest.betonquest.quest.objective.arrow;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.bukkit.Location;

/**
 * Factory for creating {@link ArrowShootObjective} instances from {@link DefaultInstruction}s.
 */
public class ArrowShootObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the ArrowShootObjectiveFactory.
     */
    public ArrowShootObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final DefaultInstruction instruction) throws QuestException {
        final Variable<Location> location = instruction.get(Argument.LOCATION);
        final Variable<Number> range = instruction.get(Argument.NUMBER);
        return new ArrowShootObjective(instruction, location, range);
    }
}
