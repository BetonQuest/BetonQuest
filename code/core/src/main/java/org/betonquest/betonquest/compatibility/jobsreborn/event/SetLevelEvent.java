package org.betonquest.betonquest.compatibility.jobsreborn.event;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobProgression;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.instruction.variable.Variable;

/**
 * Event to set a job level.
 */
public class SetLevelEvent implements PlayerEvent {
    /**
     * Job to add experience to.
     */
    private final Variable<Job> job;

    /**
     * Amount to remove.
     */
    private final Variable<Number> nLevel;

    /**
     * Create a new level delete event.
     * The set level has a minimum of 1.
     *
     * @param job    the job to remove level from
     * @param amount the level amount
     */
    public SetLevelEvent(final Variable<Job> job, final Variable<Number> amount) {
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
}
