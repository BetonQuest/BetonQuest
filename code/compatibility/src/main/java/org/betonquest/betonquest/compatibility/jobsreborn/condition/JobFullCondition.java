package org.betonquest.betonquest.compatibility.jobsreborn.condition;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;

/**
 * Condition to check if a player has the max slots in a job reached.
 */
public class JobFullCondition implements PlayerCondition {

    /**
     * Job to check.
     */
    private final Argument<Job> job;

    /**
     * Create a new job full condition.
     *
     * @param job the job to check
     */
    public JobFullCondition(final Argument<Job> job) {
        this.job = job;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        final Job resolvedJob = this.job.getValue(profile);
        for (final Job job : Jobs.getJobs()) {
            if (job.isSame(resolvedJob)) {
                if (job.getMaxSlots() == null) {
                    return false;
                }
                if (job.getTotalPlayers() >= job.getMaxSlots()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
