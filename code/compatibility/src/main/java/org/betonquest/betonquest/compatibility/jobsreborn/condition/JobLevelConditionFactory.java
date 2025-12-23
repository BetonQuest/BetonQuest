package org.betonquest.betonquest.compatibility.jobsreborn.condition;

import com.gamingmesh.jobs.container.Job;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.JobParser;

/**
 * Factory to create {@link JobLevelCondition}s from {@link Instruction}s.
 */
public class JobLevelConditionFactory implements PlayerConditionFactory {

    /**
     * Create a new Factory to create Can Level Conditions.
     */
    public JobLevelConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<Job> job = instruction.get(JobParser.JOB);
        final Variable<Number> minimum = instruction.get(instruction.getParsers().number());
        final Variable<Number> maximum = instruction.get(instruction.getParsers().number());
        return new JobLevelCondition(job, minimum, maximum);
    }
}
