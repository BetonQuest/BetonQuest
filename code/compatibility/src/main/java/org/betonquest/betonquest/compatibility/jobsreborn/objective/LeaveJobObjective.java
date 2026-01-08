package org.betonquest.betonquest.compatibility.jobsreborn.objective;

import com.gamingmesh.jobs.api.JobsLeaveEvent;
import com.gamingmesh.jobs.container.Job;
import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;

/**
 * Objective that tracks the leave of a player from a specific job.
 */
public class LeaveJobObjective extends DefaultObjective {

    /**
     * Job to leave.
     */
    private final Argument<Job> job;

    /**
     * Constructor for the LeaveJobObjective.
     *
     * @param service the objective factory service
     * @param job     the job to leave
     * @throws QuestException if the instruction is invalid
     */
    public LeaveJobObjective(final ObjectiveFactoryService service, final Argument<Job> job) throws QuestException {
        super(service);
        this.job = job;
    }

    /**
     * Handles the JobsLeaveEvent.
     *
     * @param event         the event that triggered the leave
     * @param onlineProfile the profile related to the player that left the job
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onJobsLeaveEvent(final JobsLeaveEvent event, final OnlineProfile onlineProfile) throws QuestException {
        if (event.getJob().isSame(this.job.getValue(onlineProfile)) && containsPlayer(onlineProfile) && checkConditions(onlineProfile)) {
            completeObjective(onlineProfile);
        }
    }

    @Override
    public String getDefaultDataInstruction(final Profile profile) {
        return "";
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        return "";
    }
}
