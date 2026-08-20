package org.betonquest.betonquest.compatibility.jobsreborn.action;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.PlayerAction;

/**
 * Action to add job experience.
 */
public class AddExpAction implements PlayerAction {

    /**
     * Job to add experience to.
     */
    private final Argument<Job> job;

    /**
     * Amount to add.
     */
    private final Argument<Number> nAddExperience;

    /**
     * Create a new experience add action.
     *
     * @param job    the job to add experience to
     * @param amount the experience amount
     */
    public AddExpAction(final Argument<Job> job, final Argument<Number> amount) {
        this.job = job;
        this.nAddExperience = amount;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(profile.getPlayerUUID());
        if (jobsPlayer != null) {
            final JobProgression progression = jobsPlayer.getJobProgression(job.getValue(profile));
            if (progression != null) {
                progression.addExperience(nAddExperience.getValue(profile).doubleValue());
            }
        }
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
