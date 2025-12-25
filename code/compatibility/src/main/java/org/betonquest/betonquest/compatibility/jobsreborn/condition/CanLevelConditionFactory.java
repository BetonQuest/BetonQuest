package org.betonquest.betonquest.compatibility.jobsreborn.condition;

import com.gamingmesh.jobs.container.Job;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.JobParser;

/**
 * Factory to create {@link CanLevelCondition}s from {@link Instruction}s.
 */
public class CanLevelConditionFactory implements PlayerConditionFactory {

    /**
     * Create a new Factory to create Can Level Conditions.
     */
    public CanLevelConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Job> job = instruction.parse(JobParser.JOB).get();
        return new CanLevelCondition(job);
    }
}
