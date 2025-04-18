package org.betonquest.betonquest.compatibility.jobsreborn.condition;

import com.gamingmesh.jobs.Jobs;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.compatibility.jobsreborn.VariableJob;

/**
 * Condition to check if the player has a job.
 */
public class HasJobCondition implements PlayerCondition {

    /**
     * Job to check.
     */
    private final VariableJob job;

    /**
     * Create a new has job condition.
     *
     * @param job the job to check
     */
    public HasJobCondition(final VariableJob job) {
        this.job = job;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        return Jobs.getPlayerManager().getJobsPlayer(profile.getPlayerUUID()).getJobProgression(job.getValue(profile)) != null;
    }
}
