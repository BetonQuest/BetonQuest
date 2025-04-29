package org.betonquest.betonquest.compatibility.worldguard;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;

/**
 * Factory for creating {@link RegionObjective} instances from {@link Instruction}s.
 */
public class RegionObjectiveFactory implements ObjectiveFactory {
    /**
     * Creates a new instance of the RegionObjectiveFactory.
     */
    public RegionObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Variable<String> name = instruction.get(Argument.STRING);
        return new RegionObjective(instruction, name);
    }
}
