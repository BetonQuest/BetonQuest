package org.betonquest.betonquest.compatibility.jobsreborn.condition;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;

/**
 * Condition to check if the player has a job.
 */
public class HasJobCondition implements PlayerCondition {

    /**
     * Job to check.
     */
    private final Variable<Job> job;

    /**
     * Create a new has job condition.
     *
     * @param job the job to check
     */
    public HasJobCondition(final Variable<Job> job) {
        this.job = job;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        return Jobs.getPlayerManager().getJobsPlayer(profile.getPlayerUUID()).getJobProgression(job.getValue(profile)) != null;
    }
}
