package org.betonquest.betonquest.compatibility.jobsreborn.event;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.PlayerAction;

/**
 * Event to leave a job.
 */
public class LeaveJobAction implements PlayerAction {

    /**
     * Job to join.
     */
    private final Argument<Job> job;

    /**
     * Create a new job join event.
     *
     * @param job the job to check
     */
    public LeaveJobAction(final Argument<Job> job) {
        this.job = job;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        Jobs.getPlayerManager().getJobsPlayer(profile.getPlayerUUID()).leaveJob(job.getValue(profile));
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
