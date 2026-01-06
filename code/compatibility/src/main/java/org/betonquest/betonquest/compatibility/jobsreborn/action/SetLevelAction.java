package org.betonquest.betonquest.compatibility.jobsreborn.action;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobProgression;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.PlayerAction;

/**
 * Action to set a job level.
 */
public class SetLevelAction implements PlayerAction {

    /**
     * Job to add experience to.
     */
    private final Argument<Job> job;

    /**
     * Amount to remove.
     */
    private final Argument<Number> nLevel;

    /**
     * Create a new level delete action.
     * The set level has a minimum of 1.
     *
     * @param job    the job to remove level from
     * @param amount the level amount
     */
    public SetLevelAction(final Argument<Job> job, final Argument<Number> amount) {
        this.job = job;
        this.nLevel = amount;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final JobProgression progression = Jobs.getPlayerManager().getJobsPlayer(profile.getPlayerUUID()).getJobProgression(job.getValue(profile));
        if (progression != null) {
            progression.setLevel(Math.min(progression.getJob().getMaxLevel(), nLevel.getValue(profile).intValue()));
        }
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
