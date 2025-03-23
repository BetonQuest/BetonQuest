package org.betonquest.betonquest.compatibility.jobsreborn.event;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.JobProgression;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.compatibility.jobsreborn.VariableJob;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

/**
 * Event to add job experience.
 */
public class EventAddExp implements PlayerEvent {
    /**
     * Job to add experience to.
     */
    private final VariableJob job;

    /**
     * Amount to add.
     */
    private final VariableNumber nAddExperience;

    /**
     * Create a new experience add event.
     *
     * @param job    the job to add experience to
     * @param amount the experience amount
     */
    public EventAddExp(final VariableJob job, final VariableNumber amount) {
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
