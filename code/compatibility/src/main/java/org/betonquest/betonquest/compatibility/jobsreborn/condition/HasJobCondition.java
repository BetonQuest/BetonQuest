package org.betonquest.betonquest.compatibility.jobsreborn.condition;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;

/**
 * Condition to check if the player has a job.
 */
public class HasJobCondition implements PlayerCondition {

    /**
     * Job to check.
     */
    private final Argument<Job> job;

    /**
     * Create a new has job condition.
     *
     * @param job the job to check
     */
    public HasJobCondition(final Argument<Job> job) {
        this.job = job;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        final JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(profile.getPlayerUUID());
        return jobsPlayer != null && jobsPlayer.getJobProgression(job.getValue(profile)) != null;
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
