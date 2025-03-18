package org.betonquest.betonquest.compatibility.jobsreborn.condition;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobProgression;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

/**
 * Condition to check if the current player level in a job is in an interval.
 */
public class ConditionJobLevel implements PlayerCondition {

    /**
     * Job to check.
     */
    private final Job job;

    /**
     * Minimum level in job.
     */
    private final VariableNumber nMinLevel;

    /**
     * Maximum level in job.
     */
    private final VariableNumber nMaxLevel;

    /**
     * Create a new job level condition.
     *
     * @param job       the job to check
     * @param nMinLevel the minimum required level in the job
     * @param nMaxLevel the maximum required level in the job
     */
    public ConditionJobLevel(final Job job, final VariableNumber nMinLevel, final VariableNumber nMaxLevel) {
        this.job = job;
        this.nMinLevel = nMinLevel;
        this.nMaxLevel = nMaxLevel;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        final JobProgression progression = Jobs.getPlayerManager().getJobsPlayer(profile.getPlayerUUID()).getJobProgression(job);
        return progression != null
                && progression.getLevel() >= nMinLevel.getValue(profile).intValue()
                && progression.getLevel() <= nMaxLevel.getValue(profile).intValue();
    }
}
