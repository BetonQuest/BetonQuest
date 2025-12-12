package org.betonquest.betonquest.compatibility.jobsreborn.event;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobProgression;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;

/**
 * Event to reduce job level.
 */
public class DelLevelEvent implements PlayerEvent {
    /**
     * Job to add experience to.
     */
    private final Variable<Job> job;

    /**
     * Amount to remove.
     */
    private final Variable<Number> nAddLevel;

    /**
     * Create a new level delete event.
     * The set level has a minimum of 1.
     *
     * @param job    the job to remove level from
     * @param amount the level amount
     */
    public DelLevelEvent(final Variable<Job> job, final Variable<Number> amount) {
        this.job = job;
        this.nAddLevel = amount;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final JobProgression progression = Jobs.getPlayerManager().getJobsPlayer(profile.getPlayerUUID()).getJobProgression(job.getValue(profile));
        if (progression != null) {
            final int newLevel = progression.getLevel() - nAddLevel.getValue(profile).intValue();
            progression.setLevel(Math.max(1, newLevel));
        }
    }
}
