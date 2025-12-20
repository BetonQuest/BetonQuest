package org.betonquest.betonquest.compatibility.jobsreborn.objective;

import com.gamingmesh.jobs.container.Job;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.JobParser;

/**
 * Factory for creating {@link LevelUpObjective} instances from {@link DefaultInstruction}s.
 */
public class LevelUpObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the ObjectiveLevelUpEventFactory.
     */
    public LevelUpObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final DefaultInstruction instruction) throws QuestException {
        final Variable<Job> job = instruction.get(JobParser.JOB);
        return new LevelUpObjective(instruction, job);
    }
}
