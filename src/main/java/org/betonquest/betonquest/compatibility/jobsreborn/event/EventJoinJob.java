package org.betonquest.betonquest.compatibility.jobsreborn.event;

import com.gamingmesh.jobs.Jobs;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.compatibility.jobsreborn.VariableJob;

/**
 * Event to join a job.
 */
public class EventJoinJob implements Event {

    /**
     * Job to join.
     */
    private final VariableJob job;

    /**
     * Create a new job join event.
     *
     * @param job the job to check
     */
    public EventJoinJob(final VariableJob job) {
        this.job = job;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        Jobs.getPlayerManager().getJobsPlayer(profile.getPlayerUUID()).joinJob(job.getValue(profile));
    }
}
