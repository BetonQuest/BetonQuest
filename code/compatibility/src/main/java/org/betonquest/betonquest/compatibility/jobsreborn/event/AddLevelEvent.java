package org.betonquest.betonquest.compatibility.jobsreborn.event;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobProgression;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.PlayerAction;

/**
 * Event to add job level.
 */
public class AddLevelEvent implements PlayerAction {

    /**
     * Job to add experience to.
     */
    private final Argument<Job> job;

    /**
     * Amount to add.
     */
    private final Argument<Number> nAddLevel;

    /**
     * Create a new level add event.
     *
     * @param job    the job to add level to
     * @param amount the level amount
     */
    public AddLevelEvent(final Argument<Job> job, final Argument<Number> amount) {
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

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
