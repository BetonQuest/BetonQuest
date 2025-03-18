package org.betonquest.betonquest.compatibility.jobsreborn.condition;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;

/**
 * Condition to check if the player has a job.
 */
public class ConditionHasJob implements PlayerCondition {

    /**
     * Job to check.
     */
    private final Job job;

    /**
     * Create a new has job condition.
     *
     * @param job the job to check
     */
    public ConditionHasJob(final Job job) {
        this.job = job;
    }

    @Override
    public boolean check(final Profile profile) {
        return Jobs.getPlayerManager().getJobsPlayer(profile.getPlayerUUID()).getJobProgression(job) != null;
    }
}
