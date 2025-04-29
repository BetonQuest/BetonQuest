package org.betonquest.betonquest.quest.objective.location;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.bukkit.Location;

/**
 * Factory for creating {@link LocationObjective} instances from {@link Instruction}s.
 */
public class LocationObjectiveFactory implements ObjectiveFactory {
    /**
     * Creates a new instance of the LocationObjectiveFactory.
     */
    public LocationObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Variable<Location> loc = instruction.get(Argument.LOCATION);
        final Variable<Number> range = instruction.get(Argument.NUMBER);
        return new LocationObjective(instruction, loc, range);
    }
}
