package org.betonquest.betonquest.compatibility.jobsreborn.condition;

import com.gamingmesh.jobs.container.Job;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.JobParser;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;

/**
 * Factory to create {@link HasJobCondition}s from {@link Instruction}s.
 */
public class HasJobConditionFactory implements PlayerConditionFactory {
    /**
     * The data for the primary server thread.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new Factory to create Can Level Conditions.
     *
     * @param data the data for the primary server thread.
     */
    public HasJobConditionFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<Job> job = instruction.get(JobParser.JOB);
        return new PrimaryServerThreadPlayerCondition(new HasJobCondition(job), data);
    }
}
