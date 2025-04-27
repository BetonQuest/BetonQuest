package org.betonquest.betonquest.compatibility.jobsreborn.objective;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.VariableJob;
import org.betonquest.betonquest.instruction.Instruction;

/**
 * Factory for creating {@link JoinJobObjective} instances from {@link Instruction}s.
 */
public class LeaveJobObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the ObjectiveLeaveJobFactory.
     */
    public LeaveJobObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final VariableJob job = instruction.get(VariableJob::new);
        return new LeaveJobObjective(instruction, job);
    }
}
