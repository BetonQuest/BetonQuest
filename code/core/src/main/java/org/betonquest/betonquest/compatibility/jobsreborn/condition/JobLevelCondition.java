package org.betonquest.betonquest.compatibility.jobsreborn.condition;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobProgression;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;

/**
 * Condition to check if the current player level in a job is in an interval.
 */
public class JobLevelCondition implements PlayerCondition {

    /**
     * Job to check.
     */
    private final Variable<Job> job;

    /**
     * Minimum level in job.
     */
    private final Variable<Number> nMinLevel;

    /**
     * Maximum level in job.
     */
    private final Variable<Number> nMaxLevel;

    /**
     * Create a new job level condition.
     *
     * @param job       the job to check
     * @param nMinLevel the minimum required level in the job
     * @param nMaxLevel the maximum required level in the job
     */
    public JobLevelCondition(final Variable<Job> job, final Variable<Number> nMinLevel, final Variable<Number> nMaxLevel) {
        this.job = job;
        this.nMinLevel = nMinLevel;
        this.nMaxLevel = nMaxLevel;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        final JobProgression progression = Jobs.getPlayerManager().getJobsPlayer(profile.getPlayerUUID()).getJobProgression(job.getValue(profile));
        return progression != null
                && progression.getLevel() >= nMinLevel.getValue(profile).intValue()
                && progression.getLevel() <= nMaxLevel.getValue(profile).intValue();
    }
}
