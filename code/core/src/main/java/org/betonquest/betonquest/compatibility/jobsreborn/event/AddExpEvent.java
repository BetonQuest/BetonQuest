package org.betonquest.betonquest.compatibility.jobsreborn.event;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobProgression;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;

/**
 * Event to add job experience.
 */
public class AddExpEvent implements PlayerEvent {
    /**
     * Job to add experience to.
     */
    private final Variable<Job> job;

    /**
     * Amount to add.
     */
    private final Variable<Number> nAddExperience;

    /**
     * Create a new experience add event.
     *
     * @param job    the job to add experience to
     * @param amount the experience amount
     */
    public AddExpEvent(final Variable<Job> job, final Variable<Number> amount) {
        this.job = job;
        this.nAddExperience = amount;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final JobProgression progression = Jobs.getPlayerManager().getJobsPlayer(profile.getPlayerUUID()).getJobProgression(job.getValue(profile));
        if (progression != null) {
            progression.addExperience(nAddExperience.getValue(profile).doubleValue());
        }
    }
}
