package org.betonquest.betonquest.compatibility.jobsreborn.objective;

import com.gamingmesh.jobs.container.Job;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.JobParser;

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
        final Variable<Job> job = instruction.get(JobParser.JOB);
        return new LeaveJobObjective(instruction, job);
    }
}
