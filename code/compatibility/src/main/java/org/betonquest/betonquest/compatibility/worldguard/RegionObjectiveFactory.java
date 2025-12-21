package org.betonquest.betonquest.compatibility.worldguard;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

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
        final Variable<String> name = instruction.get(instruction.getParsers().string());
        return new RegionObjective(instruction, name);
    }
}
