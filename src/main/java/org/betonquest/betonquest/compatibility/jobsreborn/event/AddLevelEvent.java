package org.betonquest.betonquest.compatibility.jobsreborn.event;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.JobProgression;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.compatibility.jobsreborn.VariableJob;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

/**
 * Event to add job level.
 */
public class AddLevelEvent implements PlayerEvent {

    /**
     * Job to add experience to.
     */
    private final VariableJob job;

    /**
     * Amount to add.
     */
    private final VariableNumber nAddLevel;

    /**
     * Create a new level add event.
     *
     * @param job    the job to add level to
     * @param amount the level amount
     */
    public AddLevelEvent(final VariableJob job, final VariableNumber amount) {
        this.job = job;
        this.nAddLevel = amount;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final JobProgression progression = Jobs.getPlayerManager().getJobsPlayer(profile.getPlayerUUID()).getJobProgression(job.getValue(profile));
        if (progression != null) {
            progression.setLevel(progression.getLevel() + nAddLevel.getValue(profile).intValue());
        }
    }
}
