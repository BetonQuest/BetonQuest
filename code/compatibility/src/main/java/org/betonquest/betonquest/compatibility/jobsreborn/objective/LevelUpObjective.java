package org.betonquest.betonquest.compatibility.jobsreborn.objective;

import com.gamingmesh.jobs.api.JobsLevelUpEvent;
import com.gamingmesh.jobs.container.Job;
import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;

/**
 * Objective that tracks the level up of a player in a specific job.
 */
public class LevelUpObjective extends DefaultObjective {

    /**
     * Job to level up.
     */
    private final Argument<Job> job;

    /**
     * Constructor for the LevelUpObjective.
     *
     * @param service the objective factory service
     * @param job     the job to level up
     * @throws QuestException if the instruction is invalid
     */
    public LevelUpObjective(final ObjectiveFactoryService service, final Argument<Job> job) throws QuestException {
        super(service);
        this.job = job;
    }

    /**
     * Handles the JobsLevelUpEvent.
     *
     * @param event         the event that triggered the level up
     * @param onlineProfile the profile related to the player that leveled up
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onJobsLevelUpEvent(final JobsLevelUpEvent event, final OnlineProfile onlineProfile) throws QuestException {
        if (event.getJob().isSame(this.job.getValue(onlineProfile))) {
            getService().complete(onlineProfile);
        }
    }
}
