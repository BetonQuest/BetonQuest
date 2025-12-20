package org.betonquest.betonquest.compatibility.worldguard;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

/**
 * Factory for creating {@link RegionObjective} instances from {@link DefaultInstruction}s.
 */
public class RegionObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the RegionObjectiveFactory.
     */
    public RegionObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final DefaultInstruction instruction) throws QuestException {
        final Variable<String> name = instruction.get(Argument.STRING);
        return new RegionObjective(instruction, name);
    }
}
