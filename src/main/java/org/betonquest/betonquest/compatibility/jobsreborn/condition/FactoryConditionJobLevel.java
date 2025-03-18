package org.betonquest.betonquest.compatibility.jobsreborn.condition;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;
import org.betonquest.betonquest.util.Utils;

/**
 * Factory to create {@link ConditionJobLevel}s from {@link Instruction}s.
 */
public class FactoryConditionJobLevel implements PlayerConditionFactory {
    /**
     * The data for the primary server thread.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new Factory to create Can Level Conditions.
     *
     * @param data the data for the primary server thread.
     */
    public FactoryConditionJobLevel(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final String jobName = instruction.next();
        final Job job = Utils.getNN(Jobs.getJob(jobName), "Jobs Reborn job \"" + jobName + "\" does not exist");
        final VariableNumber minimum = instruction.get(VariableNumber::new);
        final VariableNumber maximum = instruction.get(VariableNumber::new);
        return new PrimaryServerThreadPlayerCondition(new ConditionJobLevel(job, minimum, maximum), data);
    }
}
