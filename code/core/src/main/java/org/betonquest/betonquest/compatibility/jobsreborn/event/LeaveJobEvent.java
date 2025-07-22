package org.betonquest.betonquest.compatibility.jobsreborn.event;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.instruction.variable.Variable;

/**
 * Event to leave a job.
 */
public class LeaveJobEvent implements PlayerEvent {

    /**
     * Job to join.
     */
    private final Variable<Job> job;

    /**
     * Create a new job join event.
     *
     * @param job the job to check
     */
    public LeaveJobEvent(final Variable<Job> job) {
        this.job = job;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        Jobs.getPlayerManager().getJobsPlayer(profile.getPlayerUUID()).leaveJob(job.getValue(profile));
    }
}
