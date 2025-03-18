package org.betonquest.betonquest.compatibility.jobsreborn.event;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.event.Event;

/**
 * Event to leave a job.
 */
public class EventLeaveJob implements Event {

    /**
     * Job to join.
     */
    private final Job job;

    /**
     * Create a new job join event.
     *
     * @param job the job to check
     */
    public EventLeaveJob(final Job job) {
        this.job = job;
    }

    @Override
    public void execute(final Profile profile) {
        Jobs.getPlayerManager().getJobsPlayer(profile.getPlayerUUID()).leaveJob(job);
    }
}
